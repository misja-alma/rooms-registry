package controllers

import java.time.LocalDateTime

import javax.inject._
import model.Serialization._
import model._
import play.api.libs.json._
import play.api.mvc._

/**
 * Controls the rooms registry
 */
@Singleton
class RoomsController @Inject()(cc: ControllerComponents, repo: RoomRepository) extends AbstractController(cc) {

  def listAllRooms() = Action { request: Request[AnyContent] =>
    val now = LocalDateTime.now()
    val sortedRooms = repo.allRooms.toList.sortBy(_.name)
    val roomsWithAvailability = sortedRooms.map(room => RoomAvailability(room.name, Availability.isRoomAvailableAt(room, now)))
    Ok(Json.toJson(roomsWithAvailability))
  }

  def roomDetails(name: String) = Action { request: Request[AnyContent] =>
    val maybeRoom = repo.findRoom(name)
    if (maybeRoom.isDefined) {
      val json = Json.toJson(maybeRoom.get)
      Ok(json)
    } else {
      NotFound
    }
  }
  
  def bookRoom(name: String) = Action(parse.json) { request =>
    val maybeRoom: Option[Room] = repo.findRoom(name)
    if (maybeRoom.isEmpty) {
      NotFound
    } else {
      val bookingResult: JsResult[Booking] = request.body.validate[Booking]
      if (bookingResult.isError) {
        BadRequest("Invalid Json")
      } else {
        // TODO validate both room and bookingResult
        // TODO and validate that there is no booking conflict, that it's not in the past, etc. Probably use Cats Validated here.
        val valid = Availability.validateBooking(maybeRoom.get, bookingResult.get)
        if (valid) {
          repo.addBooking(name, bookingResult.get)
          Ok("Booking successful")
        } else {
          BadRequest("Invalid booking")
        }
      }
    }
  }
}
