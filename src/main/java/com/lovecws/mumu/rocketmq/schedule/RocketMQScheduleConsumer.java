package com.lovecws.mumu.rocketmq.schedule;

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

public class RocketMQScheduleConsumer {

    /**
     * 接收消息
     */
    public void receiveMessage(){
        DefaultMQPushConsumer consumer =new DefaultMQPushConsumer(RocketMQConfiguration.ROCKETMQ_GROUP);
        consumer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        consumer.setVipChannelEnabled(false);
        try {
            consumer.subscribe(RocketMQConfiguration.ROCKETMQ_TOPIC, "schedule");
            //程序第一次启动从消息队列头取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,ConsumeConcurrentlyContext Context) {
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

    public static void main(String[] args){
        new RocketMQScheduleConsumer().receiveMessage();
    }
}
