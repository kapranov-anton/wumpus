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
      if (hasBats) "bats nearby" else "",
      if (hasPits) "pits nearby" else "",
      if (hasWumpus) "wumpus nearby" else "",
      s"rooms: $rooms",
      s"arrows: ${g.arrowTotal}"
    ).filter(_.nonEmpty).mkString("\n")
  }

  def askDirection: IO[Option[Room]] =
    IO(StdIn.readLine("what direction: ")).map { input =>
      input.toIntOption.map(Room.apply)
    }

  def onQuit: IO[Unit] = IO.unit

  def update(
      game: Game,
      command: PlayerCommand,
      maybeDirection: Option[Room]
  ): Game =
    (command, maybeDirection) match {
      case (PlayerCommand.Move, None) => game
      case (PlayerCommand.Move, Some(direction)) =>
        game.copy(playerRoom = direction)
      case (PlayerCommand.Shoot, None) => game
      case (PlayerCommand.Shoot, Some(direction)) =>
        if (direction === game.wumpusRoom) game.copy(wumpusAlive = false)
        else game.copy(arrowTotal = game.arrowTotal - 1)
      case (PlayerCommand.Quit, _)    => game
      case (PlayerCommand.Unknown, _) => game
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
      input <- IO(StdIn.readLine("shoot or move [s/m/q]: "))
      command = PlayerCommand.fromString(input)
      _ <- IO(println(s">> $command <<"))
      maybeDirection <- if (command === PlayerCommand.Move || command === PlayerCommand.Shoot) {
        val adj = adjacentRooms(g)
        askDirection.map(_.filter(adj.contains))
      } else
        IO.pure(None)
      _ <- IO(println(maybeDirection))
      newGame = update(g, command, maybeDirection)
      _ <- if (!newGame.wumpusAlive) IO(println("you win"))
      else if (newGame.arrowTotal === 0 || newGame.pitRooms.contains(
                 newGame.playerRoom
               ) || newGame.wumpusRoom === newGame.playerRoom)
        IO(println("you died"))
      else
        IO(println(drawStatus(newGame)))
    } yield ()

  val program = for {
    start <- Clock[IO].monotonic(duration.MILLISECONDS)
    g = Game.init(Seed(start))
    _ <- IO(println(drawStatus(g)))
    _ <- loop(g)
  } yield ExitCode.Success

}
