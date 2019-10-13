lazy val root = (project in file("."))
  .settings(
    name := "libgraphblas-native",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.12" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
      "net.java.dev.jna" % "jna" % "5.3.1",
      "com.nativelibs4java" % "jnaerator-runtime" % "0.12",
      "org.typelevel" %% "cats-effect" % "1.3.1",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
      "org.scalactic" %% "scalactic" % "3.0.8",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "com.github.mpilquist" %% "simulacrum" % "0.19.0"
    ),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-verbosity", "2"),
    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    )
  )