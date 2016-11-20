package controllers

import model.User
import play.api.mvc._
import play.api.mvc.Results.TemporaryRedirect
import services.{Config, Connection}

trait Authentication {

  protected def authenticatedAction(con: Connection)(body: Request[AnyContent] => User => Result) = Action { request =>
    request.cookies.get(Config.sessioncookie).flatMap(cookie => con.checkSession(cookie.value)) match {
      case None => TemporaryRedirect(s"${Config.baseurl}/signin") //TODO add later redirect to target page
      case Some(user) => body(request)(user)
    }
  }

}
