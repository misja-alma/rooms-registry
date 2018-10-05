package com.ing.roomregistry.repository

import java.time.{Duration, LocalDate}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.model.Booking

class RoomRepositorySpec extends BaseSpec {
  
  "initialRooms" should {

    "return the list of rooms configured in the json file" in {
      val rooms = RoomRepository.initialRooms

      rooms.keys.toList.sorted mustBe List("Amsterdam", "Berlin", "London", "Paris")
    }
  }

  "addBooking" should {

    "add the booking to the room and persist it" in {
      val roomRepository = new RoomRepository
      val paris = roomRepository.findRoom("Paris").get
      val time = dateTime(LocalDate.of(2018, 10, 20), 9, 0, 0)

      val result = roomRepository.addBooking(paris, Booking(time, Duration.ofMinutes(20)))

      result.name mustBe "Paris"
      result.bookings.size mustBe 1
      result.bookings.head.time mustBe time

      roomRepository.findRoom("Paris").get.bookings.size mustBe 1
    }
  }
}