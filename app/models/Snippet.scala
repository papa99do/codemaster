package models

import play.api.Play.current
import play.api.db.DB
import anorm._

case class Snippet (
  id: Long,
  title: String,
  description: String,
  tags: Seq[String],
  code: String
)

object Snippets {
  def create(snippet: Snippet) = DB.withTransaction { implicit connection =>
    val snippetId : Option[Long] = SQL("insert into snippets(title, description, code) values({title}, {description}, {code})").on(
      "title" -> snippet.title, "description" -> snippet.description, "code" -> snippet.code
    ).executeInsert()

    Tags.createSnippetTags(snippet.tags, snippetId.get)
  }
}
