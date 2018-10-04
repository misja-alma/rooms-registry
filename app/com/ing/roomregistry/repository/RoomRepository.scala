package com.ing.roomregistry.repository

import com.ing.roomregistry.model.{Booking, Room}

class RoomRepository {
  import RoomRepository._

  private val rooms = collection.mutable.Map(initialRooms.toSeq: _*)

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
