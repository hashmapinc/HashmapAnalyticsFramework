
name := "SparkFunctionAsServiceWithIgnite"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-sql_2.11" % "2.2.0",
  "org.apache.ignite" % "ignite-spark" % "2.3.0" excludeAll(ExclusionRule("org.apache.spark")),
  "com.github.scopt" %% "scopt" % "3.7.0"
)

        