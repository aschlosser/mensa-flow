package services

import model.User
import org.neo4j.driver.v1.exceptions.NoSuchRecordException
import org.neo4j.driver.v1._
import play.api.Logger

import collection.JavaConverters._
import scala.util.Random

/**
  * @author Alex Schlosser
  */
object Database {

  private val driver = GraphDatabase.driver(Config.neo4jurl, AuthTokens.basic(Config.neo4juser, Config.neo4jpassword))

  def connect(): Connection = {
    Logger.info("Opening new database connection")
    return new Connection(driver.session())
  }

}

class Connection(val db: Session) {

  def checkSession(session: String): Option[User] = {
    val result = db.run(
      """
        MATCH (:Session {sid:{session}})-[:for]->(u:User)
        RETURN u.uid, u.username
      """,
      Map[String, AnyRef]("session" -> session).asJava)
    return resultToUserOption(result, session)
  }

  def login(username: String, password: String): Option[User] = {
    val session = Random.alphanumeric.take(32).mkString
    val result = db.run(
      """
        MATCH (u:User {username:{username}, password:{password}})
        CREATE (s:Session {sid:{session}, created:timestamp()})-[r:for]->(u)
        RETURN u.uid, u.username
      """,
      Map[String, AnyRef](
        "username" -> username,
        "password" -> password,
        "session" -> session).asJava)
    return resultToUserOption(result, session)
  }

  private def resultToUserOption(result: StatementResult, session: String): Option[User] = {
    if (result.hasNext) {
      val r = result.next()
      return Some(new User(r.get("u.uid").asLong(), r.get("u.username").asString(), session))
    } else {
      return None
    }
  }

  def close() = {
    db.close()
  }
}
