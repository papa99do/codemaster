package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{Snippet, Snippets}

object SnippetController extends Controller {

  implicit val rds = (
    (__ \ 'title).read[String] and
    (__ \ 'description).read[String] and
    (__ \ 'tags).read[String] and
    (__ \ 'code).read[String]
  ).tupled

  implicit val snippetWrites : Writes[Snippet] = (
    (__ \ 'id).write[Long] and
    (__ \ 'title).write[String] and
    (__ \ 'description).write[String] and
    (__ \ 'tags).write[Seq[String]] and
    (__ \ 'code).write[String]
  )(unlift(f = Snippet.unapply))

  def newSnippet = Action(parse.json) { request =>

    request.body.validate[(String, String, String, String)].map {
      case (title, description, tags, code) => {
        Snippets.create(Snippet(0L, title, description, tags.split(" "), code))
        Ok(title)
      }
    }.recoverTotal {
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }

  }

  def search(tag: String) = Action {
    Ok(Json.toJson(Snippets.search(tag)))
  }

  def viewCode(id: Long) = Action {
    Ok(Snippets.getCode(id))
  }

}
