package com.ing.roomregistry.util

import java.time.{LocalDate, LocalDateTime, LocalTime}

import org.scalatestplus.play.PlaySpec

class BaseSpec extends PlaySpec {

  def dateTime(date: LocalDate, hours: Int, minutes: Int, seconds: Int): LocalDateTime =
    LocalDateTime.of(date, LocalTime.of(hours, minutes, seconds, 0))
}
