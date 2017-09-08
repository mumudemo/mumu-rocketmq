package com.lovecws.mumu.rocketmq.benchmark;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 使用jmh测试工具 测试rocketmq发送消息的并发量
 */
public class RocketMQAsyncMessage {

    private static DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
    private static byte[] message=new byte[10];
    static {
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendFailed(0);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送异步消息
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void sendAsyncMessage() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        producer.send(new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,message), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                //System.out.println("receive message"+sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                //System.out.println("receive message error"+throwable.getLocalizedMessage());
            }
        });
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RocketMQAsyncMessage.class.getSimpleName())
                .warmupIterations(60)
                .measurementIterations(60)
                .forks(1)
                .threads(20)
                .build();
        new Runner(opt).run();
    }
}
