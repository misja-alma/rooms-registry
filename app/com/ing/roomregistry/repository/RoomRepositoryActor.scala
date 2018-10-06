package com.ing.roomregistry.repository

import akka.actor._
import com.google.inject.Inject
import com.ing.roomregistry.model.Booking
import com.ing.roomregistry.util.Validation

object RoomRepositoryActor {

  def props = Props[RoomRepositoryActor]

  case class GetAllRooms()

  case class FindRoom(name: String)

  case class AddBooking(roomName: String, booking: Booking)

  case class BookingError(httpStatus: Int, message: String)
}

class RoomRepositoryActor @Inject() (repo: RoomRepository) extends Actor {
  import RoomRepositoryActor._

  def receive = {
    case GetAllRooms() =>
      sender() ! repo.allRooms
    case FindRoom(name: String) =>
      sender() ! repo.findRoom(name)
    case AddBooking(roomName: String, booking: Booking) =>
      val maybeRoom = repo.findRoom(roomName)
      val bookingResult = for {
        room <- Either.cond(maybeRoom.isDefined, maybeRoom.get, BookingError(404, "Room not found"))
        booking <- Validation.validateBooking(room, booking).left.map(BookingError(422, _))
      } yield {
        repo.addBooking(room, booking)
      }
      sender() ! bookingResult
    case unknown =>
      sys.error(s"Can't handle message of unknown type: $unknown")
  }
}