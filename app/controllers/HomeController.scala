package controllers

import javax.inject._

import play.api.mvc._
import services.ConnectionProvider

class HomeController @Inject()(val db: ConnectionProvider) extends Controller with Authentication {


  def index = db.withConnection { con =>
    authenticatedAction(con) {
      _ => user => Ok(views.html.index(s"Hello ${user.name}!"))
    }
  }

}
