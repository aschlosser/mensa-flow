package services

import org.neo4j.driver.v1.{GraphDatabase, Session}
import play.api.Logger

/**
  * @author Alex Schlosser
  */
object Database {

  private val driver = GraphDatabase.driver(Config.neo4jurl)

  def connect(): Connection = {
    Logger.info("Opening new database connection");
    return new Connection(driver.session())
  }

}

class Connection(val session: Session) {
  def close() = {
    session.close()
  }
}
