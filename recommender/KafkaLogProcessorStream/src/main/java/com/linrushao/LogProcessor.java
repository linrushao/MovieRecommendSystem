package com.linrushao;


import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import static com.linrushao.javamodel.Constant.MOVIE_RATING_PREFIX;

/**
 * @Author LRS
 * @Date 2022/9/19 10:03
 * Desc 设置Kafka日志程序过滤
 */
public class LogProcessor implements Processor<byte[], byte[]> {
    //上下文
    private ProcessorContext context;
    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
    }
    @Override
    public void process(byte[] dummy, byte[] line) {
        // 把收集到的日志信息用string表示
        String input = new String(line);
        // 根据前缀MOVIE_RATING_PREFIX:从日志信息中提取评分数据
        if( input.contains(MOVIE_RATING_PREFIX) ){
            System.out.println("电影评分数据>>>>>>>>>>>" + input);
            input = input.split(MOVIE_RATING_PREFIX)[1].trim();
            context.forward( "logProcessor".getBytes(), input.getBytes() );
        }
    }
    @Override
    public void punctuate(long l) {

    }

    @Override
    public void close() {

    }
}
