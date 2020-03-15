package wumpus

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object GameSpec extends Properties("Game") {
  property("init") = forAll { (a: Long) =>
    val g = Game.init(Seed(a))
    val rooms = Set(g.playerRoom, g.wumpusRoom) ++ g.pitRooms ++ g.batRooms
    rooms.size == 6
  }
}
