package com.terefe

import play.api.libs.json.{JsValue, Format, Json}

import akka.actor._
import akka.event.EventBus
import akka.event.LookupClassification

final case class MessageEnvelope[T, A](topic: T, payload: A)

class LookupBusImpl[T, A] extends EventBus with LookupClassification {

  type Event = MessageEnvelope[T, A]
  type Classifier = T
  type Subscriber = ActorRef

  override protected def classify(e: Event): Classifier = e.topic
  override protected def publish(e: Event, s: Subscriber): Unit = s ! e.payload
  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int =
    a.compareTo(b)

  override protected def mapSize(): Int = 128
}

enum Topic:
  case Post, Get, Delete, Update

enum Message:
  case Get extends Message
  case Post(body: JsValue) extends Message

object Message:
  given PostFormat: Format[Message.Post] = Json.format
end Message

// REST actor subscribers
class GetEventHandler(bus: LookupBusImpl[Topic, Message]) extends Actor {
  override def preStart(): Unit = bus.subscribe(self, Topic.Get)

  def receive = {
    case Message.Get => println("get <---------------")
    case _           => println("Error -- Only handles Get events")
  }
}

class PostEventHandler(bus: LookupBusImpl[Topic, Message]) extends Actor {
  override def preStart(): Unit = bus.subscribe(self, Topic.Post)
  def receive = {
    case Message.Post(body) => println(s"post ----->${body}")
    case _                  => println("Error -- Only handles Post events")
  }
}

object LookupBusImpl {

  val system = ActorSystem("test-actor-system")
  val bus = new LookupBusImpl[Topic, Message]

  // This is just so the actors can be created
  // and the bus can be subscribed to
  system.actorOf(Props(new GetEventHandler(bus)), "get-event-actor")
  system.actorOf(Props(new PostEventHandler(bus)), "post-event-actor")

  // Used in the controller
  def publishGet: Unit = {
    bus.publish(MessageEnvelope(Topic.Get, Message.Get))
  }

  def publishPost(msg: JsValue): Unit = {
    println(s"Main.publishPost: ${msg}")
    bus.publish(MessageEnvelope(Topic.Post, Message.Post(msg)))
  }

}
