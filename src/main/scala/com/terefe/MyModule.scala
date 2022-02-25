package com.terefe

import javax.inject.Inject
import play.api.http.HttpConfiguration
import play.api.{Configuration, Environment}
import play.api.inject._
import play.api.mvc.{Action, Handler, RequestHeader, Results}
import play.api.routing.Router.Routes
import play.api.routing.sird.GET
import play.api.routing.{Router, SimpleRouter}

class MyRouter @Inject() (ctrl: HomeController) extends SimpleRouter {

  import play.api.routing.sird._

  def routes: Routes = {
    case GET(p"/greet") => ctrl.index()
    case req            => ctrl.index
  }

}

class MyModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    println("MyModule.bindings")
    Seq(
      bind[MyRouter].toSelf.eagerly()
    )
  }
}
