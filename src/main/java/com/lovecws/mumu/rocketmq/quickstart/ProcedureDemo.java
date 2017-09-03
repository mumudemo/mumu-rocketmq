package com.lovecws.mumu.rocketmq.quickstart;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class ProcedureDemo {
    public static void main(String[] args) {
        DefaultMQProducer producer=new DefaultMQProducer("rocketmq");
        producer.setNamesrvAddr("192.168.0.25:9876");
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            //String createTopicKey = producer.getCreateTopicKey();
            //System.out.println(createTopicKey);
            //producer.createTopic(createTopicKey,"babymm",100);

            SendResult sendResult = producer.send(new Message("TopicTest", "lovecws".getBytes()));
            System.out.println(sendResult);
        } catch (MQClientException|InterruptedException| RemotingException|MQBrokerException e) {
            e.printStackTrace();
            System.exit(1);
        }finally {
            producer.shutdown();
        }
    }
}
