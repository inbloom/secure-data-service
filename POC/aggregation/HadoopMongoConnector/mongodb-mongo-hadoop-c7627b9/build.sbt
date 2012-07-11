
name := "mongo-hadoop"

organization := "org.mongodb"

seq(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)


hadoopRelease in ThisBuild := "1.0.x"
