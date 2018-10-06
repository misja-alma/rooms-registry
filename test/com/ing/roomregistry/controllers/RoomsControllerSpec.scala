package com.ing.roomregistry.controllers

import java.time.{Duration, LocalDateTime}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.model.JsonSerialization._
import com.ing.roomregistry.model.{Booking, Room, RoomAvailability}
import com.ing.roomregistry.repository.RoomRepository
import com.typesafe.config.ConfigFactory
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._


class RoomsControllerSpec extends BaseSpec with GuiceOneAppPerTest with Injecting {
  private val config = ConfigFactory.load()

  "RoomsController GET /rooms" should {

    "return all the rooms with their availability" in {
      val request = FakeRequest(GET, "/rooms")
      val rooms = route(app, request).get

      status(rooms) mustBe OK
      contentType(rooms) mustBe Some("application/json")

      val allRooms = RoomRepository.loadInitialRooms(config).values.toList.sortBy(_.name)
      val availabilities = allRooms.map(room => RoomAvailability(room.name, true))
      val json = Json.toJson(availabilities)
      contentAsString(rooms) mustBe json.toString
    }
  }

  "RoomsController GET /rooms/{name}" should {

    "return the details of the room" in {
      val request = FakeRequest(GET, "/rooms/Paris")
      val roomDetails = route(app, request).get

      status(roomDetails) mustBe OK
      contentType(roomDetails) mustBe Some("application/json")

      val json = Json.toJson(RoomRepository.loadInitialRooms(config)("Paris"))
      contentAsString(roomDetails) mustBe json.toString
    }

    "return http 404 when the room does not exist" in {
      val request = FakeRequest(GET, "/rooms/Foo")
      val rooms = route(app, request).get

      status(rooms) mustBe NOT_FOUND
    }
  }

  "RoomsController POST /rooms/{name}" should {

    "update the booking of the room" in {
      val newBooking = Booking(LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(25))
      val postRequest = FakeRequest(POST, "/rooms/Paris").withJsonBody(Json.toJson(newBooking))
      val result = route(app, postRequest).get

      status(result) mustBe OK

      val getRequest = FakeRequest(GET, "/rooms/Paris")
      val roomDetails = route(app, getRequest).get

      status(roomDetails) mustBe OK
      val room = Json.fromJson[Room](contentAsJson(roomDetails))
      room.isError mustBe false
      room.get.bookings.size mustBe 1
      room.get.bookings.head mustBe newBooking
    }

    "return http 400 when the room does not exist" in {
      val newBooking = Booking(LocalDateTime.now(), Duration.ofMinutes(25))
      val postRequest = FakeRequest(POST, "/rooms/Foo").withJsonBody(Json.toJson(newBooking))
      val result = route(app, postRequest).get

      status(result) mustBe BAD_REQUEST
    }
  }
}
