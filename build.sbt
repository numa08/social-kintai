name := "social-kintai"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-Xlint")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies ++= Seq(
  "com.github.aselab" %% "scala-activerecord" % "0.2.3",
  "org.slf4j" % "slf4j-nop" % "1.7.5",
  "com.h2database" % "h2" % "1.3.173"
)

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.1"

resolvers ++= Seq(
  "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "jquery" % "2.1.0-2",
  "org.webjars" % "font-awesome" % "4.0.3",
  "org.pac4j"   % "play-pac4j_scala" % "1.2.0",
  "org.pac4j" % "pac4j-oauth" % "1.5.1"
)

play.Project.playScalaSettings
