name := "kafka-streams-worksops"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.11.0.1",
  "org.apache.kafka" % "kafka-streams" % "0.11.0.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "ch.qos.logback" % "logback-core" % "1.0.13",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.madewithtea" %% "mockedstreams" % "1.4.0" % "test",
  "com.google.code.gson" % "gson" % "2.8.2"
)

parallelExecution in Test := false
