import com.typesafe.sbt.osgi.SbtOsgi
import com.typesafe.sbt.osgi.SbtOsgi.autoImport._
import sbtrelease.ReleasePlugin
import com.typesafe.sbt.pgp.PgpKeys.publishSigned

val commonSettings = Seq(
  scalaVersion := "2.11.8",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

val dontPublishSettings = Seq(
   publishSigned := (),
   publish := ()
 )

lazy val sslConfigCore = project.in(file("ssl-config-core"))
  .settings(commonSettings: _*)
  .settings(osgiSettings: _*)
  .settings(
    name := "ssl-config-core",
    libraryDependencies ++= Dependencies.sslConfigCore,
    libraryDependencies ++= (
      scalaBinaryVersion.value match {
        case "2.10" => Seq.empty[ModuleID]
        case _      => Seq(Library.parserCombinators)
      }),
    libraryDependencies ++= Dependencies.testDependencies,
    OsgiKeys.bundleSymbolicName := s"${organization.value}.sslconfig",
    OsgiKeys.exportPackage := Seq(s"com.typesafe.sslconfig.*;version=${version.value}"),
    OsgiKeys.importPackage := Seq("!sun.misc", "!sun.security.*", configImport(), "*")
  ).enablePlugins(ReleasePlugin, SbtOsgi)

lazy val documentation = project.in(file("documentation"))
  .settings(dontPublishSettings: _*)

//lazy val sslConfigPlay = project.in(file("ssl-config-play"))
//  .dependsOn(sslConfigCore)
//  .settings(commonSettings: _*)
//  .settings(
//    name := "ssl-config-play",
//    libraryDependencies ++= Dependencies.sslConfigPlay
//  ).enablePlugins(ReleasePlugin)

lazy val root = project.in(file("."))
  .aggregate(
    sslConfigCore,
//    sslConfigPlay,
    documentation)
  .settings(dontPublishSettings: _*)


// JDK6: 1.2.0, Akka 2.4: 1.3.0
def configImport(packageName: String = "com.typesafe.config.*") = versionedImport(packageName, "1.2.0", "1.4.0")
def versionedImport(packageName: String, lower: String, upper: String) = s"""$packageName;version="[$lower,$upper)""""
