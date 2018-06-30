package projet

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import projet.Main.conformite

import scala.collection.mutable.ListBuffer

class Tools {


  /**
    * this function is used for getting data from file and return a RDD
    * @param sc sparkContext
    * @param path String
    * @param filedSep String
    * @return RDD
    */
  def readFile(sc : org.apache.spark.SparkContext,path:String,filedSep:String): RDD[(String, String, String)] ={
    var data    = sc.textFile(path)
    data        = data.filter(line => line.length() > 0)
    val dataset = data.map(line => {
      val id::attributs::values = line.replace("/","_").split(filedSep).toList
      (id, attributs, values mkString "")
    })
    dataset
  }


  /**
    * createDataFrameFromRDD is used for creation a DataFrame from RDD
    * @param spark SparkSession
    * @param rdd RDD
    * @param listOfNewNames Attributs Names
    * @return DataFrame
    */
  def createDataFrameFromRDD(spark:org.apache.spark.sql.SparkSession,rdd:RDD[(String,String,String)],listOfNewNames:Array[String]): DataFrame ={
    var df = spark.createDataFrame(rdd)
    val ar = df.columns
    for (i <- ar.indices){
      df = df.withColumnRenamed(ar(i),listOfNewNames(i))
    }
    df
  }


  /**
    * this function is for display the resulat
    * @param line the rule
    * @param dfs List of dataFrame
    */
  def display(line:String,dfs:ListBuffer[DataFrame]): Unit ={
    val splitRule   = line.split(" => ")

    if((dfs.length > 1)&& ( (conformite checkIfVarInCondition(splitRule(0),splitRule(1))) > 1)){

      dfs.head.join(dfs(1),"values").show()

    }else if (dfs.length > 1){
      dfs.head.join(dfs(1),"id").show()
    }else{
      dfs.head.show()
    }
  }

}
