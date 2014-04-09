package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.AceHelper

object AceController extends Controller {

  def getAvailableLangModes = Action {
    Ok(Json.toJson(AceHelper.supportedLangMode.toList.sorted))
  }
}
