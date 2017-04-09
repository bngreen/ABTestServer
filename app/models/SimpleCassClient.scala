package models

import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.Cluster
import play.api.{Configuration, Play}


/**
  * Created by Bruno on 4/9/2017.
  */
@Singleton
class SimpleCassClient @Inject()(configuration: Configuration) {

    private val cluster = Cluster.builder().addContactPoint(configuration.getString("cassandra.defaultnode").get).build()
    val session = cluster.connect()
    def createSchema(): Unit = {
      session.execute("CREATE KEYSPACE IF NOT EXISTS abtest WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};")
      session.execute(
        """CREATE TABLE IF NOT EXISTS abtest.experiment (
          id uuid PRIMARY KEY,
          starttime timestamp,
          name text,
          description text,
          );""")
      session.execute(
        """CREATE INDEX IF NOT EXISTS experiment_name ON abtest.experiment (name);"""
      )
      session.execute(
        """CREATE TABLE IF NOT EXISTS abtest.variation (
          id uuid PRIMARY KEY,
          experiment_id uuid,
          name text,
          description text,
          rate double,
          );""")
      session.execute(
        """CREATE INDEX IF NOT EXISTS variation_name ON abtest.variation (name);"""
      )
      session.execute(
        """CREATE INDEX IF NOT EXISTS variation_experiment_id ON abtest.variation (experiment_id);"""
      )
    }
  createSchema()
}
