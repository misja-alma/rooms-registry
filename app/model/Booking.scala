package model

import java.time.{Duration, LocalDateTime}

case class Booking(time: LocalDateTime, duration: Duration)