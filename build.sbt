name := "light-delay"

version := "0.0.1"

scalaVersion := "2.12.10"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
PB.protoSources in Compile := Seq(baseDirectory.value / "protos")

lazy val akkaVersion = "2.5.28"

libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)

mainClass in (Compile, run) := Some("io.github.ahappypie.LightDelay.LightDelayServer")

enablePlugins(AshScriptPlugin)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

maintainer in Docker := "Brian Bagdasarian"
dockerRepository := Some("quay.io")
dockerUsername := Some("ahappypie")
dockerBaseImage := s"openjdk:8-alpine"

dockerExposedPorts := Seq(sys.env.getOrElse("GRPC_PORT", "50051").toInt)

javaOptions in Universal ++= Seq("-J-XX:+UnlockExperimentalVMOptions", "-J-XX:+UseCGroupMemoryLimitForHeap")