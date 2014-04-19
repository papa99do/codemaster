package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{Template, Templates}

object TemplateController extends Controller {

  implicit val rds = (
    (__ \ 'name).read[String] and
    (__ \ 'tabTrigger).read[String] and
    (__ \ 'content).read[String] and
    (__ \ 'mode).read[String]
  ).tupled

  implicit val writes : Writes[Template] = (
    (__ \ 'id).write[Long] and
    (__ \ 'name).write[String] and
    (__ \ 'tabTrigger).write[String] and
    (__ \ 'content).write[String] and
    (__ \ 'mode).write[String]
  )(unlift(f = Template.unapply))

  def save(id: Option[Long]) = Action(parse.json) { request =>
    request.body.validate[(String, String, String, String)].map {
      case (name, tabTrigger, content, mode) => {
        Templates.create(Template(0L, name, tabTrigger, content, mode))
        Ok(Json.obj("status" -> "ok"))
      }
    }.recoverTotal {
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }

  def load(mode: String) = Action {
    Ok(Json.toJson(Templates.searchByMode(mode)))
  }



}
