package controllers

import play.api.mvc._
import org.pac4j.play.scala.ScalaController
import org.pac4j.oauth.profile.twitter.TwitterProfile
import models.twiter.TwitterAuthConfig
import twitter4j.auth.AccessToken
import twitter4j.TwitterFactory
import scala.collection.JavaConversions._

object Application extends ScalaController with TwitterAuthConfig {

  def index = Action { request =>
    val newSession = getOrCreateSessionId(request)
    val content = Option(getUserProfile(request)).fold(getRedirectAction(request, newSession, "TwitterClient", "/").getLocation) { p =>
      val tp = p.asInstanceOf[TwitterProfile]
      val ac = new AccessToken(tp.getAccessToken, tp.getAccessSecret)

      val tw = TwitterFactory.getSingleton
      tw.setOAuthConsumer(consumer.key, consumer.secret)
      tw.setOAuthAccessToken(ac)
      tw.getUserTimeline.map(_.getText).mkString("\n")
    }
    Ok(content)
  }
}