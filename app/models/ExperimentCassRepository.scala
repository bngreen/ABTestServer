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
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Row
import play.api.libs.functional.syntax._

import scala.collection.convert.WrapAsScala
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._

/**
  * Created by Bruno on 4/9/2017.
  */
@Singleton
class ExperimentCassRepository @Inject()(client: SimpleCassClient) {
    import Utils._

    private val insertIntoExperimentQuery = new BoundStatement(client.session.prepare("INSERT INTO abtest.experiment (id, name, description, starttime) VALUES (?, ?, ?, ?);"))
    private val insertIntoVariationQuery = new BoundStatement(client.session.prepare("INSERT INTO abtest.variation (id, experiment_id, name, description, rate) VALUES (?, ?, ?, ?, ?);"))
    def createExperiment(experiment: json.Experiment) : Future[UUID] = {
      val stmt = insertIntoExperimentQuery
      val varquery = insertIntoVariationQuery
      val id = UUIDs.timeBased
      experiment.variations.map(v => {
        var vid = UUIDs.timeBased
        client.session.execute(varquery.bind(vid, id, v.name, v.description, new java.lang.Double(v.rate)))
      })
      client.session.execute(stmt.bind(id, experiment.name, experiment.description, new java.util.Date(experiment.starttime)))
      Future(id)
    }

    private def variations(row: Row) = {
      Variation(row.getUUID("id"), row.getString("name"), row.getString("description"), row.getDouble("rate"))
    }

    private def experiment(row: Row, variations: Seq[Variation]) = {
      Experiment(row.getUUID("id"), row.getTimestamp("starttime").getTime, row.getString("name"), row.getString("description"), variations)
    }

    def exists(name:String) : Future[Boolean] = {
      val stmt = getExperimentByNameQuery
      client.session.executeAsync(stmt.bind(name)).toScalaFuture.map(!_.isExhausted)
    }

    private val getExperimentByNameQuery = new BoundStatement(client.session.prepare("SELECT * FROM abtest.experiment WHERE name = ?;"))
    private val getVariationByExperimentIdQuery = new BoundStatement(client.session.prepare("SELECT * FROM abtest.variation WHERE experiment_id = ?;"))

    def getByName(name:String) : Future[Option[Experiment]] = {
      val stmt = getExperimentByNameQuery
      val varquery = getVariationByExperimentIdQuery
      for {
        ex <- client.session.executeAsync(stmt.bind(name)).toScalaFuture.map(_.one())
        vars <- if (ex == null) Future(null) else client.session.executeAsync(varquery.bind(ex.getUUID("id"))).toScalaFuture.map(x=>x.all().map(r=>variations(r)))
      } yield if (ex == null)
        None
      else
        Some(experiment(ex, vars))
    }
}


