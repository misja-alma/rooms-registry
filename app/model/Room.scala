package model

// TODO check if a list is the most handy datatype. It should be sorted but also allow fast insert. Maybe a tree?
// Or maybe an in-mem db is best after all ..
// TODO should the list of bookings be ordered/ sorted? Probably ..
case class Room(name: String, bookings: List[Booking])

case class RoomAvailability(name: String, available: Boolean)