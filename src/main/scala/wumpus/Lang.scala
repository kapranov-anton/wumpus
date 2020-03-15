package wumpus

sealed trait Lang {
  def batsNearby: String
  def pitNearby: String
  def wumpusNearby: String
  def roomsNearby: String
  def arrows: String
  def whatDirection: String
  def unknownDirection: String
  def moveTo: String
  def missed: String
  def bye: String
  def unknownCommand: String
  def shootOrMove: String
  def wumpusKill: String
  def fell: String
  def outOfArrows: String
  def batsMoveYou: String
  def intro: String
  def win: String
}
object Lang {
  val en = new Lang {
    val intro = "Hunt the Wumpus. You are in the room"
    val batsNearby = "Bats nearby"
    val pitNearby = "Pit nearby"
    val wumpusNearby = "Wumpus nearby"
    val roomsNearby = "Rooms nearby"
    val arrows = "Arrows"
    val whatDirection = "What direction"
    val unknownDirection = "Unknown direction"
    val moveTo = "Move to"
    val missed = "You missed and seemed to frighten off Wumpus"
    val bye = "Bye"
    val unknownCommand = "Unknown command"
    val shootOrMove = "Shoot or move"
    val wumpusKill = "Wumpus killed you :("
    val fell = "You fell into a pit :("
    val outOfArrows = "Out of arrows :("
    val batsMoveYou = "Bats move you to another room"
    val win = "You win"
  }
  val ru = new Lang {
    val intro = "Победи Вумпуса. Ты в комнате"
    val batsNearby = "Летучие мыши рядом"
    val pitNearby = "Яма рядом"
    val wumpusNearby = "Вумпус рядом"
    val roomsNearby = "Комнаты рядом"
    val arrows = "Стрелы"
    val whatDirection = "Куда"
    val unknownDirection = "Незнамо куда"
    val moveTo = "Пошли на"
    val missed = "Промазали и, похоже, спугнули Вумпуса"
    val bye = "Покедова"
    val unknownCommand = "Не знаю такую команду"
    val shootOrMove = "Стрельнём или пойдём"
    val wumpusKill = "Вумпус тебя съел :("
    val fell = "Упали в яму :("
    val outOfArrows = "Стрелы закончились :("
    val batsMoveYou = "Летучие мыши перенесли вас в другую комнату"
    val win = "Победа!"
  }
}
