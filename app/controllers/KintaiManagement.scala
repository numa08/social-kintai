package controllers

import org.pac4j.play.scala.ScalaController
import play.api.mvc.Action
import org.pac4j.oauth.profile.twitter.TwitterProfile
import models.twiter.TwitterAuthConfig
import twitter4j.auth.AccessToken
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._
import models.kintai._
import play.api.libs.json.JsArray
import play.api.libs.concurrent.Execution.Implicits._
import models.kintai.CollectionKintai
import models.kintai.Kintai
import akka.util.Timeout
import scala.concurrent.Future

object KintaiManagement extends ScalaController with TwitterAuthConfig {

  def kintai = Action.async { request =>
    val newSession = getOrCreateSessionId(request)
    Option(getUserProfile(request)).fold {
      Future(Forbidden(getRedirectAction(request, newSession, "TwitterClient", "/?1").getLocation))
    } { p =>
      val tp = p.asInstanceOf[TwitterProfile]
      val ac = new AccessToken(tp.getAccessToken, tp.getAccessSecret)
      val twitter = TwitterAuthConfig.twitter

      twitter.setOAuthAccessToken(ac)
      val actor = Akka.system.actorOf(Props(new KintaiActor(twitter)))
      implicit val timeout = Timeout(180 seconds)
      val kintai = actor ? GetKintai()

      val collection = for {
        st <- kintai.mapTo[List[Either[Syussya, Taisya]]]
      } yield {
        CollectionKintai(st)
      }
      val res = collection.flatMap(actor ? _)

      val json = for {
        kList <- res.mapTo[List[Kintai]]
      } yield {
        val json = kList.map(_.toJson)
        JsArray(json)
      }

      json.map(Ok(_))
    }
  }
}
