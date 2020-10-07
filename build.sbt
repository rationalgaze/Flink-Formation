
name := "bitcoin"

lazy val commonSettings = Seq(
  version := "0.0.1",
  organization := "com.altran",
  scalaVersion := "2.11.12",
  scalacOptions += "-target:jvm-1.8",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  test in assembly := {}
)

lazy val dependencies = new {
  val flinkVersion = "1.11.1"
  val flink = "org.apache.flink" %% "flink-scala" % flinkVersion % "provided"
  val flinkStreaming = "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided"
  val flinkKafkaConnector = "org.apache.flink" %% "flink-connector-kafka" % flinkVersion
  val flinkTableBridge = "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion
  val flinkTableCommon = "org.apache.flink" % "flink-table-common" % flinkVersion % "provided"
  val flinkTablePlannerBlink = "org.apache.flink" %% "flink-table-planner-blink" % flinkVersion % "provided"
  val flinkTablePlanner = "org.apache.flink" %% "flink-table-planner" % flinkVersion % "provided"
  val flinkClients = "org.apache.flink" %% "flink-clients" % flinkVersion
  val rabbitmq = "org.apache.flink" %% "flink-connector-rabbitmq" % flinkVersion
}

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalactic" %% "scalactic" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.4.0"
)

val flinkDependencies = Seq(
  dependencies.flink,
  dependencies.flinkStreaming,
  dependencies.flinkKafkaConnector,
  dependencies.flinkClients,
  dependencies.flinkTableBridge,
  dependencies.flinkTablePlanner,
  dependencies.flinkTablePlannerBlink,
  dependencies.flinkTableCommon,
  dependencies.rabbitmq
)

def assemblySettings = Seq(
  mainClass in assembly := Some(s"com.altran.mediation.jobs.Job${name.value.toLowerCase.capitalize}"),
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  assemblyJarName in assembly := s"otarie-streaming-${name.value.toLowerCase}-${version.value}.jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)

lazy val root = (project in file("."))
lazy val wiki = project.
  settings(commonSettings: _*).
  settings(
    name := "wiki",
    libraryDependencies ++= flinkDependencies
  )

lazy val bitcoin = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= flinkDependencies,
    libraryDependencies ++= testDependencies,
    assemblySettings,
  )