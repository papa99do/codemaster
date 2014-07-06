package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.Template
import play.api.Logger
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object TemplateController extends Controller {

  def save = Action.async(parse.json) { request =>
    request.body.validate[Template].map { template =>
      Template.save(template).map { lastError =>
        Logger.debug(s"Successfully saved template with LastError: $lastError")
        Ok(Json.obj("status" -> "ok"))
      }

    }.recoverTotal {
      e => {
        Logger.debug(s"Save template validation error: " + JsError.toFlatJson(e))
        Future.successful(BadRequest("invalid json"))
      }
    }
  }

  def load(mode: String) = Action.async { request =>
    val after : Option[String] = request.getQueryString("lastLoadedOn")
    Template.searchByMode(mode).map { templates =>
      Ok(Json.toJson(templates))
    }
  }

  def delete(id: String) = Action.async {
    Template.delete(id).map { lastError =>
      Logger.debug(s"Successfully deleted template '$id' with LastError: $lastError")
      Ok(Json.obj("status" -> "ok"))
    }
  }

}
