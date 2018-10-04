package com.ing.roomregistry.controllers

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.ing.roomregistry.model.JsonSerialization._
import com.ing.roomregistry.model._
import com.ing.roomregistry.repository.RoomRepositoryActor
import com.ing.roomregistry.repository.RoomRepositoryActor._
import com.ing.roomregistry.util.Availability
import javax.inject._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class RoomsController @Inject()(system: ActorSystem,
                                cc: ControllerComponents)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val repoTimeout: Timeout = 5.seconds

  private val repoActor = system.actorOf(RoomRepositoryActor.props, "room-repository-actor")

  def listAllRooms() = Action.async { request: Request[AnyContent] =>
    val now = LocalDateTime.now()
    (repoActor ? GetAllRooms()).mapTo[Iterable[Room]].map { allRooms =>
      val sortedRooms = allRooms.toList.sortBy(_.name)
      val roomsWithAvailability = sortedRooms.map(room =>
        RoomAvailability(room.name, Availability.isRoomAvailableAt(room, now)))
      Ok(Json.toJson(roomsWithAvailability))
    }
  }

  def roomDetails(name: String) = Action.async { request: Request[AnyContent] =>
    (repoActor ? FindRoom(name)).mapTo[Option[Room]].map { maybeRoom =>
      maybeRoom.map { room =>
        val json = Json.toJson(room)
        Ok(json)
      }.getOrElse (
        NotFound
      )
    }
  }
  
  def bookRoom(name: String) = Action(parse.json).async { request =>
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
      }
    }
  }
}
