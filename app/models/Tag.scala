package models

import play.api.Play.current
import play.api.db.DB
import anorm._

case class Tag(id: Long, name: String)

case class SnippetTag(snippetId: Long, tagId: Long)

object Tags {

  def createSnippetTags(tags: Seq[String], snippetId: Long) = DB.withConnection { implicit connection =>
    for (tag: String <- tags) {
      SQL("insert into snippet_tags values({snippetId}, {tagId})").on(
        "snippetId" -> snippetId, "tagId" -> getTagIdByName(tag).get
      ).executeInsert()
    }
  }

  def deleteSnippetTags(snippetId: Long) = DB.withConnection { implicit connection =>
    SQL("delete from snippet_tags where snippet_id = {snippetId}").on(
      "snippetId" -> snippetId
    ).execute()
  }

  def deleteEmptyTags() = DB.withConnection {implicit connection =>
    SQL("delete from tags where not exists (select * from snippet_tags where tag_id = id)").execute()
  }

  def getTagIdByName(tag: String) : Option[Long] = DB.withConnection { implicit connection =>
    SQL("select id from tags where name = {tag}")
      .on("tag" -> tag.toLowerCase()).as(SqlParser.scalar[Long].singleOpt) match {
      case Some(id: Long) => Some(id)
      case None => SQL("insert into tags(name) values({name})").on("name" -> tag).executeInsert()
    }
  }

  def getSnippetIdsByTag(tag: String): Set[Long] = DB.withConnection { implicit connection =>
    SQL("select distinct snippet_id from snippet_tags st, tags t where t.id = st.tag_id and t.name = {tag}")
      .on("tag" -> tag).as(SqlParser.long("snippet_id") *).toSet
  }

}
