package wumpus

import cats.data.State
import cats.implicits._

/**
  * Knuth’s 64-bit linear congruential generator
  */
final case class Seed(long: Long) {
  def next = Seed(long * 6364136223846793005L + 1442695040888963407L)
}
object Random {
  val nextLong: State[Seed, Long] = State(seed => (seed.next, seed.long))
  val nextBoolean: State[Seed, Boolean] = nextLong.map(long => long > 0)
  val nextCap: Long => State[Seed, Long] = {
    case 0   => nextLong.as(0)
    case max => nextLong.map(long => long % max)
  }
  def shuffle[A](elems: List[A]): State[Seed, List[A]] =
    elems.traverse { e => nextLong.tupleRight(e) }.map(_.sortBy(_._1).map(_._2))
}
