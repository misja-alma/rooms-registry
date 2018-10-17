package com.ing.roomregistry.controllers

import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDateTime, ZoneOffset}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.booking.RoomRepository
import com.ing.roomregistry.model.JsonSerialization._
import com.ing.roomregistry.model.{Booking, Room, RoomAvailability}
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._


class RoomsControllerSpec extends BaseSpec with GuiceOneAppPerTest with Injecting {
  implicit private val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(_.toEpochSecond(ZoneOffset.UTC))

  private val config = ConfigFactory.load().withValue(
    "rooms-registry.initial-rooms-json",
    ConfigValueFactory.fromAnyRef("/populated-rooms.json"))

  "RoomsController GET /api/rooms" should {

    "return all the rooms with their availability, sorted by name" in {
      val request = FakeRequest(GET, "/api/rooms")
      val rooms = route(app, request).get

      status(rooms) mustBe OK
      contentType(rooms) mustBe Some("application/json")

      val allRooms = RoomRepository.loadInitialRooms(config).values.toList.sortBy(_.name)
      val availabilities = allRooms.map(room => RoomAvailability(room.name, true))
      val json = Json.toJson(availabilities)
      contentAsString(rooms) mustBe json.toString
    }
  }

  "RoomsController GET /api/rooms/{name}" should {

    "return the room with its bookings, sorted by time" in {
      val request = FakeRequest(GET, "/api/rooms/Paris")
      val roomDetails = route(app, request).get

      status(roomDetails) mustBe OK
      contentType(roomDetails) mustBe Some("application/json")

      val room = RoomRepository.loadInitialRooms(config)("Paris")
      val sortedBookings = room.bookings.sortBy(_.time)
      val json = Json.toJson(room.copy(bookings = sortedBookings))
      contentAsString(roomDetails) mustBe json.toString
    }

    "return http 404 when the room does not exist" in {
      val request = FakeRequest(GET, "/api/rooms/Foo")
      val room = route(app, request).get

      status(room) mustBe NOT_FOUND
    }
  }

  "RoomsController POST /api/rooms/{name}" should {

    "update the booking of the room" in {
      val newBooking = Booking(LocalDateTime.now()
        .plusMinutes(5)
        .truncatedTo(ChronoUnit.MINUTES),
        Duration.ofMinutes(25)
      )
      val postRequest = FakeRequest(POST, "/api/rooms/Paris").withJsonBody(Json.toJson(newBooking))
      val result = route(app, postRequest).get

      status(result) mustBe OK

      val getRequest = FakeRequest(GET, "/api/rooms/Paris")
      val roomDetails = route(app, getRequest).get

      status(roomDetails) mustBe OK
      val room = Json.fromJson[Room](contentAsJson(roomDetails))
      room.isError mustBe false
      room.get.bookings.size mustBe 1
      room.get.bookings.head mustBe newBooking
    }

    "return http 404 when the room does not exist" in {
      val newBooking = Booking(LocalDateTime.now(), Duration.ofMinutes(25))
      val postRequest = FakeRequest(POST, "/api/rooms/Foo").withJsonBody(Json.toJson(newBooking))
      val result = route(app, postRequest).get

      status(result) mustBe NOT_FOUND
    }
  }
}
