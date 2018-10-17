package com.ing.roomregistry.booking

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
        ConfigValueFactory.fromAnyRef("/missing.json"))

      the[RuntimeException] thrownBy {
        RoomRepository.loadInitialRooms(wrongConfig)
      } must have message "Could not initialize the room repository"
    }
  }
}