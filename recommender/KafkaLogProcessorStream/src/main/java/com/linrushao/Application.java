package com.linrushao;

/**
 * @Author LRS
 * @Date 2022/9/19 10:01
 * Desc 主运行程序
 */


import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;


import java.util.Properties;
import java.util.logging.Logger;

/**
 * kafka过滤程序
 */
public class Application {

    public static void main(String[] args) {
        String brokers = "hadoop201:9092";
        String zookeepers = "hadoop201:2181";
        // 输入和输出的topic
        String from = "log";//product
        String to = "recommender";//consume
        // 定义kafka streaming的配置
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "logFilter");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeepers);
        // 创建 kafka stream 配置对象
        StreamsConfig config = new StreamsConfig(settings);
        // 创建一个拓扑建构器
        TopologyBuilder builder = new TopologyBuilder();
        // 定义流处理的拓扑结构
        builder.addSource("SOURCE", from)
                .addProcessor("PROCESS", () -> new LogProcessor(), "SOURCE")
                .addSink("SINK", to, "PROCESS");
        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();
        System.out.println("Kafka stream started!>>>>>>>>>>>");
    }
}
