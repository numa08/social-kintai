package controllers

import play.api.mvc._
import org.pac4j.play.scala.ScalaController
import org.pac4j.oauth.profile.twitter.TwitterProfile
import models.twiter.TwitterAuthConfig
import models.{ NavigationBar, AuthorizedNavigationBar, UnAuthorizedNavigationBar }

object Application extends ScalaController with TwitterAuthConfig {

  def index = Action { request =>
    val newSession = getOrCreateSessionId(request)
    val content = Option(getUserProfile(request)).fold[NavigationBar] {
      val url = getRedirectAction(request, newSession, "TwitterClient", "/?1").getLocation
      UnAuthorizedNavigationBar(url)
    } { p =>
      val tp = p.asInstanceOf[TwitterProfile]
      val name = tp.getDisplayName
      val avatar = tp.getPictureUrl
      AuthorizedNavigationBar(name, avatar)
    }
    Ok(views.html.index(content))
  }
}