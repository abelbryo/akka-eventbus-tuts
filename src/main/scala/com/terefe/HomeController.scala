package com.terefe

import javax.inject.{Singleton, Inject}

import akka.actor._
import akka.stream.Materializer
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (cc: ControllerComponents)(implicit
    system: ActorSystem,
    mat: Materializer
) extends AbstractController(cc) {

  import HomeController.PostBody

  def index = Action {
    Main.publishGet
    Ok("Hello World")
  }

  def post = Action(parse.json) { (req: Request[JsValue]) =>
    req.body
      .validate[PostBody]
      .fold(
        err => UnprocessableEntity(JsError.toJson(err)),
        data => {
          Main.publishPost(req.body)
          Ok(data.message)
        }
      )
  }

  def socket = WebSocket.accept[JsValue, JsValue] { _ =>
    ActorFlow.actorRef(out => PostSubscriberActor.props(out))
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
object PostSubscriberActor {
  def props(out: ActorRef) = Props(new PostSubscriberActor(Main.bus, out))
}

class PostSubscriberActor(bus: LookupBusImpl[Topic, Message], out: ActorRef)
    extends Actor {
  override def preStart(): Unit = bus.subscribe(self, Topic.Post)
  override def receive: PartialFunction[Any, Unit] = { case Message.Post(msg) =>
    out ! msg
  }
}
