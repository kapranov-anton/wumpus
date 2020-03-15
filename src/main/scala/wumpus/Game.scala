package wumpus

import cats.Eq
import cats.implicits._

final case class Room(value: Int) extends AnyVal
object Room {
  val all: List[Room] = 1.to(20).map(Room.apply).toList
  implicit val eq: Eq[Room] = Eq.by[Room, Int](_.value)
}

final case class Edge(r1: Room, r2: Room) {
  def linked(r: Room): Option[Room] = r match {
    case `r1` => Some(r2)
    case `r2` => Some(r1)
    case _    => None
  }
}
final case class Game(
    seed: Seed,
    map: Set[Edge],
    wumpusRoom: Room,
    wumpusAlive: Boolean,
    pitRooms: Set[Room],
    batRooms: Set[Room],
    playerRoom: Room,
    arrowTotal: Int
)
object Game {
  def init(s: Seed): Game = {
    val shuffled = Random.shuffle(Room.all).run(s).value._2
    val List(wumpusRoom, pit1, pit2, bat1, bat2, player) = shuffled.take(6)
    Game(
      seed = s,
      map = GameMap.edges,
      wumpusRoom = wumpusRoom,
      wumpusAlive = true,
      pitRooms = Set(pit1, pit2),
      batRooms = Set(bat1, bat2),
      playerRoom = player,
      arrowTotal = 5
    )
  }

}

object GameMap {
  lazy val edges: Set[Edge] = edges_.map(e => Edge(Room(e._1), Room(e._2)))
  lazy val edges_ =
    Set(
      (1, 2),
      (2, 3),
      (3, 4),
      (4, 5),
      (5, 1),
      (6, 7),
      (7, 8),
      (8, 9),
      (9, 10),
      (10, 11),
      (11, 12),
      (12, 13),
      (13, 14),
      (14, 15),
      (15, 6),
      (16, 17),
      (17, 18),
      (18, 19),
      (19, 20),
      (20, 16),
      (1, 6),
      (2, 8),
      (3, 10),
      (4, 12),
      (5, 14),
      (7, 16),
      (9, 17),
      (11, 18),
      (13, 19),
      (15, 20)
    )
}
