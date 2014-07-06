package models

import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.{Format, Json}
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Future
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

case class Template (
  _id: Option[BSONObjectID],
  name: String,
  tabTrigger: String,
  content: String,
  mode: String
)

object Template {
  private def db = ReactiveMongoPlugin.db
  def collection: JSONCollection = db[JSONCollection]("templates")

  implicit val format: Format[Template] = Json.format[Template]

  def save(template: Template) =
    collection.save(Json.toJson(template))

  def delete(id: String) =
    collection.remove(Json.obj("_id" -> BSONObjectID(id)))

  def searchByMode(mode: String): Future[List[Template]] =
    collection.find(Json.obj("mode" -> mode)).cursor[Template].collect[List]()

}