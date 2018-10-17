package com.ing.roomregistry.booking

import com.ing.roomregistry.model.Room
import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import scala.util.Try

object RoomRepository {
  import com.ing.roomregistry.model.JsonSerialization._

  private val logger = Logger(LoggerFactory.getLogger("RoomRepository"))

  private def exception2Either[B] (f: () => B): Either[String, B] =
    Try {
      f()
    }.toEither
      .left
      .map(_.toString)

  def loadInitialRooms(config: Config): Map[String, Room] = {
    val result = for {
      fileName <- exception2Either(() => config.getString("rooms-registry.initial-rooms-json"))
      json     <- exception2Either(() => Json.parse(getClass.getResourceAsStream(fileName)))
      rooms    <- Json.fromJson[List[Room]](json)
                   .asEither
                   .left
                   .map( _.mkString )
    } yield {
      rooms.map(room => room.name -> room).toMap
    }

    result match {
      case Left(error) =>
        logger.error(error)
        sys.error("Could not initialize the room repository")
      case Right(rooms) =>
        logger.info("Successfully initialized the room repository")
        rooms
    }
  }
}
