package com.lovecws.mumu.rocketmq.filter;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class RocketMQFilterProcedure {

    /**
     * 发送消息
     * @return
     */
    public String sendFilterMessage(){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            for (int i = 0; i <10 ; i++) {
                Message message1 = new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"filter",null, ("lovecws"+i).getBytes());
                message1.putUserProperty("a",String.valueOf(i));
                SendResult sendResult = producer.send(message1);
                System.out.println(sendResult);
            }
            return null;
        } catch (MQClientException |InterruptedException| RemotingException |MQBrokerException e) {
            e.printStackTrace();
            System.exit(1);
        }finally {
            producer.shutdown();
        }
        return null;
    }

    public static void main(String[] args) {
        new RocketMQFilterProcedure().sendFilterMessage();
    }
}
