package wumpus

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object GameSpec extends Properties("Game") {
  property("init") = forAll { (a: Long) =>
    val g = Game.init(Lang.en, Seed(a))
    g.nonEmptyRooms.size == 6
  }
}
