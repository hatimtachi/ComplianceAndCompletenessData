package projet

"""
Created by hatim tachi.
Copyright © 2018 hatim tachi. All rights reserved.
"""

import org.apache.spark.sql.SparkSession
import scala.io.Source
import org.apache.log4j.{Level, Logger}

object Main extends App {

  // To remove Logs
  val rootLogger = Logger.getRootLogger
  rootLogger.setLevel(Level.ERROR)



  val spark = SparkSession.builder
    .appName("Projet")
    .master("local[*]")
    .getOrCreate()


  val sc            = spark.sparkContext
  val tools         = new Tools
  val completude    = new Completude
  val conformite    = new Conformite
  val staticSchema  = Array("id", "attributs", "values")
  val dataset       = tools readFile(sc, "./src/main/data/personnes.data", " ")
  val df            = tools createDataFrameFromRDD(spark, dataset, staticSchema)

  sc.setLogLevel("ERROR")

  df.createTempView("dataFrame")

  // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

  var resCompletude = completude getCompletude spark
  resCompletude.show()

  // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

  val lines = Source.fromFile("./src/main/data/quality.rules").getLines.toList
  var att   = (completude getDistinctValuesFromDataFrame(df, "attributs")).collect().toList
  for (line <- lines) {

    printf(line+"\n")
    val dfs = conformite readRules(df, spark, line, staticSchema)

    tools display(line,dfs)
  }
}