package com.lovecws.mumu.rocketmq.batch;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.List;

public class RocketMQBatchProcedure {

    /**
     * 批量发送消息
     * @return
     */
    public String sendMessage(){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            List<Message> messages=new ArrayList<Message>();
            Message message1=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"bactch",null,"lovecws1".getBytes());
            Message message2=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"bactch",null,"lovecws2".getBytes());
            Message message3=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"bactch",null,"lovecws3".getBytes());
            Message message4=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"bactch",null,"lovecws4".getBytes());
            Message message5=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"bactch",null,"lovecws5".getBytes());
            Message message6=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"bactch",null,"lovecws6".getBytes());
            messages.add(message1);
            messages.add(message2);
            messages.add(message3);
            messages.add(message4);
            messages.add(message5);
            messages.add(message6);
            SendResult sendResult = producer.send(messages);
            System.out.println(sendResult);
            return sendResult.toString();
        } catch (MQClientException|InterruptedException| RemotingException|MQBrokerException e) {
            e.printStackTrace();
            System.exit(1);
        }finally {
            producer.shutdown();
        }
        return null;
    }

    public static void main(String[] args) {
        new RocketMQBatchProcedure().sendMessage();
    }
}
