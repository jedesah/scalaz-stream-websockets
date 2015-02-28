
import spray.revolver.RevolverPlugin._

Revolver.settings

organization := "com.timperrett"

name := "scalaz-stream-websockets"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scalaz.stream" %% "scalaz-stream"               % "0.6a",
  "net.databinder"    %% "unfiltered-filter"           % "0.8.4",
  "net.databinder"    %% "unfiltered-netty-server"     % "0.8.4",
  "net.databinder"    %% "unfiltered-netty-websockets" % "0.8.4"
)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

mainClass in Revolver.reStart := Some("example.Server")

