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
import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.{BoundStatement, ResultSet}
import com.datastax.driver.core.utils.UUIDs
import play.api.Logger

import scala.collection.JavaConversions._
import utils.Util._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Bruno on 4/9/2017.
  */
@Singleton
class EventCassRepository @Inject()(client: SimpleCassClient) {
  import Utils._
  val insertIntoEventQuery = client.session.prepare("INSERT INTO abtest.event (time, userid, name, metrics, experiments, nmetrics) VALUES (?, ?, ?, ?, ?, ?);")
  def createEvent(event:Event) : Future[Unit] = {
    client.session.executeAsync(new BoundStatement(insertIntoEventQuery).bind(new java.util.Date(event.time), event.userid, event.name, toJavaMap(event.metrics), toJavaMap(event.experiments), toJavaMapD(event.nmetrics)))
    .toScalaFuture.map(x=>()).recover {
      case e : Exception => {
        Logger.error("Error on event creation", e)
        createEvent(event)
      }
    }
  }
}
