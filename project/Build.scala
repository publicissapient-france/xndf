import sbt._
import PlayProject._
import Keys._

object ApplicationBuild extends Build {

  val appName = "Xndf"
  val appVersion = "1.0"
  val embed_mongo: ModuleID = "de.flapdoodle.embedmongo" % "de.flapdoodle.embedmongo" % "1.11"
  val play_salat_plugin: ModuleID = "se.radley" %% "play-plugins-salat" % "1.0.9"
  val gridFs: ModuleID = "org.mongodb" %% "casbah-gridfs" % "2.4.1"
  val apache_poi = "org.apache.poi" % "poi" % "3.8"

  val appDependencies = {
    Seq(
      embed_mongo, play_salat_plugin, gridFs, apache_poi
    )
  }

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers ++= Seq(
      "scala-tools release" at "http://scala-tools.org/repo-releases/",
      "sonatype release" at "https://oss.sonatype.org/content/repositories/releases",
      "OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"),
    // Add your own project settings here
    routesImport += "se.radley.plugin.salat.Binders._",
    //scalacOptions ++= Seq("-Xlog-implicits"),
    templatesImport += "org.bson.types.ObjectId"
  )

}
