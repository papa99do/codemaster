package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{Snippet, Snippets}

object SnippetController extends Controller {

  implicit val rds = (
    (__ \ 'title).read[String] and
    (__ \ 'description).readNullable[String] and
    (__ \ 'tags).read[String] and
    (__ \ 'code).read[String]
  ).tupled

  implicit val snippetWrites : Writes[Snippet] = (
    (__ \ 'id).write[Long] and
    (__ \ 'title).write[String] and
    (__ \ 'description).write[Option[String]] and
    (__ \ 'tags).write[Seq[String]] and
    (__ \ 'code).write[String] and
    (__ \ 'langMode).write[String]
  )(unlift(f = Snippet.unapply))

  def save(id: Option[Long]) = Action(parse.json) { request =>
    request.body.validate[(String, Option[String], String, String)].map {
      case (title, description, tags, code) => {
        id match {
          case (Some(idValue)) => Snippets.update(Snippet(idValue, title, description, tags.split(" "), code))
          case None => Snippets.create(Snippet(0L, title, description, tags.split(" "), code))
        }
        Ok(Json.obj("status" -> "ok"))
      }
    }.recoverTotal {
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }

  def delete(id: Long) = Action {
    Snippets.delete(id)
    Ok(Json.obj("status" -> "ok"))
  }

  def search(tags: String) = Action {
    Ok(Json.toJson(Snippets.search(tags)))
  }

  def viewCode(id: Long) = Action {
    Ok(Snippets.getCode(id))
  }

}
