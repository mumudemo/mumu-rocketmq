package com.lovecws.mumu.rocketmq.order;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 按照消息发送的顺序来接受消息
 */
public class RocketMQOrderConsumer {

    public void receiveOrderMessage(){
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(RocketMQConfiguration.ROCKETMQ_GROUP);
        consumer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
            consumer.subscribe(RocketMQConfiguration.ROCKETMQ_TOPIC,"*");
            //监听消息接受
            consumer.registerMessageListener(new MessageListenerOrderly() {
                AtomicLong consumeTimes = new AtomicLong(0);
                @Override
                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext consumeOrderlyContext) {
                    consumeOrderlyContext.setAutoCommit(false);
                    System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + new String(msgs.get(0).getBody()) + "%n");
                    System.out.println(consumeTimes.incrementAndGet());
                    if ((this.consumeTimes.get() % 2) == 0) {
                        return ConsumeOrderlyStatus.SUCCESS;
                    } else if ((this.consumeTimes.get() % 3) == 0) {
                        return ConsumeOrderlyStatus.ROLLBACK;
                    } else if ((this.consumeTimes.get() % 4) == 0) {
                        return ConsumeOrderlyStatus.COMMIT;
                    } else if ((this.consumeTimes.get() % 5) == 0) {
                        consumeOrderlyContext.setSuspendCurrentQueueTimeMillis(3000);
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RocketMQOrderConsumer().receiveOrderMessage();
    }
}
