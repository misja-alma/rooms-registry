package com.ing.roomregistry.model


// TODO this should be called from an Actor to avoid concurrency issues.
//      in particular the addBooking + the validation should be in an atomic block.
class RoomRepository {

  private val rooms = collection.mutable.Map(RoomRepository.initialRooms.toSeq: _*)

  def allRooms: Iterable[Room] = rooms.values

  def findRoom(name: String): Option[Room] = rooms.get(name)

  /**
    * Adds the booking to the room and persists it. Returns the updated room.
    */
  def addBooking(room: Room, booking: Booking): Room = {
    val updatedRoom = room.copy(bookings = booking +: room.bookings)
    rooms += room.name -> updatedRoom
    updatedRoom
  }
}

object RoomRepository {

  // TODO read from config. Maybe a serialized json. Or alternatively, use a db and initialize with a db script.
  val roomNames = List("London", "Paris", "Berlin", "Amsterdam")

  def initialRooms: Map[String, Room] = roomNames.map(name => name -> Room(name, List())).toMap
}
