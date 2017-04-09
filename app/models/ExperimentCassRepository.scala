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
      for {
        uuid <- client.session.executeAsync(stmt.bind(id, experiment.name, experiment.description, new java.util.Date(experiment.starttime))).toScalaFuture.map(rs => id)
        _ <- Future(experiment.variations.map(v => {
          var vid = UUIDs.timeBased
          client.session.executeAsync(varquery.bind(vid, uuid, v.name, v.description, new java.lang.Double(v.rate))).toScalaFuture.map(rs => vid)
        }))
      }
      yield uuid
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


