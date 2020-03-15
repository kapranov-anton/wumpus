package wumpus

import cats.data.State
import cats.implicits._
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import scala.io.StdIn
import cats.effect.Clock
import scala.concurrent.duration

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = program

  def adjacentRooms(g: Game): List[Room] =
    g.map.toList.mapFilter(_.linked(g.playerRoom))

  def drawStatus(g: Game) = {
    val adj = adjacentRooms(g)
    val adjSet = adj.toSet
    val hasBats = g.batRooms.intersect(adjSet).nonEmpty
    val hasPits = g.pitRooms.intersect(adjSet).nonEmpty
    val hasWumpus = adj.contains(g.wumpusRoom)
    val rooms = adj.map(_.value.toString).mkString(", ")
    List(
      if (hasBats) g.lang.batsNearby else "",
      if (hasPits) g.lang.pitNearby else "",
      if (hasWumpus) g.lang.wumpusNearby else "",
      s"${g.lang.roomsNearby}: $rooms",
      s"${g.lang.arrows}: ${g.arrowTotal}"
    ).filter(_.nonEmpty).mkString("\n")
  }

  def askDirection(l: Lang): IO[Option[Room]] =
    IO(StdIn.readLine(s"${l.whatDirection}: ")).map { input =>
      input.toIntOption.map(Room.apply)
    }

  def onQuit: IO[Unit] = IO.unit

  def vacantRoom(game: Game): (Seed, Option[Room]) = {
    val vacantRooms = Room.all.toSet.removedAll(game.nonEmptyRooms).toList
    val (seed, rooms) = Random.shuffle(vacantRooms).run(game.seed).value
    seed -> rooms.headOption
  }

  def update(
      game: Game,
      command: PlayerCommand,
      maybeDirection: Option[Room]
  ): Game =
    (command, maybeDirection) match {
      case (PlayerCommand.Move, None) => game
      case (PlayerCommand.Move, Some(direction)) =>
        if (game.batRooms.contains(direction)) {
          val (seed, maybeRoom) = vacantRoom(game)
          val newRoom = maybeRoom.getOrElse(direction)
          game.copy(seed = seed, playerRoom = newRoom)
        } else
          game.copy(playerRoom = direction)
      case (PlayerCommand.Shoot, None) => game
      case (PlayerCommand.Shoot, Some(direction)) =>
        if (direction === game.wumpusRoom) game.copy(wumpusAlive = false)
        else {
          val (seed, maybeRoom) = vacantRoom(game)
          val newRoom = maybeRoom.getOrElse(game.wumpusRoom)
          game.copy(
            seed = seed,
            wumpusRoom = newRoom,
            arrowTotal = game.arrowTotal - 1
          )
        }
      case (PlayerCommand.Quit, _)    => game
      case (PlayerCommand.Unknown, _) => game
    }

  def drawTurnResult(
      game: Game,
      command: PlayerCommand,
      maybeDirection: Option[Room]
  ) =
    (command, maybeDirection) match {
      case (PlayerCommand.Move, None)  => game.lang.unknownDirection
      case (PlayerCommand.Shoot, None) => game.lang.unknownDirection
      case (PlayerCommand.Move, Some(direction)) =>
        if (direction === game.playerRoom)
          s"${game.lang.moveTo} ${game.playerRoom.value}"
        else
          s"${game.lang.batsMoveYou} ${game.playerRoom.value}"
      case (PlayerCommand.Shoot, Some(direction)) =>
        game.lang.missed
      case (PlayerCommand.Quit, _)    => game.lang.bye
      case (PlayerCommand.Unknown, _) => game.lang.unknownCommand
    }

  /**
    * win condition: Wumpus is killed
    * lose condition: (Wumpus is alive && Arrows == 0)
    *                 || Player in pit room
    *                 || Player in Wumpus room
    *
    */
  def loop(g: Game): IO[Unit] =
    for {
      _ <- IO(println("-" * 80))
      input <- IO(StdIn.readLine(s"${g.lang.shootOrMove} [s/m/q]: "))
      command = PlayerCommand.fromString(input)
      maybeDirection <- if (command === PlayerCommand.Move || command === PlayerCommand.Shoot) {
        val adj = adjacentRooms(g)
        askDirection(g.lang).map(_.filter(adj.contains))
      } else
        IO.pure(None)
      newGame = update(g, command, maybeDirection)
      _ <- if (!newGame.wumpusAlive) IO(println(g.lang.win))
      else if (newGame.wumpusRoom === newGame.playerRoom)
        IO(println(g.lang.wumpusKill))
      else if (newGame.pitRooms.contains(newGame.playerRoom))
        IO(println(g.lang.fell))
      else if (newGame.arrowTotal === 0)
        IO(println(g.lang.outOfArrows))
      else
        IO(println(drawTurnResult(newGame, command, maybeDirection))) *>
          IO(println(drawStatus(newGame))) *>
          (if (command =!= PlayerCommand.Quit) loop(newGame) else IO.unit)
    } yield ()

  val program = for {
    start <- Clock[IO].monotonic(duration.MILLISECONDS)
    g = Game.init(Lang.ru, Seed(start))
    _ <- IO(println(s"${g.lang.intro} ${g.playerRoom.value}"))
    _ <- IO(println(drawStatus(g)))
    _ <- loop(g)
  } yield ExitCode.Success

}
