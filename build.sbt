lazy val root = (project in file("."))
  .settings(
    name := "libgraphblas-native",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.12" % Test,
      "net.java.dev.jna" % "jna" % "5.3.1",
      "com.nativelibs4java" % "jnaerator-runtime" % "0.12",
      "org.typelevel" %% "cats-effect" % "1.3.1",
      "com.github.mpilquist" %% "simulacrum" % "0.19.0"
    ),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-Xfatal-warnings", // New lines for each options
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    )
  )