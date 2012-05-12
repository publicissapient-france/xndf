import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "Xndf"
    val appVersion      = "1.0"

    val resolvers = Seq("scala-tools release" at "http://scala-tools.org/repo-releases/")
    val appDependencies = Seq(
      "org.mongodb" %% "casbah" % "3.0.0-M2",
      "de.flapdoodle.embedmongo" % "de.flapdoodle.embedmongo" % "1.11"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
    )

}
