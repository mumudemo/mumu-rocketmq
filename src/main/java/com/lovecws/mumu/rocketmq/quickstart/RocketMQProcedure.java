package com.lovecws.mumu.rocketmq.quickstart;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class RocketMQProcedure {

    /**
     * 发送消息【同步】
     * @param message
     * @return
     */
    public String sendMessage(String message){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            for (int i = 0; i < 10; i++) {
                SendResult sendResult = producer.send(new Message(RocketMQConfiguration.ROCKETMQ_TOPIC, message!=null?message.getBytes():null));
                System.out.println(sendResult);
            }
            return null;
        } catch (MQClientException|InterruptedException| RemotingException|MQBrokerException e) {
            e.printStackTrace();
            System.exit(1);
        }finally {
            producer.shutdown();
        }
        return null;
    }

    /**
     * 发送消息【异步】
     * @param message
     * @return
     */
    public String sendAsyncMessage(String message){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP+"123");
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            producer.send(new Message(RocketMQConfiguration.ROCKETMQ_TOPIC, message != null ? message.getBytes() : null), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("消息发送成功"+sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("消息发送失败"+throwable.getLocalizedMessage());
                }
            });
            return null;
        } catch (MQClientException|InterruptedException| RemotingException e) {
            e.printStackTrace();
            System.exit(1);
        }finally {
            producer.shutdown();
        }
        return null;
    }

    /**
     * 发送消息【one way】
     * @param message
     * @return
     */
    public String sendOneWayMessage(String message){
        DefaultMQProducer producer=new DefaultMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP+"123456");
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            producer.sendOneway(new Message(RocketMQConfiguration.ROCKETMQ_TOPIC, message != null ? message.getBytes() : null));
            return null;
        } catch (MQClientException|InterruptedException| RemotingException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            producer.shutdown();
        }
        return null;
    }

    public static void main(String[] args) {
        new RocketMQProcedure().sendMessage("lovecws");
    }
}
