package models.kintai

trait Message

case class GetSyussya() extends Message
case class GetTaisya() extends Message
case class GetKintai() extends Message
case class CollectionKintai(st: List[Either[Syussya, Taisya]]) extends Message
