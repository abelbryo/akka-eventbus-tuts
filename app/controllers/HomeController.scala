package controllers

import javax.inject._

import scala.util.control.NonFatal

import akka.actor._
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (cc: ControllerComponents)(
  implicit
  system: ActorSystem,
  mat: Materializer
) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Main.publishGet
    Ok(views.html.index("Your new application is ready."))
  }

  final case class Msg(message: String)
  object Msg {
    implicit val msgJsonFormat = Json.format[Msg]
  }

  def post = Action(parse.json) { implicit req =>
    req.body.validate[Msg] match {
      case JsSuccess(m, _) =>
        Main.publishPost(m.message)
        Ok(m.message)
      case JsError(err) => UnprocessableEntity(JsError.toJson(err))
    }
  }

//  lazy val (sink, source) = {
//    val source = MergeHub.source[String]
//      .log("source")
//      .recoverWithRetries(-1, { case NonFatal(ex) => Source.empty})
//
//    val sink = BroadcastHub.sink[String]
//    source.toMat(sink)(Keep.both).run()
//  }
//
//  lazy val flow: Flow[String, String, _] = Flow.fromSinkAndSource(sink, source)
//
//  def socketHub = WebSocket.accept[String, String] { implicit req =>
//    flow.map { e =>
//      Main.publishPost(e)
//      s"Received '$e.'"
//    }
//  }

  def socket = WebSocket.accept[String , String] { request =>
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

