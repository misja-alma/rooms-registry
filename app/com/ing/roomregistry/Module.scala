package com.ing.roomregistry

import com.google.inject.AbstractModule
import com.ing.roomregistry.booking.BookingActor
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[BookingActor]("room-repository-actor")
  }
}
