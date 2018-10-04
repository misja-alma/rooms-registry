package com.ing.roomregistry.util

import java.time.LocalDateTime

import com.ing.roomregistry.model.{Booking, Room}
import com.ing.roomregistry.util.Availability.isRoomAvailableAt

object Validation {

  // TODO also do the Duration validation, use Cats here
  def validateBooking(room: Room, booking: Booking): Boolean = {
    !booking.time.isBefore(LocalDateTime.now()) && isRoomAvailableAt(room, booking.time, booking.duration)
  }

}
