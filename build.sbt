name := "light-delay"

version := "0.1.0"

scalaVersion := "2.12.12"

enablePlugins(AkkaGrpcPlugin)
akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Scala)
akkaGrpcGeneratedSources := Seq(AkkaGrpc.Server)

inConfig(Compile)(Seq(
  PB.protoSources += baseDirectory.value / "protos"
))

/*PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
PB.protoSources in Compile := Seq(baseDirectory.value / "protos")

lazy val akkaVersion = "2.6.2"

libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)
*/

libraryDependencies ++= Seq (
  "ch.megard" %% "akka-http-cors" % "0.4.2"
)

mainClass in (Compile, run) := Some("io.github.ahappypie.LightDelay.LightDelayServer")

enablePlugins(AshScriptPlugin)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

maintainer in Docker := "Brian Bagdasarian"
dockerRepository := Some("quay.io")
dockerUsername := Some("ahappypie")
dockerBaseImage := s"openjdk:11.0.9.1-jre-buster"

//dockerExposedPorts := Seq(sys.env.getOrElse("GRPC_PORT", "50051").toInt)

//javaOptions in Universal ++= Seq("-J-XX:+UnlockExperimentalVMOptions", "-J-XX:+UseCGroupMemoryLimitForHeap")