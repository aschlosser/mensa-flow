package controllers

import model.User
import play.api.mvc._
import play.api.mvc.Results.TemporaryRedirect
import services.{Config, Connection, Database}

trait Authentication {

  protected def authenticatedAction(body: Request[AnyContent] => User => Result)(implicit db: Connection) = Action { request =>
    request.cookies.get(Config.sessioncookie).flatMap(cookie => db.checkSession(cookie.value)) match {
      case None => TemporaryRedirect(s"${Config.baseurl}/signin") //TODO add later redirect to target page
      case Some(user) => body(request)(user)
    }
  }

}
