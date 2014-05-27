package models.kintai

import java.util.Date
import play.api.libs.functional.syntax._
import play.api.libs.json._
import akka.actor.Actor
import twitter4j.{ Paging, Twitter, Status }
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.annotation.tailrec

case class Kintai(syussya: Date, taisya: Date) {

  def toJson: JsValue = {
    implicit val writer: Writes[Kintai] = (
      (__ \ "start").write[Date] and
      (__ \ "end").write[Date])(unlift(Kintai.unapply))

    val json = Json.toJson(this)
    json
  }
}

class KintaiActor(tw: Twitter) extends Actor with KintaiFilter with KintaiCollection {

  def twitter = tw

  def receive = {
    case GetKintai()          => sender ! kintais
    case CollectionKintai(st) => sender ! collection(st)
  }
}

trait TwitterFilter {
  def twitter: Twitter

  def tweets(isEnd: Status => Boolean): List[Status] = {
    @tailrec
    def _tweets(ts: List[Status], p: Int, isEnd: Status => Boolean): List[Status] = {
      val nts = ts ++ twitter.getUserTimeline(new Paging(p, 200)).toList
      nts.last match {
        case s if isEnd(s) => nts
        case _             => _tweets(nts, p + 1, isEnd)
      }
    }

    _tweets(List(), 1, isEnd)
  }
}

trait KintaiFilter extends TwitterFilter {

  val kintais: List[Either[Syussya, Taisya]] = tweets { s =>
    val oneMonthAgo = new Date(System.currentTimeMillis() - (30 days).toMillis).getTime
    s.getCreatedAt.getTime < oneMonthAgo
  }.filterNot(_.isRetweet)
    .collect {
      case s if s.getText == "ｼｭｯｼｬ" => Left(Syussya(s.getCreatedAt))
      case s if s.getText == "ﾀｲｼｬ"  => Right(Taisya(s.getCreatedAt))
    }

}

trait KintaiCollection {
  def collection(kintais: List[Either[Syussya, Taisya]]): List[Kintai] = {
    val syussyas = kintais.collect { case Left(s) => s }
    val taisyas = kintais.collect { case Right(t) => t }
    syussyas.zip(taisyas).map(st => Kintai(st._1.d, st._2.d))
  }
}

case class Syussya(d: Date)
case class Taisya(d: Date)