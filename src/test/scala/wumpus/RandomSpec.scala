package wumpus

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object RandomSpec extends Properties("Random") {

  property("nextCap") = forAll { (a: Long, b: Long) =>
    val v = Random.nextCap(a).run(Seed(b)).value._2
    if (a >= 0) a >= v else a < v
  }
  property("shuffle") = forAll { (elems: List[Int], b: Long) =>
    val shuffled = Random.shuffle(elems).run(Seed(b)).value._2
    shuffled.length == elems.length
  }
}
