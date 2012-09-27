import sbt._
import PlayProject._
import Keys._

object ApplicationBuild extends Build {

  val appName = "Xndf"
  val appVersion = "1.0"
  val embed_mongo: ModuleID = "de.flapdoodle.embedmongo" % "de.flapdoodle.embedmongo" % "1.11"
  val play_salat_plugin: ModuleID = ("se.radley" %% "play-plugins-salat" % "1.0.9").excludeAll(ExclusionRule("play"))
  val gridFs: ModuleID = "org.mongodb" %% "casbah-gridfs" % "2.4.1"
  val apache_poi = "org.apache.poi" % "poi" % "3.8"
  val mailer="nl.rhinofly" %% "api-ses" % "1.0"

  val appDependencies = {
    Seq(
      embed_mongo, play_salat_plugin, gridFs, apache_poi,mailer
    )
  }

  val exclusions = Seq(
    ExclusionRule("net.sf.ehcache","ehcache-core"),
    ExclusionRule("com.h2database","h2"),
    ExclusionRule("org.avaje","ebean"),
    ExclusionRule("com.google.guava","guava"),
    ExclusionRule("com.google.code.findbugs","jsr305"),
    ExclusionRule("org.hibernate.javax.persistence","hibernate-jpa-2.0-api"),
    ExclusionRule("play","anorm_2.9.1")
  )
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers ++= Seq(
      "sonatype release" at "http://oss.sonatype.org/content/repositories/releases",
      "OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "Rhinofly Internal Release Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local"),
    // Add your own project settings here
    routesImport += "se.radley.plugin.salat.Binders._",
    //scalacOptions ++= Seq("-Xlog-implicits"),
    templatesImport += "org.bson.types.ObjectId" ,
    libraryDependencies ~=  { _.map {
      dep  => exclusions.foldLeft(dep){ (dep, ex) => dep.excludeAll(ex)}
    }}
  )

}
