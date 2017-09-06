package com.lovecws.mumu.rocketmq.quickstart;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageQueueListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RocketMQConsumer {

    /**
     * 接收消息
     */
    public void receiveMessage() {
        //获取消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConfiguration.ROCKETMQ_GROUP);
        //设置namesrv地址【可以通过环境变量、运行参数来设置】
        consumer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        consumer.setVipChannelEnabled(false);
        //设置消费者最大最小线程
        consumer.setConsumeThreadMax(20);
        consumer.setConsumeThreadMin(10);
        try {
            //订阅主题下的所有tag
            consumer.subscribe(RocketMQConfiguration.ROCKETMQ_TOPIC, "*");
            //程序第一次启动从消息队列头取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //随机获取队列中的消息，相应的是MessageListenerOrderly 按照消息发送顺序来接受消息
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext Context) {
                            Message msg = list.get(0);
                            System.out.println(msg.toString());
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            //启动消费端 线程会一直挂起 等待消息的到来
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用拉消息的模式获取消息
     */
    public void receivePullMessage(){
        DefaultMQPullConsumer consumer=new DefaultMQPullConsumer(RocketMQConfiguration.ROCKETMQ_GROUP);
        consumer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        try {
            Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues(RocketMQConfiguration.ROCKETMQ_TOPIC);
            System.out.println(messageQueues);
            consumer.registerMessageQueueListener(RocketMQConfiguration.ROCKETMQ_TOPIC, new MessageQueueListener() {
                @Override
                public void messageQueueChanged(String s, Set<MessageQueue> set, Set<MessageQueue> set1) {
                    System.out.println(s+" "+set+"  "+set1);
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RocketMQConsumer().receiveMessage();
    }
}
