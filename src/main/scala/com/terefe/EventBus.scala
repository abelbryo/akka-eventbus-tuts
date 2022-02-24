package com.terefe

import play.api.libs.json.JsValue

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

//////////////////
sealed trait Topic
object Topic {
  case object Post extends Topic
  case object Get extends Topic
  case object Delete extends Topic
  case object Update extends Topic
}

//////////////////
sealed trait Message
object Message extends Message {
  case object Get extends Message
  case class Post[A](body: A) extends Message
}

class GetEventHandler extends Actor {
  import Message._
  def receive = {
    case Get => println("get <---------------")
    case _   => println("Error -- Only handles Get events")
  }
}

class PostEventHandler[A] extends Actor {
  import Message._
  def receive = {
    case Post(body) => println(s"post ----->${body}")
    case _          => println("Error -- Only handles Post events")
  }
}

object Main {

  val system = ActorSystem("test-actor-system")
  val bus = new LookupBusImpl[Topic, Message]

  val getEventHandler =
    system.actorOf(Props[GetEventHandler](), "get-event-actor")
  def postEventHandler[A] =
    system.actorOf(Props[PostEventHandler[A]](), "post-event-actor")

  bus.subscribe(getEventHandler, Topic.Get)
  bus.subscribe(postEventHandler[String], Topic.Post)

  def publishGet = {
    bus.publish(MessageEnvelope(Topic.Get, Message.Get))
  }

  def publishPost(msg: JsValue) = {
    bus.publish(MessageEnvelope(Topic.Post, Message.Post(msg)))
  }

}
