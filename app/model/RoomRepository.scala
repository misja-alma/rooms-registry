package model


// TODO this should be an actor to ensure there are no concurrency issues
class RoomRepository {

  private val rooms = collection.mutable.Map(RoomRepository.initialRooms.toSeq: _*)

  def allRooms: Iterable[Room] = rooms.values

  def findRoom(name: String): Option[Room] = rooms.get(name)

  /**
    * Requires that the room exists.
    */
  def addBooking(roomName: String, booking: Booking): Unit = {
    val room = findRoom(roomName).get
    val updatedRoom = room.copy(bookings = booking +: room.bookings)
    rooms += roomName -> updatedRoom
  }
}

object RoomRepository {

  val roomNames = List("London", "Paris", "Berlin", "Amsterdam")

  def initialRooms: Map[String, Room] = roomNames.map(name => name -> Room(name, List())).toMap
}
