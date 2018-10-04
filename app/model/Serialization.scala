package model

import java.time.{Duration, LocalDateTime}

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

object Serialization {

  implicit val bookingWrites: Writes[Booking] = (
    (JsPath \ "time").write[LocalDateTime] and
      (JsPath \ "duration").write[Duration]
    )(unlift(Booking.unapply))


  implicit val bookingReads: Reads[Booking] = (
    (JsPath \ "time").read[LocalDateTime] and
      (JsPath \ "duration").read[Duration]
    )(Booking.apply _)

  implicit val roomWrites: Writes[Room] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "bookings").write[List[Booking]]
    )(unlift(Room.unapply))

  implicit val roomReads: Reads[Room] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "bookings").read[List[Booking]]
    )(Room.apply _)

  implicit val roomAvailabilityWrites: Writes[RoomAvailability] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "available").write[Boolean]
    )(unlift(RoomAvailability.unapply))

  implicit val roomAvailabilityReads: Reads[RoomAvailability] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "available").read[Boolean]
    )(RoomAvailability.apply _)
}
