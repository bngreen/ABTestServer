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

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.utils.UUIDs

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

  private val insertIntoEventQuery = new BoundStatement(client.session.prepare("INSERT INTO abtest.event (id, time, userid, name, metrics, experiments) VALUES (?, ?, ?, ?, ?, ?);"))
  def createEvent(event:Event) : Future[UUID] = {
    val id = UUIDs.random
    val m = toJavaMap(event.metrics)
    client.session.executeAsync(insertIntoEventQuery.bind(id, new java.util.Date(event.time), event.userid, event.name, toJavaMap(event.metrics), toJavaMap(event.experiments)))
    .toScalaFuture.map(x=>id)
  }
}
