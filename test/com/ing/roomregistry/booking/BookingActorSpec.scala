package com.ing.roomregistry.booking

import java.time.{Duration, LocalDateTime}

import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import com.ing.roomregistry.model.{Booking, Room}
import com.ing.roomregistry.booking.BookingActor.{AddBooking, BookingError}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike}
import play.api.http.Status._

class BookingActorSpec extends TestKit(ActorSystem("RoomRepositoryActorSpec"))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers {

  private val config = ConfigFactory.load()

  private def createRepoActor() = system.actorOf(Props(new BookingActor(new RoomRepository(config))))

  "addBooking" should {

    "add the booking to the room when it is valid and return Right with the updated room" in {
      val actor = createRepoActor()
      val probe = TestProbe()
      val newBooking = Booking(LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(25))

      actor.tell(AddBooking("Paris", newBooking), probe.ref)

      val updatedRoom = Room("Paris", List(newBooking))
      probe.expectMsg(Right(updatedRoom))
    }

    "return Left with an appropriate booking error when the room was not found" in {
      val actor = createRepoActor()
      val probe = TestProbe()
      val newBooking = Booking(LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(25))

      actor.tell(AddBooking("Foo", newBooking), probe.ref)

      probe.expectMsg(Left(BookingError(NOT_FOUND, "Room not found")))
    }

    "return Left with an appropriate booking error when the booking was not valid" in {
      val actor = createRepoActor()
      val probe = TestProbe()
      val invalidBooking = Booking(LocalDateTime.now().plusMinutes(5), Duration.ZERO)

      actor.tell(AddBooking("Paris", invalidBooking), probe.ref)

      probe.expectMsg(Left(BookingError(UNPROCESSABLE_ENTITY, "The duration should be bigger than zero")))
    }
  }
}
