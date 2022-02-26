package com.terefe

import akka.actor._
import akka.stream.Materializer
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import javax.inject.Inject
import javax.inject.Singleton

/** This controller creates an `Action` to handle HTTP requests to the application's home page.
  */
@Singleton
class HomeController @Inject() (cc: ControllerComponents)(implicit
    system: ActorSystem,
    mat: Materializer
) extends AbstractController(cc) {

  import HomeController.PostBody

  def ping = Action {
    LookupBusImpl.publishGet
    Ok("pong")
  }

  def sendMessage = Action(parse.json) { (req: Request[JsValue]) =>
    req.body
      .validate[PostBody]
      .fold(
        err => UnprocessableEntity(JsError.toJson(err)),
        data => {
          LookupBusImpl.publishPost(req.body)
          Ok(data.message)
        }
      )
  }

  def socket = WebSocket.accept[JsValue, JsValue] { _ =>
    ActorFlow
      .actorRef[JsValue, JsValue](out => WebSocketSubscriberActor.props(out))
      .map { jsValue =>
        Json.obj("body" -> jsValue, "confirmation" -> "Recieved your message")
      }
  }
}

/** Companion of [[HomeController]]. */
object HomeController {
  final case class PostBody(message: String)
  object PostBody {
    given msgJsonFormat: OFormat[PostBody] = Json.format[PostBody]
  }
}

/* Actor */
object WebSocketSubscriberActor {
  def props(out: ActorRef) = Props(new WebSocketSubscriberActor(LookupBusImpl.bus, out))
}

class WebSocketSubscriberActor(bus: LookupBusImpl[Topic, Message], out: ActorRef) extends Actor {
  override def preStart(): Unit = bus.subscribe(self, Topic.Post)
  override def receive: PartialFunction[Any, Unit] = { msg =>
    msg
      .asInstanceOf[JsValue]
      .validate[Message.Post]
      .fold(
        err => println(s"***Error: Can't validate ${msg} Message.Post"),
        data =>
          println("*** Recieved successful Message.Post: " + data)
          out ! data.body
      )
  }
}
