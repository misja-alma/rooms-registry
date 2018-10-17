package com.ing.roomregistry.booking

import akka.actor._
import com.google.inject.Inject
import com.ing.roomregistry.model.{Booking, Room}
import com.ing.roomregistry.validation.Validation
import com.typesafe.config.Config
import play.api.http.Status._

object BookingActor {

  def props = Props[BookingActor]

  sealed trait BookingRequest

  final case class GetAllRooms() extends BookingRequest

  final case class FindRoom(name: String) extends BookingRequest

  final case class AddBooking(roomName: String, booking: Booking) extends BookingRequest

  case class BookingError(httpStatus: Int, message: String)
}

class BookingActor @Inject()(config: Config) extends Actor {
  import BookingActor._

  def receive = doReceive(RoomRepository.loadInitialRooms(config))

  def doReceive(rooms: Map[String, Room]): Receive = {
    case GetAllRooms() =>
      sender() ! rooms.values

    case FindRoom(name: String) =>
      sender() ! rooms.get(name)

    case AddBooking(roomName: String, booking: Booking) =>
      val maybeRoom = rooms.get(roomName)
      val bookingResult = for {
        room <- Either.cond(maybeRoom.isDefined, maybeRoom.get, BookingError(NOT_FOUND, "Room not found"))
        booking <- Validation.validateBooking(room, booking).left.map(BookingError(UNPROCESSABLE_ENTITY, _))
      } yield {
        val updatedRoom = room.copy(bookings = booking +: room.bookings)
        val newRooms = rooms + (room.name -> updatedRoom)
        context.become(doReceive(newRooms))

        updatedRoom
      }

      sender() ! bookingResult

    case unknown =>
      sys.error(s"Can't handle message of unknown type: $unknown")
  }
}