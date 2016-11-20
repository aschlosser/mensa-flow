package services

import javax.inject.Provider

import model.User
import org.neo4j.driver.v1._
import play.api.Logger

import collection.JavaConverters._
import scala.util.Random

trait ConnectionProvider extends Provider[Connection] {
  def withConnection[T](f: Connection => T): T = {
    val con = get()
    try f(con) finally con.close()
  }
}

object Neo4jDatabase extends ConnectionProvider {

  private val driver = GraphDatabase.driver(Config.neo4jurl, AuthTokens.basic(Config.neo4juser, Config.neo4jpassword))

  override def get(): Connection = {
    Logger.info("Opening new database connection")
    new Neo4jConnection(driver.session())
  }

}

object MockDatabase extends ConnectionProvider {
  override def get(): Connection = new Connection {
    override def checkSession(session: String): Option[User] = {
      if ("fs8e7hal4ba47wgtol4g".equals(session)) {
        Some(new User(0, "Testuser", session))
      } else {
        None
      }
    }
    override def close(): Unit = {}
    override def login(username: String, password: String): Option[User] = {
      if ("Testuser".equals(username.toLowerCase) && "mother3".equals(password)) {
        Some(new User(0, "Testuser", "fs8e7hal4ba47wgtol4g"))
      } else {
        None
      }
    }
  }
}


trait Connection {

  def close()
  def checkSession(session: String): Option[User]
  def login(username: String, password: String): Option[User]

}

class Neo4jConnection(private val db: Session) extends Connection {

  def checkSession(session: String): Option[User] = {
    val result = db.run(
      """
        MATCH (:Session {sid:{session}})-[:for]->(u:User)
        RETURN u.uid, u.username
      """,
      Map[String, AnyRef]("session" -> session).asJava)
    resultToUserOption(result, session)
  }

  def login(username: String, password: String): Option[User] = {
    val session = Random.alphanumeric.take(Config.sessionIdLength).mkString
    val result = db.run(
      """
        MATCH (u:User {username:{username}, password:{password}})
        CREATE (s:Session {sid:{session}, created:timestamp()})-[r:for]->(u)
        RETURN u.uid, u.username
      """,
      Map[String, AnyRef](
        "username" -> username,
        "password" -> password,
        "session" -> session
      ).asJava
    )
    resultToUserOption(result, session)
  }

  private def resultToUserOption(result: StatementResult, session: String): Option[User] = {
    if (result.hasNext) {
      val r = result.next()
      Some(new User(r.get("u.uid").asLong(), r.get("u.username").asString(), session))
    } else {
      None
    }
  }

  def close() = {
    db.close()
  }
}
