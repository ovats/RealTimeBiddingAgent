import Dependencies.Libraries

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.vbansal",
      scalaVersion := "2.13.4",
    )
  ),
  name := "real-time-bidding-agent",
  libraryDependencies ++= Libraries.akkaDeps ++ Libraries.logDeps ++ Libraries.testDeps ++ Libraries.configDeps,
)
