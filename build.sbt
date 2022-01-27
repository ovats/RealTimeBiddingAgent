import Dependencies.Libraries

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.vbansal",
      scalaVersion := "2.13.4",
    )
  ),
  scalacOptions ++= Seq(
    "-unchecked",
    "-feature",
    "-deprecation",
    "-Xfatal-warnings",
    "-encoding",
    "utf8",
  ),
  name := "real-time-bidding-agent",
  libraryDependencies ++= Libraries.akkaDeps ++ Libraries.logDeps ++ Libraries.testDeps ++ Libraries.configDeps,
)
