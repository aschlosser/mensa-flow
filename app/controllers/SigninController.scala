package controllers

import javax.inject._

import play.api.mvc.{Cookie, _}
import services.{Config, Database}

@Singleton
class SigninController() extends Controller {

  def showSigninPage() = Action {
    Ok(views.html.signin(false))
  }

  def handleSignin() = Action(parse.urlFormEncoded) { request => {
    val db = Database.connect()
    try {
      val params = request.body
      db.login(params.getOrElse("username",Seq("")).head, params.getOrElse("password",Seq("")).head) match {
        case Some(x) => Ok(views.html.signinsuccessful()).withCookies(Cookie(Config.sessioncookie, x.session))
        case None => Unauthorized(views.html.signin(true))
      }
    } finally {
      db.close()
    }
  }
  }

}
