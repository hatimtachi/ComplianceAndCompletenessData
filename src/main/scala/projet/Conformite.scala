package projet

import org.apache.spark.sql.DataFrame

import scala.collection.mutable.ListBuffer

class Conformite {


  def readRules(df:DataFrame,
                spark:org.apache.spark.sql.SparkSession,
                rule:String,
                dfAttributs:Array[String]): ListBuffer[DataFrame]  ={

    var res         = new ListBuffer[DataFrame]()
    val splitRule   = rule.split(" => ")
    val attributes = splitRule(0).split(" ∧ ")
    val condition   = splitRule(1).split(" ")

    if ((attributes.length > 1)&&(checkIfVarInCondition(splitRule(0),splitRule(1))>1)){

      val att1 = attributes(0).split(" ")
      val att2 = attributes(1).split(" ")

      val request: DataFrame = spark.sql("SELECT * FROM dataFrame")
      val listM = spark.sql("select * from dataFrame where attributs = '" + att1(1) + "'").collect().toList
      res += spark.sql("select * from dataFrame where attributs = '"+att1(1)+"'")
      var s = listM mkString("(", ",", ")")
      s = s replace("[","'")
      s = s replace("]","'")
      res += request.sqlContext.sql("select * from dataFrame where attributs = '"+att2(1)+"' AND values NOT IN "+s)
      res
    }else{
      getResulatFromRulesWhenValuesExists(df,spark,rule,dfAttributs,attributes,condition)
    }

  }



  def getResulatFromRulesWhenValuesExists(df:DataFrame,spark:org.apache.spark.sql.SparkSession,
                                          rule:String,
                                          dfAttributs:Array[String],
                                          attributes:Array[String],
                                          condition:Array[String]): ListBuffer[DataFrame] ={

    var localAttributes = attributes
    var res : DataFrame = spark.sql("SELECT * FROM dataFrame")
    var str = ""
    var resArray = new ListBuffer[DataFrame]()
    val posi = getIndexOfElementInList(localAttributes, condition(0))
    localAttributes = swapElementToPositionZero(localAttributes, posi)

    for (att <- localAttributes){
      val at = att.split(" ")

      if (att contains condition(0)){
        // execution du rules qu'on a une values a comparaitre

        str +="SELECT id FROM dataFrame WHERE "+dfAttributs(1)+" = '"+
          at(1)+"' AND NOT ("+dfAttributs(2)+condition(1)+checkType(condition(2))+")"

        res = res.sqlContext.sql("SELECT * FROM dataFrame WHERE "+dfAttributs(1)+" = '"+
          at(1)+"' AND NOT ("+dfAttributs(2)+condition(1)+checkType(condition(2))+")")
        resArray += res

      }else{
        // execution du rules ou il n'a pas de values a comparaitre
        val test = spark.sql(str).collect().toList
        var s = test mkString("(", ",", ")")
        s = s replace("[","'")
        s = s replace("]","'")
        if (s.length > 2){
          res = spark.sql("SELECT * FROM dataFrame WHERE "+dfAttributs(1)+" = '"+at(1)
            +"' AND "+dfAttributs(0)+" IN "+s)
          resArray += res
        }
      }
    }
    resArray
  }



  def swapElementToPositionZero(attributes:Array[String],position:Int): Array[String] ={
    attributes.updated(0,attributes(position)).updated(position,attributes(0))
  }

  def getIndexOfElementInList(attributes:Array[String],element: String): Int ={
    var res = -1
    for (att <- attributes){
      if(att contains element){
        res = attributes.indexOf(att)
      }
    }
    res
  }

  def checkIfVarInCondition(attributsRules:String,condition:String): Int ={

    val condiSplit = condition.split(" ")
    var count = 0
    for (condi <- condiSplit){

      if(attributsRules contains condi){
        count+=1
      }
    }
    count
  }


  def checkType(str: AnyRef): AnyRef = {
    str match {
      case s: String => "'" + str + "'"
      case d: Integer => str
      case _ => str
    }
  }
}
