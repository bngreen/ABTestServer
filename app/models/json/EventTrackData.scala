package models.json

import models.UserState
import play.api.libs.json.Json

/**
  * Created by Bruno on 4/9/2017.
  */
case class EventTrackData(userstate: UserState, timestamp:Long, metrics:Option[Map[String, String]])

object EventTrackData{
  implicit val jsf = Json.format[EventTrackData]
}

