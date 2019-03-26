package controllers

import javax.inject._

import scala.util.control.NonFatal

import akka.actor._
import akka.stream.scaladsl.{ BroadcastHub, Flow, Keep, MergeHub, Sink, Source }
import akka.stream.{ Materializer, OverflowStrategy }
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow

final case class Msg(message: String)
object Msg {
  implicit val msgJsonFormat = Json.format[Msg]
}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (cc: ControllerComponents)(
  implicit
  system: ActorSystem,
  mat:    Materializer) extends AbstractController(cc) {

  /**
   */
  def index = Action {
    Main.publishGet
    Ok(views.html.index("Hello World"))
  }

  def post = Action(parse.json) { implicit req =>
    req.body.validate[Msg] match {
      case JsSuccess(m, _) =>
        Main.publishPost(m.message)
        Ok(m.message)
      case JsError(err) => UnprocessableEntity(JsError.toJson(err))
    }
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => PostSubscriberActor.props(out))
  }
}

object PostSubscriberActor {
  def props(out: ActorRef) = Props(new PostSubscriberActor(Main.bus, out))
}

class PostSubscriberActor(bus: LookupBusImpl[Topic, Message], out: ActorRef) extends Actor {
  override def preStart(): Unit = bus.subscribe(self, Topic.Post)
  override def receive: PartialFunction[Any, Unit] = {
    case a @ Message.Post(msg) => out ! s" * ${msg}"
  }
}

