package com.linrushao.statistics

import com.linrushao.scalamodel.{GenresRecommendation, MongoConfig, Movies, Ratings, Recommendation}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.linrushao.javamodel.Constant._
import com.linrushao.javamodel.SQLUtils
import com.linrushao.scalamodel.ConfigParams.params


/**
 * @Author LRS
 * @Date 2022/9/10 8:34
 *       Desc
 */
object StatisticsRecommender {

  def main(args: Array[String]): Unit = {

    /**
     * 创建一个sparkConf
     */
    val conf = new SparkConf()
      .setAppName("StatisticsRecommender")
      .setMaster(params("spark.cores").asInstanceOf[String])
      .set("spark.executor.memory","2G")

    /**
     * 创建一个sparkSession
     */
    val spark = SparkSession.builder()
      .config(conf)
      .getOrCreate()

    import spark.implicits._

    /**
     * 使用mongdodb数据库
     */
    implicit val mongoConfig = MongoConfig(params("mongo.uri").asInstanceOf[String], params("mongo.db").asInstanceOf[String])
    /**
     * 加载需要用到的所有数据
     */
    val ratings = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format(SQLUtils.SPARK_MONGODB_SQL)
      .load()
      .as[Ratings]
      .cache

    val movies = spark
      .read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .format(SQLUtils.SPARK_MONGODB_SQL)
      .load()
      .as[Movies]
      .cache

    ratings.createOrReplaceTempView("ratings")

    // 1. 历史热门统计，历史评分数据最多，mid，count【电影评分数量，一部电影有多少人评分】
    val rateMoreDF = spark.sql(SQLUtils.RATE_MORE_SQL)

    // 2. 近期热门统计，按照“yyyyMM”格式选取最近的评分数据，统计评分个数
    //对原始数据做预处理，去掉uid
    val ratingOfYearMonth: DataFrame = spark.sql(SQLUtils.RATE_YEAR_MONTH)
    ratingOfYearMonth.registerTempTable("ratingOfYearMonth")
    // 从ratingOfMonth中查找电影在各个月份的评分，mid，count，yearmonth
    val rateMoreRecentlyMoviesDF: DataFrame = spark.sql(SQLUtils.RATE_MORE_RECENTLY_MOVIES)

    //3. 优质电影统计，统计电影的平均评分，mid，avg
    val averageMoviesDF: DataFrame = spark.sql(SQLUtils.AVERAGE_MOVIES)

    //4. 各类别电影Top统计
    // 定义所有类别
    val genres = List("动作","动画","冒险","喜剧","犯罪","奇幻","家庭","传记","历史","恐怖",
      "歌舞","悬疑","爱情","古装","科幻","运动","惊悚","战争","武侠","音乐","剧情")

    // 把平均评分加入movie表里，加一列，inner join
    val movieWithScore = movies.join(averageMoviesDF, "mid")
    // 为做笛卡尔积，把genres转成rdd
    val genresRDD = spark.sparkContext.makeRDD(genres)

    // 计算类别top，首先对类别和电影做笛卡尔积
    val genresTopMoviesDF = genresRDD.cartesian(movieWithScore.rdd)
      .filter{
        // 条件过滤，找出movie的字段genres值(Action|Adventure|Sci-Fi)包含当前类别genre(Action)的那些
        //过滤掉电影的类别不匹配的电影
        case (genre, movieRow) => movieRow.getAs[String]("genres").toLowerCase.contains( genre.toLowerCase )
      }
      .map{
            //将整个数据集的数据量减少，生成RDD[string,Iter[mid,avg]]
        case (genre, movieRow) => ( genre, ( movieRow.getAs[Int]("mid"), movieRow.getAs[Double]("avg") ) )
      }
      //将genres数据集中的相同的聚集
      .groupByKey()
      .map{
            //take 表示10条数据(在常量项中统一设置) 通过评分的大小进行数据的排序，然后将数据映射为对象
        case (genre, items) =>
          GenresRecommendation( genre, items.toList.sortWith(_._2>_._2)
            .map( item=> Recommendation(item._1, item._2)) )
      }.toDF()

    /**
     * 保存到mongodb
     */
    // 1. 历史热门统计，历史评分数据最多
    storeDFInMongoDB(rateMoreDF, MONGODB_RATE_MORE_MOVIES_COLLECTION)
//    2. 近期热门统计
    storeDFInMongoDB(rateMoreRecentlyMoviesDF, MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION)
    // 3. 优质电影统计，统计电影的平均评分
    storeDFInMongoDB(averageMoviesDF, MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION)
    // 4. 各类别电影Top统计
    storeDFInMongoDB(genresTopMoviesDF, MONGODB_GENRES_TOP_MOVIES_COLLECTION)

    //去除缓存
    ratings.unpersist()
    movies.unpersist()

    spark.stop()
  }

  /**
   * 保存到mongdodb
   * @param df
   * @param collection_name
   * @param mongoConfig
   */
  def storeDFInMongoDB(df: DataFrame, collection_name: String)(implicit mongoConfig: MongoConfig): Unit ={
    df.write
      .option("uri", mongoConfig.uri)
      .option("collection", collection_name)
      .mode("overwrite")
      .format(SQLUtils.SPARK_MONGODB_SQL)
      .save()
    println(collection_name+"数据保存成功")
  }
}
