package models

import java.util.UUID

import play.api.libs.json.Json

/**
  * Created by Bruno on 4/9/2017.
  */
case class Event(id: UUID, name: String, user:String, time:Long, metrics:Option[Map[String, String]])


object Event{
  implicit val jsf = Json.format[Event]
}