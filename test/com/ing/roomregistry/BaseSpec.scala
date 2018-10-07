package com.ing.roomregistry

import java.time.{LocalDate, LocalDateTime, LocalTime}

import org.scalatestplus.play.PlaySpec

class BaseSpec extends PlaySpec {

  def dateTime(date: LocalDate, hours: Int, minutes: Int): LocalDateTime =
    LocalDateTime.of(date, LocalTime.of(hours, minutes, 0, 0))
}
