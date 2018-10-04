package com.ing.roomregistry.util

import java.time.{Duration, LocalDateTime}

import com.ing.roomregistry.model.Room

object Availability {

  /**
    * Returns true if at the given time the room is not booked.
    */
  def isRoomAvailableAt(room: Room, time: LocalDateTime): Boolean = {
    noBookingExistsWhichContainsStartTime(time, room)
  }

  /**
    * Returns true if the room is not booked at the given time until after the given duration.
    */
  def isRoomAvailableAt(room: Room, time: LocalDateTime, duration: Duration): Boolean = {
    noBookingExistsWhichContainsStartTime(time, room) &&
    noBookingExistsWhichContainsEndTime(time, duration, room) &&
    noBookingExistsWhichStartsWithinDuration(time, duration, room)
  }

  private def noBookingExistsWhichContainsEndTime(time: LocalDateTime, duration: Duration, room: Room) = {
    val endTime = time.plus(duration)
    !room.bookings.exists(b => b.time.isBefore(endTime) && b.time.plus(b.duration).isAfter(endTime))
  }

  private def noBookingExistsWhichContainsStartTime(time: LocalDateTime, room: Room) = {
    !room.bookings.exists(b => !b.time.isAfter(time) && b.time.plus(b.duration).isAfter(time))
  }

  private def noBookingExistsWhichStartsWithinDuration(time: LocalDateTime, duration: Duration, room: Room) = {
    val endTime = time.plus(duration)
    !room.bookings.exists(b => !b.time.isBefore(time) && b.time.isBefore(endTime))
  }
}
