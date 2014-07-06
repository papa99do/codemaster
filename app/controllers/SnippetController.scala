package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.{Snippet}
import play.modules.reactivemongo.MongoController
import scala.concurrent.Future
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object SnippetController extends Controller with MongoController {

  def save = Action.async(parse.json) { request =>
    request.body.validate[Snippet].map { snippet =>
        Snippet.save(snippet).map { lastError =>
          Logger.debug(s"Successfully saved snippet with LastError: $lastError")
          Ok(Json.obj("status" -> "ok"))
        }
    }.recoverTotal {
      e => {
        Logger.debug(s"Save snippet validation error: " + JsError.toFlatJson(e))
        Future.successful(BadRequest("invalid json"))
      }
    }
  }

  def delete(id: String) = Action.async {
    Snippet.delete(id).map { lastError =>
      Logger.debug(s"Successfully deleted snippet '$id' with LastError: $lastError")
      Ok(Json.obj("status" -> "ok"))
    }

  }

  def search(query: String) = Action.async {
    Snippet.searchByTags(query.split(" ")).map { snippets =>
      Ok(Json.toJson(snippets))
    }
  }

  def viewCode(id: String) = Action.async {
    Snippet.getCode(id).map { code =>
      Ok(code)
    }
  }

}
