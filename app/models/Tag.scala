package models

import play.api.Play.current
import play.api.db.DB
import anorm._

case class Tag(id: Long, name: String)

case class SnippetTag(snippetId: Long, tagId: Long)

object Tags {

  def createSnippetTags(tags: Seq[String], snippetId: Long) = DB.withConnection { implicit  connection =>
    for (tag: String <- tags) {
      SQL("insert into snippet_tags values({snippetId}, {tagId})").on(
        "snippetId" -> snippetId, "tagId" -> getTagIdByName(tag).get
      ).executeInsert()
    }
  }

  def getTagIdByName(tag: String) : Option[Long] = DB.withConnection { implicit connection =>
    SQL("select id from tags where name = {tag}")
      .on("tag" -> tag.toLowerCase()).as(SqlParser.scalar[Long].singleOpt) match {
      case Some(id: Long) => Some(id)
      case None => SQL("insert into tags(name) value({name})").on("name" -> tag).executeInsert()
    }
  }

}
