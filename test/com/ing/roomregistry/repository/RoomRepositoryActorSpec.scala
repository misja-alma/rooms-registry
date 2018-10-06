package com.ing.roomregistry.repository

import java.time.{Duration, LocalDateTime}

import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import com.ing.roomregistry.model.{Booking, Room}
import com.ing.roomregistry.repository.RoomRepositoryActor.{AddBooking, BookingError}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike}

class RoomRepositoryActorSpec extends TestKit(ActorSystem("RoomRepositoryActorSpec"))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers {

  private val config = ConfigFactory.load()

  private def createRepoActor() = system.actorOf(Props(new RoomRepositoryActor(new RoomRepository(config))))

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

      probe.expectMsg(Left(BookingError(404, "Room not found")))
    }

    "return Left with an appropriate booking error when the booking was not valid" in {
      val actor = createRepoActor()
      val probe = TestProbe()
      val invalidBooking = Booking(LocalDateTime.now().plusMinutes(5), Duration.ZERO)

      actor.tell(AddBooking("Paris", invalidBooking), probe.ref)

      probe.expectMsg(Left(BookingError(422, "The duration should be bigger than zero")))
    }
  }
}
