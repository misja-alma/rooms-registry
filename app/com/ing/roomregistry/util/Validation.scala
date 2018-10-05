package com.ing.roomregistry.util

import java.time.LocalDateTime

import com.ing.roomregistry.model.{Booking, Room}
import com.ing.roomregistry.util.Availability.isRoomAvailableAt
import cats.kernel.Semigroup
import cats.syntax.semigroup._

object Validation {

  private implicit val optionalStringSemiGroup = new Semigroup[Option[String]]  {
    override def combine(x: Option[String], y: Option[String]): Option[String] = (x, y) match {
      case (None, _) => y
      case (_, None) => x
      case (Some(a), Some(b)) => Some(a + ", " + b)
    }
  }
  
  /**
    * Returns either a string with all failed validations, or the valid booking
    */
  def validateBooking(room: Room, booking: Booking): Either[String, Booking] = {
    val errors = List(
      validateDuration(booking),
      validateStartTime(booking),
      validateBookingConflict(room, booking))
      .reduceLeft (_ |+| _)

    Either.cond(errors.isEmpty, booking, errors.get)
  }

  private def validateDuration(booking: Booking): Option[String] =
    validate (!booking.duration.isZero, "The duration should be bigger than zero")

  private def validateStartTime(booking: Booking): Option[String] =
    validate (!booking.time.isBefore(LocalDateTime.now()), "The booking should not start in the past")

  private def validateBookingConflict(room: Room, booking: Booking): Option[String] =
    validate (isRoomAvailableAt(room, booking.time, booking.duration),
      "The booking time conflicts with another booking")

  private def validate(validation: Boolean, errorMessage: String): Option[String] =
    if (validation) None else Some(errorMessage)
}
