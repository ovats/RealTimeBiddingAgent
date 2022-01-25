import Dependencies.Versions._
import sbt._

object Dependencies {

  object Versions {
    lazy val akkaHttpVersion   = "10.2.7"
    lazy val akkaVersion       = "2.6.18"
    lazy val scalatestVersion  = "3.1.4"
    lazy val logbackVersion    = "1.2.3"
    lazy val pureConfigVersion = "0.17.1"
  }

  object Libraries {
    // Akka
    lazy val akkaHttp          = "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion
    lazy val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
    lazy val akkaActorTyped    = "com.typesafe.akka" %% "akka-actor-typed"     % akkaVersion
    lazy val akkaStream        = "com.typesafe.akka" %% "akka-stream"          % akkaVersion

    // Logs
    lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion

    // For managing conf files
    lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % pureConfigVersion

    // Tests
    lazy val akkaHttpTest  = "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion  % Test
    lazy val akkaActorTest = "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion      % Test
    lazy val scalaTest     = "org.scalatest"     %% "scalatest"                % scalatestVersion % Test

    lazy val akkaDeps   = Seq(akkaHttp, akkaHttpSprayJson, akkaActorTyped, akkaStream)
    lazy val logDeps    = Seq(logbackClassic)
    lazy val configDeps = Seq(pureConfig)
    lazy val testDeps   = Seq(akkaHttpTest, akkaActorTest, scalaTest)
  }

}
