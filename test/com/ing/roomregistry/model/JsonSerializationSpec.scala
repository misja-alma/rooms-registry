package com.ing.roomregistry.model

import java.time.{LocalDate, LocalDateTime}

import com.ing.roomregistry.BaseSpec
import com.ing.roomregistry.model.JsonSerialization._
import play.api.libs.json.{JsString, Json}

class JsonSerializationSpec extends BaseSpec {

  private val date = LocalDate.of(2018, 10, 10)
  
  "localDateTime serialization/ deserialization" should {

    "serialize to the correct datetime format" in {
      val localDateTime = dateTime(date, 11, 35)
      
      Json.toJson(localDateTime) mustBe JsString("2018-10-10T11:35")
    }

    "deserialize from the yy-MM-dd'T'HH-mm format" in {
      val localDateTime = dateTime(date, 13, 35)

      Json.fromJson[LocalDateTime](JsString("2018-10-10T13:35")).get mustBe localDateTime
    }
  }
}
