package com.ing.roomregistry.repository

import java.time.{Duration, LocalDate}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.model.Booking
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}

class RoomRepositorySpec extends BaseSpec {
  private val config = ConfigFactory.load()

  "initialRooms" should {

    "return the list of rooms configured in the json file" in {
      val rooms = RoomRepository.loadInitialRooms(config)

      rooms.keys.toList.sorted mustBe List("Amsterdam", "Berlin", "London", "Paris")
    }

    "throw an appropriate error when the json file is not found" in {
      val wrongConfig = config.withValue("rooms-registry.initial-rooms-json",
        ConfigValueFactory.fromAnyRef("missing.json"))

      the[RuntimeException] thrownBy {
        RoomRepository.loadInitialRooms(wrongConfig)
      } must have message "Could not initialize the room repository"
    }
  }

  "addBooking" should {

    "add the booking to the room and persist it" in {
      val roomRepository = new RoomRepository(config)
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