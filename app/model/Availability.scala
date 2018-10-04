package model

import java.time.{Duration, LocalDateTime}

object Availability {

  def validateBooking(room: Room, booking: Booking): Boolean = {
    !booking.time.isBefore(LocalDateTime.now()) && isRoomAvailableAt(room, booking.time, booking.duration)
  }

  /**
    * Returns true if at the given time the room is not booked.
    */
  def isRoomAvailableAt(room: Room, time: LocalDateTime): Boolean = {
    noBookingExistsWhichContainsStartTime(time, room)
  }

  /**
    * Returns true if the room is not booked at the given time and for the given duration.
    */
  def isRoomAvailableAt(room: Room, time: LocalDateTime, duration: Duration): Boolean = {
    noBookingExistsWhichContainsStartTime(time, room) && noBookingExistsWhichContainsEndTime(time, duration, room)
  }

  private def noBookingExistsWhichContainsEndTime(time: LocalDateTime, duration: Duration, room: Room) = {
    val endTime = time.plus(duration)
    !room.bookings.exists(b => b.time.isBefore(endTime) && b.time.plus(b.duration).isAfter(endTime))
  }

  private def noBookingExistsWhichContainsStartTime(time: LocalDateTime, room: Room) = {
    !room.bookings.exists(b => !b.time.isAfter(time) && b.time.plus(b.duration).isAfter(time))
  }
}
