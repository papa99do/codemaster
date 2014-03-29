package models

import play.api.Play.current
import play.api.db.DB
import anorm._

case class Snippet (
  id: Long,
  title: String,
  description: String,
  tags: Seq[String] = Nil,
  code: String = ""
)

object Snippets {
  def create(snippet: Snippet) = DB.withTransaction { implicit connection =>
    val snippetId : Option[Long] = SQL("insert into snippets(title, description, code) values({title}, {description}, {code})").on(
      "title" -> snippet.title, "description" -> snippet.description, "code" -> snippet.code
    ).executeInsert()

    Tags.createSnippetTags(snippet.tags, snippetId.get)
  }

  def search(tag: String): List[Snippet] = DB.withConnection {
    implicit connection =>
      val query = SQL( """
        select s.id, s.title, s.description
        from snippets s, tags t, snippet_tags st
        where s.id = st.snippet_id
        and st.tag_id = t.id
        and t.name = {tag}""").on("tag" -> tag)

    query().map { row =>
      Snippet(row[Long]("id"), row[String]("title"), row[String]("description"))
    }.toList
  }

  def getCode(id: Long): String = DB.withConnection { implicit connection =>
    SQL("select code from snippets where id = {id}").on("id" -> id).as(SqlParser.scalar[String].single)
  }
}
