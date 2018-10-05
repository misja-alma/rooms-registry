package com.ing.roomregistry.repository

import com.ing.roomregistry.model.{Booking, Room}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import scala.util.Try

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
  import com.ing.roomregistry.model.JsonSerialization._

  private val logger = Logger(LoggerFactory.getLogger("RoomRepository"))

  def initialRooms: Map[String, Room] = {
    val result = Try {
      val roomsFile = ConfigFactory.load().getString("rooms-registry.initial-rooms-json")
      val json = Json.parse(getClass.getResourceAsStream(roomsFile))
      Json.fromJson[List[Room]](json)
        .asEither
        .map(
          _.map(room => room.name -> room).toMap
        ).left.map {
          error => error.mkString
        }
    }.recover {
      case ex => Left(s"Exception while initializing the room repository: $ex")
    }.get

    if (result.isLeft) {
      logger.error(result.left.get)
      sys.error("Could not initialize the room repository")
    } else {
      logger.info("Successfully initialized the room repository")
      result.right.get
    }
  }
}
