package projet

"""
Created by hatim tachi.
Copyright © 2018 hatim tachi. All rights reserved.
"""

import org.apache.spark.sql.DataFrame

class Completude {

  def getDistinctValuesFromDataFrame(df:DataFrame,attributs:String): DataFrame={
    df.select(attributs).distinct().toDF()
  }


  def getCompletude(spark:org.apache.spark.sql.SparkSession): DataFrame ={
    spark.sql("SELECT attributs, " +
      "(COUNT(id) * 100 /(SELECT COUNT(DISTINCT id) FROM dataFrame)) as Completude" +
      " FROM dataFrame GROUP BY attributs")
  }


}
