package com.lovecws.mumu.rocketmq.schedule;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 发送延迟消息
 */
public class RocketMQScheduleProcedure {

    public void sendScheduleMessage(String message){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        try {
            producer.start();
            Message msg=new Message(RocketMQConfiguration.ROCKETMQ_TOPIC,"schedule",null,message!=null?message.getBytes():null);
            //10s之后发送消息
            msg.setDelayTimeLevel(3);
            SendResult sendResult = producer.send(msg);
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
        new RocketMQScheduleProcedure().sendScheduleMessage("lovecws-schedule");
    }
}
