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

  def update(snippet: Snippet) = DB.withTransaction { implicit connection =>
    SQL("update snippets set title = {title}, description = {description}, code = {code} where id = {id}")
      .on("title" -> snippet.title, "description" -> snippet.description, "code" -> snippet.code, "id" -> snippet.id)
      .executeUpdate()

    Tags.deleteSnippetTags(snippet.id)
    Tags.createSnippetTags(snippet.tags, snippet.id)
    Tags.deleteEmptyTags()
  }

  def search(tags: String): List[Snippet] = DB.withConnection {
    implicit connection =>

    var ids : Set[Long] = null
    tags.split(" ").foreach { tag =>
      if (ids == null) {
        ids = Tags.getSnippetIdsByTag(tag)
      } else {
        ids = ids & Tags.getSnippetIdsByTag(tag)
      }
    }

    if (ids.isEmpty) return List()

    val query = SQL( """
      select s.id, s.title, s.description, group_concat(t.name) tags
      from snippets s, tags t, snippet_tags st
      where s.id = st.snippet_id
      and st.tag_id = t.id
      and s.id in (%s)
      group by s.id""".format(ids.mkString(",")))

    query().map { row =>
      Snippet(row[Long]("id"), row[String]("title"), row[String]("description"), row[String]("tags").split(","))
    }.toList
  }

  def getCode(id: Long): String = DB.withConnection { implicit connection =>
    SQL("select code from snippets where id = {id}").on("id" -> id).as(SqlParser.scalar[String].single)
  }
}
