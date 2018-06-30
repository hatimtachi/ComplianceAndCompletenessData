package projet

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
