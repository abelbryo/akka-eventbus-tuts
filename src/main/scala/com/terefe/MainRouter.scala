package com.terefe

import play.api.mvc.Action
import play.api.mvc.Handler
import play.api.mvc.RequestHeader
import play.api.mvc.Results
import play.api.routing.Router
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird.GET

import javax.inject.Inject

class MainRouter @Inject() (ctrl: HomeController) extends SimpleRouter {

  import play.api.routing.sird._

  def routes: Routes = {
    case GET(p"/ping")          => ctrl.ping
    case POST(p"/send-message") => ctrl.sendMessage
    case GET(p"/socket")        => ctrl.socket
  }

}
