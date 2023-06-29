package com.linrushao.javamodel;

/**
 * @Author linrushao
 * @Date 2023-06-29
 */
public class SQLUtils {
    //*****Spark加载MongoDB的SQL语句*****
    public static final String SPARK_MONGODB_SQL = "com.mongodb.spark.sql";

    //*****Spark加载ElasticSearch的SQL语句*****
    public static final String SPARK_ELASTICSEARCH_SQL = "org.elasticsearch.spark.sql";

    //*****历史热门统计，历史评分数据最多，mid，count【电影评分数量，一部电影有多少人评分】*****
    public static final String RATE_MORE_SQL = "select mid, count(mid) as count from ratings group by mid order by count desc";

    //*****近期热门统计，按照“yyyyMM”格式选取最近的评分数据，统计评分个数*****
    public static final String RATE_YEAR_MONTH = "select mid, score double, timestamp as yearmonth from ratings";

    //*****从ratingOfMonth临时表中查找电影在各个月份的评分，mid，count，yearmonth*****
    public static final String RATE_MORE_RECENTLY_MOVIES = "select mid, count(mid) as count, yearmonth from ratingOfYearMonth group by yearmonth, mid order by yearmonth desc, count desc";

    //*****优质电影统计，统计电影的平均评分，mid，avg*****
    public static final String AVERAGE_MOVIES = "select mid, avg(score) as avg from ratings group by mid";
}
