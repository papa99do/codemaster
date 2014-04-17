package models

object AceHelper {

  val supportedLangMode = Set(
    "powershell","mysql","text","tcl","c9search","c_cpp","jsx","rdoc","typescript","csharp",
    "coffee","livescript","batchfile","ocaml","curly","rhtml","objectivec","diff","verilog",
    "xquery","ada","r","golang","snippets","haxe","dart","svg","lsl","jade","properties","ejs",
    "erlang","markdown","perl","tmsnippet","sh","tex","twig","makefile",
    "pascal","dot","d","coldfusion","cobol","actionscript","php","assembly_x86","groovy","jsp",
    "latex","asciidoc","scss","ftl","python","textile","ruby","jsoniq","julia","yaml","glsl",
    "rust","prolog","logiql","scheme","abap","autohotkey","mushcode","lucene","stylus","scala",
    "html","sass","javascript","ini","scad","clojure","haml","java","plain_text","css","django",
    "haskell","liquid","json","toml","less","xml","sql","luapage","forth","pgsql","vbscript",
    "lua","matlab","lisp","velocity","html_ruby"
  )

  val langModeAliasMap = Map(
    "js" -> "javascript"
  )

  def guessLangMode(tags: Seq[String]): String = {
    tags.map(langMode(_)).filter(_.isDefined).toList match {
      case (x::xs) => x.get
      case Nil => "text"
    }
  }

  private def langMode(tag: String): Option[String] = {
    if (supportedLangMode.contains(tag)) Some(tag) else langModeAliasMap.get(tag)
  }

}
