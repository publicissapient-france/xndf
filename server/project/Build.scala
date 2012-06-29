import sbt._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "Xndf"
    val appVersion      = "1.0"

    val resolvers = Seq("scala-tools release" at "http://scala-tools.org/repo-releases/")
    val appDependencies = Seq(
      "de.flapdoodle.embedmongo" % "de.flapdoodle.embedmongo" % "1.11",
      "se.radley" %% "play-plugins-salat" % "1.0.6"
    )


    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      routesImport += "se.radley.plugin.salat.Binders._",
      //scalacOptions ++= Seq("-Xlog-implicits"),
      templatesImport += "org.bson.types.ObjectId"
    )

}
