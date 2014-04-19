package models

import play.api.Play.current
import play.api.db.DB
import anorm._

case class Template (
  id: Long,
  name: String,
  tabTrigger: String,
  content: String,
  mode: String
)

object Templates {

  def create(template: Template) = DB.withConnection { implicit connection =>
    SQL("insert into templates(name, tab_trigger, content, mode, last_updated_on) " +
      "values ({name}, {tabTrigger}, {content}, {mode}, now())").on(
        "name" -> template.name, "tabTrigger" ->  template.tabTrigger,
        "content" -> template.content, "mode" -> template.mode
      ).executeInsert()
  }

  def searchByMode(mode: String) : List[Template] = DB.withConnection { implicit connection =>
    SQL("select id, name, tab_trigger, content, mode from templates where mode = {mode}")
      .on("mode" -> mode)().map { row =>
      Template(row[Long]("id"), row[String]("name"), row[String]("tab_trigger"),
        row[String]("content"), row[String]("mode"))
    }.toList
  }

}