package wumpus

import cats.Eq

sealed trait PlayerCommand
object PlayerCommand {
  case object Move extends PlayerCommand
  case object Shoot extends PlayerCommand
  case object Quit extends PlayerCommand
  case object Unknown extends PlayerCommand

  implicit val eq: Eq[PlayerCommand] = Eq.fromUniversalEquals[PlayerCommand]

  def fromString(s: String): PlayerCommand =
    s.trim.toLowerCase match {
      case "m" => Move
      case "s" => Shoot
      case "q" => Quit
      case _   => Unknown
    }
}

