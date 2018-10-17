package com.ing.roomregistry.validation

import java.time.{Duration, LocalDate, LocalDateTime}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.model.{Booking, Room}

class ValidationSpec extends BaseSpec {
  
  private val date = LocalDate.now.plusDays(1)
  private val bookings = List(
    Booking(dateTime(date, 11, 0), Duration.ofMinutes(30)),
    Booking(dateTime(date, 13, 0), Duration.ofMinutes(30))
  )
  private val room = Room("test", bookings)

  "validateBooking" should {

    "return Right when the booking is valid" in {
      val time = dateTime(date, 12, 0)
      val booking = Booking(time, Duration.ofMinutes(30))

      Validation.validateBooking(room, booking) mustBe Right(booking)
    }

    "return Left with the concatenated errors when the booking is invalid" in {
      val conflictingTimeWithInvalidDuration = dateTime(date, 11, 5)
      val booking = Booking(conflictingTimeWithInvalidDuration, Duration.ofMinutes(0))

      Validation.validateBooking(room, booking) mustBe Left("The duration should be bigger than zero, The booking time conflicts with another booking")

      val timeInThePast = LocalDateTime.now.minusHours(1)
      val pastBooking = Booking(timeInThePast, Duration.ofMinutes(30))

      Validation.validateBooking(room, pastBooking) mustBe Left("The booking should not start in the past")
    }
  }
}
