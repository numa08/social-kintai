package models.kintai

import java.util.Date

trait Message

case class GetSyussya() extends Message
case class GetTaisya() extends Message
case class GetKintai(sl: List[Date], tl: List[Date])
