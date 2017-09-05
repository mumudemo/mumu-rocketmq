package com.lovecws.mumu.rocketmq.transaction;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class RocketMQTransactionConsumer {
    /**
     * 接收消息
     */
    public void receiveTransactionMessage() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConfiguration.ROCKETMQ_GROUP);
        consumer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeThreadMax(20);
        consumer.setConsumeThreadMin(10);
        try {
            consumer.subscribe(RocketMQConfiguration.ROCKETMQ_TOPIC, "transaction");
            //程序第一次启动从消息队列头取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext Context) {
                            Message msg = list.get(0);
                            System.out.println(msg.toString());
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RocketMQTransactionConsumer().receiveTransactionMessage();
    }
}
