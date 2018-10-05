package com.ing.roomregistry.controllers

import java.time.{LocalDateTime, ZoneOffset}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.ing.roomregistry.model.JsonSerialization._
import com.ing.roomregistry.model._
import com.ing.roomregistry.repository.RoomRepositoryActor
import com.ing.roomregistry.repository.RoomRepositoryActor._
import com.ing.roomregistry.util.Availability
import com.typesafe.scalalogging.Logger
import javax.inject._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class RoomsController @Inject()(system: ActorSystem,
                                cc: ControllerComponents)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val repoTimeout: Timeout = 5.seconds
  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(_.toEpochSecond(ZoneOffset.UTC))

  private val logger = Logger(LoggerFactory.getLogger("RoomsController"))

  private val repoActor = system.actorOf(RoomRepositoryActor.props, "room-repository-actor")

  def listAllRooms() = Action.async { request: Request[AnyContent] =>
    logger.info(s"Incoming request: $request")

    val now = LocalDateTime.now()
    (repoActor ? GetAllRooms()).mapTo[Iterable[Room]].map { allRooms =>
      val sortedRooms = allRooms.toList.sortBy(_.name)
      val roomsWithAvailability = sortedRooms.map(room =>
        RoomAvailability(room.name, Availability.isRoomAvailableAt(room, now)))
      Ok(Json.toJson(roomsWithAvailability))
    }.recover { case ex =>
      logger.error ("Error when retrieving list of rooms", ex)
      InternalServerError
    }
  }

  def roomDetails(name: String) = Action.async { request: Request[AnyContent] =>
    logger.info(s"Incoming request: $request")

    (repoActor ? FindRoom(name)).mapTo[Option[Room]].map { maybeRoom =>
      maybeRoom.map { room =>
        val sortedBookings = room.bookings.sortBy(_.time)
        val json = Json.toJson(room.copy(bookings = sortedBookings))
        Ok(json)
      }.getOrElse (
        NotFound
      )
    }.recover { case ex =>
      logger.error ("Error when retrieving room details", ex)
      InternalServerError
    }
  }
  
  def bookRoom(name: String) = Action(parse.json).async { request =>
    logger.info(s"Incoming request: $request")

    val parsedBooking = request.body.validate[Booking]
    if (parsedBooking.isError) {
      Future.successful(BadRequest("Invalid Json"))
    } else {
      (repoActor ? AddBooking(name, parsedBooking.get)).mapTo[Either[String, Room]].map { updatedRoomOrError =>
        if (updatedRoomOrError.isRight) {
          Ok("Booking successful")
        } else {
          BadRequest(updatedRoomOrError.left.get)
        }
      }.recover { case ex =>
        logger.error ("Error when adding booking", ex)
        InternalServerError
      }
    }
  }
}
