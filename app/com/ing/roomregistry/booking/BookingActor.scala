package com.ing.roomregistry.booking

import akka.actor._
import com.google.inject.Inject
import com.ing.roomregistry.model.Booking
import com.ing.roomregistry.validation.Validation
import play.api.http.Status._

object BookingActor {

  def props = Props[BookingActor]

  sealed trait BookingRequest

  final case class GetAllRooms() extends BookingRequest

  final case class FindRoom(name: String) extends BookingRequest

  final case class AddBooking(roomName: String, booking: Booking) extends BookingRequest

  case class BookingError(httpStatus: Int, message: String)
}

class BookingActor @Inject()(repo: RoomRepository) extends Actor {
  import BookingActor._

  def receive = {
    case GetAllRooms() =>
      sender() ! repo.allRooms
    case FindRoom(name: String) =>
      sender() ! repo.findRoom(name)
    case AddBooking(roomName: String, booking: Booking) =>
      val maybeRoom = repo.findRoom(roomName)
      val bookingResult = for {
        room <- Either.cond(maybeRoom.isDefined, maybeRoom.get, BookingError(NOT_FOUND, "Room not found"))
        booking <- Validation.validateBooking(room, booking).left.map(BookingError(UNPROCESSABLE_ENTITY, _))
      } yield {
        repo.addBooking(room, booking)
      }
      sender() ! bookingResult
    case unknown =>
      sys.error(s"Can't handle message of unknown type: $unknown")
  }
}