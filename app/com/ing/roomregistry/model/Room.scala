package com.ing.roomregistry.model

case class Room(name: String, bookings: List[Booking])

case class RoomAvailability(name: String, available: Boolean)