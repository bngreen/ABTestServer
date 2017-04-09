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

package controllers

import javax.inject.Inject

import com.datastax.driver.core.utils.UUIDs
import models.EventCassRepository
import models.json.Variation
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.Util._
/**
  * Created by Bruno on 4/9/2017.
  */
class EventController @Inject()(eventCassRepository: EventCassRepository) extends Controller{
  def trackEvent(name:String) = Action.async(parse.json[models.json.EventTrackData]) { implicit request =>
    val data = request.body
    eventCassRepository.createEvent(models.Event(UUIDs.timeBased, name, data.userstate.userid, data.timestamp, data.metrics, data.userstate.experiments))
      .map(x=>Ok("Created: "+x.toString))
  }
}
