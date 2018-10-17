package com.ing.roomregistry.controllers

import java.time.{Duration, LocalDateTime}

import com.ing.roomregistry.model.Booking
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import com.ing.roomregistry.model.JsonSerialization._
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.json.Json

class End2EndSpec extends PlaySpec with GuiceOneServerPerSuite {
  private val wsClient = app.injector.instanceOf[WSClient]
  private val address = s"localhost:$port"

  "The application should respond at GET /api/rooms" in {
    val testURL = s"http://$address/api/rooms"

    val response = await(wsClient.url(testURL).get())

    response.status mustBe OK
  }

  "The application should respond at GET /api/rooms/{name}" in {
    val testURL = s"http://$address/api/rooms/Paris"

    val response = await(wsClient.url(testURL).get())

    response.status mustBe OK
  }

  "The application should respond at POST /api/rooms/{name}" in {
    val testURL = s"http://$address/api/rooms/Paris"
    val newBooking = Booking(LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(25))

    val response = await(wsClient.url(testURL).post(Json.toJson(newBooking)))

    response.status mustBe OK
  }
}