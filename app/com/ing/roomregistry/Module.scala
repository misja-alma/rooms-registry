package com.ing.roomregistry

import com.google.inject.AbstractModule
import com.ing.roomregistry.repository.RoomRepositoryActor
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[RoomRepositoryActor]("room-repository-actor")
  }
}
