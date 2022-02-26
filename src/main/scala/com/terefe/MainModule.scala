package com.terefe

import play.api.Configuration
import play.api.Environment
import play.api.inject.{Module, Binding}

class MainModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[MainRouter].toSelf.eagerly()
    )
  }
}
