package com.lovecws.mumu.rocketmq.order;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.apache.rocketmq.remoting.common.RemotingHelper.DEFAULT_CHARSET;

public class RocketMQOrderProcedure {

    private static DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
    static {
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void sendOrderMessage(String message,int orderId){
        try {
            Message msg = new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,message!=null?message.getBytes():null);
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    System.out.println(mqs);
                    System.out.println(msg);
                    System.out.println(arg);
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);
            System.out.println(sendResult+"\n");
        } catch (MQClientException|InterruptedException|RemotingException|MQBrokerException e) {
            e.printStackTrace();
            System.exit(1);
        }
        producer.shutdown();
    }

    public static void main(String[] args) {
        RocketMQOrderProcedure orderProcedure = new RocketMQOrderProcedure();
        for (int i = 0; i < 10; i++) {
            int j=i+1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    orderProcedure.sendOrderMessage("lovecws"+j,1);
                }
            }).start();
        }
    }
}
