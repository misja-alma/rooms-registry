package com.ing.roomregistry.controllers

import java.time.{LocalDateTime, ZoneOffset}

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.ing.roomregistry.model.JsonSerialization._
import com.ing.roomregistry.model._
import com.ing.roomregistry.booking.BookingActor._
import com.ing.roomregistry.validation.Availability
import com.typesafe.scalalogging.Logger
import javax.inject._
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class RoomsController @Inject()(system: ActorSystem,
                                cc: ControllerComponents,
                                lifeCycle: ApplicationLifecycle,
                                @Named("room-repository-actor") repoActor: ActorRef)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit private val repoTimeout: Timeout = 5.seconds
  implicit private val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(_.toEpochSecond(ZoneOffset.UTC))

  private val logger = Logger(LoggerFactory.getLogger("RoomsController"))

  lifeCycle.addStopHook { () =>
    Future.successful {
      logger.info("Application is shutting down ...")
    }
  }

//  def index = Action { implicit request =>
//    Ok(views.html.index())
//  }

  private def handleError(message: String): PartialFunction[Throwable, Result] = { case ex =>
    logger.error (message, ex)
    InternalServerError
  }

  def listAllRooms() = Action.async { _ =>
    val now = LocalDateTime.now()
    (repoActor ? GetAllRooms()).mapTo[Iterable[Room]].map { allRooms =>
      val sortedRooms = allRooms.toList.sortBy(_.name)
      val roomsWithAvailability = sortedRooms.map(room =>
        RoomAvailability(room.name, Availability.isRoomAvailableAt(room, now)))
      Ok(Json.toJson(roomsWithAvailability))
    }.recover ( handleError("Error when retrieving room list") )
  }

  def roomDetails(name: String) = Action.async { _ =>
    (repoActor ? FindRoom(name)).mapTo[Option[Room]].map { maybeRoom =>
      maybeRoom.map { room =>
        val sortedBookings = room.bookings.sortBy(_.time)
        val json = Json.toJson(room.copy(bookings = sortedBookings))
        Ok(json)
      }.getOrElse (
        NotFound
      )
    }.recover ( handleError("Error when retrieving room details") )
  }

  def bookRoom(name: String) = Action(parse.json).async { request =>
    val parsedBooking = request.body.validate[Booking]
    if (parsedBooking.isError) {
      Future.successful(BadRequest("Invalid Json"))
    } else {
      (repoActor ? AddBooking(name, parsedBooking.get)).mapTo[Either[BookingError, Room]].map {
        case Right(_) =>
          Ok("Booking successful")
        case Left(error) =>
          new Status(error.httpStatus)(error.message)
      }.recover ( handleError("Error when adding booking") )
    }
  }
}
