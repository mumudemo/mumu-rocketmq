package com.lovecws.mumu.rocketmq.broadcast;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class RocketMQBroadcastProcedure {

    public void sendBroadcastMessage(String message){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        try {
            producer.start();
            SendResult sendResult = producer.send(new Message(RocketMQConfiguration.ROCKETMQ_TOPIC, "tag1", "OrderID188", message != null ? message.getBytes() : null));
            System.out.println(sendResult);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

    public static void main(String[] args) {
        new RocketMQBroadcastProcedure().sendBroadcastMessage("lovecws");
    }
}
