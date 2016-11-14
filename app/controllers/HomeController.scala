package controllers

import javax.inject._

import play.api.mvc._
import services.{Connection, Database}

@Singleton
class HomeController() extends Controller with Authentication {

  def index = Database.withConnection {
    implicit db: Connection => authenticatedAction {
      _ => user => Ok(views.html.index(s"Hello ${user.name}!"))
    }
  }

}
