/*
Copyright (C) 2017  Bruno Naspolini Green. All rights reserved.

This file is part of ABTestServer.

ABTestServer is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ABTestServer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ABTestServer.  If not, see <http://www.gnu.org/licenses/>.
*/

package models

import java.util.UUID

import play.api.libs.json.Json

/**
  * Created by Bruno on 4/9/2017.
  */
case class Event(id: UUID, name: String, userid:String, time:Long, metrics:Option[Map[String, String]], nmetrics:Option[Map[String, Double]], experiments: Map[String, String])


object Event{
  implicit val jsf = Json.format[Event]
}