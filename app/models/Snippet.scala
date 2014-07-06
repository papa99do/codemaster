package models

import play.api.Play.current
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, Format}
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.ReactiveMongoPlugin
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.json.collection.JSONCollection

case class Snippet (
  _id: Option[BSONObjectID],
  title: String,
  description: Option[String],
  tags: Seq[String],
  langMode: String,
  code: Option[String])

object Snippet {
  private def db = ReactiveMongoPlugin.db
  def collection: JSONCollection = db[JSONCollection]("snippets")

  implicit val format: Format[Snippet] = Json.format[Snippet]

  def searchByTags(tags: Seq[String]): Future[List[Snippet]] =
    collection.find(Json.obj("tags" -> Json.obj("$all" -> tags)), Json.obj("code" -> 0))
      .cursor[Snippet].collect[List]()

  def getCode(id: String): Future[String] =
    collection.find(Json.obj("_id" -> BSONObjectID(id)))
      .cursor[Snippet].collect[List](upTo = 1).map{_(0).code.getOrElse("")}

  def save(snippet: Snippet) = collection.save(Json.toJson(snippet))

  def delete(id: String) = collection.remove(Json.obj("_id" -> BSONObjectID(id)))
}
