package models.kintai

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import models.twiter.TwitterAuthConfig
import twitter4j.{ TwitterFactory, Twitter }
import play.api.Configuration
import com.typesafe.config.ConfigFactory
import java.io.File
import twitter4j.auth.AccessToken

@RunWith(classOf[JUnitRunner])
class KintaiFilterSpec extends Specification with TwitterAuthConfig {

  "Filter" should {
    "filtered status" in {
      val filter = new SyussyaFilter {
        def twitter = {
          val t = new TwitterFactory().getInstance()
          t.setOAuthConsumer(consumer.key, consumer.secret)
          val ac = new AccessToken(access._1, access._2)
          t.setOAuthAccessToken(ac)
          t
        }
      }
      println(filter.syussyaDates.mkString(","))
      filter.syussyaDates.isEmpty === false
    }
  }

  val access: (String, String) = {
    val config = Configuration(ConfigFactory.parseFileAnySyntax(new File("conf/pac4j.conf")))
    (for {
      key <- config.getString("test.twitter.access.token")
      secret <- config.getString("test.twitter.access.secret")
    } yield {
      (key, secret)
    }).getOrElse(throw new RuntimeException("conf/pac4j.conf is not define or format is invalid"))
  }

}
