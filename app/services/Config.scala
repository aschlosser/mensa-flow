package services

import java.io.File

import play.api.Logger
import play.api.libs.json._

import scala.io.Source._

/**
  * Load the config file and make it accessible a Service.
  */
object Config {
  private val CONFIG_FILE = "mensa-flow.conf"
  private val data = parseConfigFile()

  val neo4jurl = (data \ "neo4j" \ "url").as[String]
  val neo4juser = (data \ "neo4j" \ "auth" \ "user").as[String]
  val neo4jpassword = (data \ "neo4j" \ "auth" \ "password").as[String]
  val baseurl = (data \ "baseurl").as[String]

  val sessioncookie = "mf-session"
  val sessionIdLength = 32

  private def parseConfigFile(): JsValue = {
    val f = new File(CONFIG_FILE)
    if (f.exists()) {
      Logger.info(s"Loading config from $CONFIG_FILE")
      val buffer = fromFile(f)
      val v = Json.parse(buffer.mkString)
      buffer.close()
      return v
    } else {
      Logger.error(s"Missing config file: $CONFIG_FILE")
      return JsNull // TODO This will crash if the file is missing, needs to be more robust
    }
  }

}
