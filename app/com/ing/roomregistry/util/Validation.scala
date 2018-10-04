package com.ing.roomregistry.util

import java.time.LocalDateTime

import com.ing.roomregistry.model.{Booking, Room}
import com.ing.roomregistry.util.Availability.isRoomAvailableAt

object Validation {
  
  /**
    * Returns either a string with all failed validations, or the valid booking
    */
  def validateBooking(room: Room, booking: Booking): Either[String, Booking] = {
    val errors = Seq(
      validateDuration(booking),
      validateStartTime(booking),
      validateBookingConflict(room, booking)).flatten

    if (errors.isEmpty) Right(booking) else Left(errors.mkString(", "))
  }

  private def validateDuration(booking: Booking): Option[String] =
    if (booking.duration.isZero)
      Some("The duration should be bigger than zero")
    else
      None

  private def validateStartTime(booking: Booking): Option[String] =
    if (booking.time.isBefore(LocalDateTime.now()))
      Some("The booking should not start in the past")
    else
      None

  private def validateBookingConflict(room: Room, booking: Booking): Option[String] =
    if (isRoomAvailableAt(room, booking.time, booking.duration))
      None
    else
      Some("The booking time conflicts with another booking")
}
