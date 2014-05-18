package models.kintai

import java.util.Date
import play.api.libs.functional.syntax._
import play.api.libs.json._
import akka.actor.Actor
import twitter4j.{ Query, Twitter, Status }
import scala.collection.JavaConversions._
import java.text.SimpleDateFormat
import scala.concurrent.duration._

case class Kintai(syussya: Date, taisya: Date) {

  def toJson: JsValue = {
    implicit val writer: Writes[Kintai] = (
      (__ \ "start").write[Date] and
      (__ \ "end").write[Date])(unlift(Kintai.unapply))

    val json = Json.toJson(this)
    json
  }
}

class KintaiActor(tw: Twitter) extends Actor with SyussyaFilter with TaisyaFilter with KintaiCount {

  def twitter = tw

  def receive = {
    case GetSyussya()      => sender ! syussyaDates
    case GetTaisya()       => sender ! taisyaDates
    case GetKintai(sl, tl) => sender ! kintai(sl, tl)
  }
}

trait TwitterFilter {
  def twitter: Twitter

  def filter(query: Query)(filter: Status => Boolean): List[Status] = {
    Option {
      twitter.search(query)
        .getTweets
    }.map(_.filter(filter).toList).getOrElse(Nil)
  }
}

trait KintaiQuery {

  def query(query: String): Query = {
    val q = new Query(query)
    val df = new SimpleDateFormat("yyyy-MM-dd")
    q.setUntil(df.format(new Date(System.currentTimeMillis())))
    q.setSince {
      df.format {
        new Date(System.currentTimeMillis() - (30 days).toMillis)
      }
    }
    q
  }
}

trait SyussyaFilter extends TwitterFilter with KintaiQuery {
  val syussyaDates: List[Date] = filter(query("ｼｭｯｼｬ"))(_.getText == "ｼｭｯｼｬ").map(_.getCreatedAt)
}

trait TaisyaFilter extends TwitterFilter with KintaiQuery {
  val taisyaDates: List[Date] = filter(query("ﾀｲｼｬ"))(_.getText == "ﾀｲｼｬ").map(_.getCreatedAt)
}

trait KintaiCount {
  def kintai(syussya: List[Date], taisya: List[Date]): List[Kintai] = {
    syussya.zip(taisya).map(st => Kintai(st._1, st._2))
  }
}