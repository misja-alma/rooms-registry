package com.ing.roomregistry.validation

import java.time.{Duration, LocalDate}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.model.{Booking, Room}

class AvailabilitySpec extends BaseSpec {

  private val date = LocalDate.of(2018, 10, 10)
  private val bookings = List(
    Booking(dateTime(date, 11, 0), Duration.ofMinutes(30)),
    Booking(dateTime(date, 13, 0), Duration.ofMinutes(30))
  )
  private val room = Room("test", bookings)
  
  "isRoomAvailableAt(room, time)" should {

    "return true when the room is not booked" in {
      val time = dateTime(date, 12, 0)

      Availability.isRoomAvailableAt(room, time) mustBe true

      val timeAtEnd = dateTime(date, 11, 30)

      Availability.isRoomAvailableAt(room, timeAtEnd) mustBe true
    }

    "return false when the room is booked" in {
      val timeInMiddle = dateTime(date, 11, 15)

      Availability.isRoomAvailableAt(room, timeInMiddle) mustBe false

      val timeAtStart = dateTime(date, 11, 0)

      Availability.isRoomAvailableAt(room, timeAtStart) mustBe false
    }
  }

  "isRoomAvailableAt(room, time, duration)" should {

    "return true when the room is not booked" in {
      val time = dateTime(date, 12, 0)

      Availability.isRoomAvailableAt(room, time, Duration.ofMinutes(30)) mustBe true

      val timeAtEnd = dateTime(date, 11, 30)

      Availability.isRoomAvailableAt(room, timeAtEnd, Duration.ofMinutes(30)) mustBe true

      val timeBeforeStart = dateTime(date, 12, 30)

      Availability.isRoomAvailableAt(room, timeBeforeStart, Duration.ofMinutes(30)) mustBe true
    }

    "return false when the room is booked" in {
      val timeInMiddle = dateTime(date, 11, 15)

      Availability.isRoomAvailableAt(room, timeInMiddle, Duration.ofMinutes(5)) mustBe false
      Availability.isRoomAvailableAt(room, timeInMiddle, Duration.ofMinutes(30)) mustBe false

      val timeAtStart = dateTime(date, 11, 0)

      Availability.isRoomAvailableAt(room, timeAtStart, Duration.ofMinutes(5)) mustBe false

      val timeBeforeStart = dateTime(date, 10, 45)

      Availability.isRoomAvailableAt(room, timeBeforeStart, Duration.ofMinutes(30)) mustBe false
      Availability.isRoomAvailableAt(room, timeBeforeStart, Duration.ofMinutes(60)) mustBe false
    }
  }
}