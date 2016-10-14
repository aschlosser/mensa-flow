package controllers

import javax.inject._

import play.api.mvc._
import services.{Config, Database}
import play.api.mvc.Cookie
import play.mvc.Http

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {request => handleIndex(request)}
  private def handleIndex(request: Request[AnyContent]): Result = {
      return request.cookies.get(Config.sessioncookie) match {
        case None => TemporaryRedirect("/signin")
        case Some(x) => handleIndex0(x.value)
      }
  }

  private def handleIndex0(session: String): Result = {
    val db = Database.connect()
    try {
      db.checkSession(session) match {
        case None => TemporaryRedirect("/signin")
        case Some(x) => Ok(views.html.index(s"Hello ${x.name}!"))
      }
    } finally {
      db.close()
    }
  }

}
