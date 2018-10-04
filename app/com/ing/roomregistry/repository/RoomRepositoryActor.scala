package com.ing.roomregistry.repository

import akka.actor._
import com.ing.roomregistry.model.Booking
import com.ing.roomregistry.util.Validation

object RoomRepositoryActor {

  def props = Props[RoomRepositoryActor]

  case class GetAllRooms()

  case class FindRoom(name: String)

  case class AddBooking(roomName: String, booking: Booking)
}

class RoomRepositoryActor extends Actor {
  import RoomRepositoryActor._

  val repo = new RoomRepository

  def receive = {
    case GetAllRooms() =>
      sender() ! repo.allRooms
    case FindRoom(name: String) =>
      sender() ! repo.findRoom(name)
    case AddBooking(roomName: String, booking: Booking) =>
      val maybeRoom = repo.findRoom(roomName)
      val bookingResult = for {
        room <- Either.cond(maybeRoom.isDefined, maybeRoom.get, "Room not found")
        booking <- Validation.validateBooking(room, booking)
      } yield {
        repo.addBooking(room, booking)
      }
      sender() ! bookingResult
    case unknown =>
      sys.error(s"Can't handle message of unknown type: $unknown")
  }
}