package com.lovecws.mumu.rocketmq.transaction;

import com.lovecws.mumu.rocketmq.config.RocketMQConfiguration;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * rocketmq实现事务消息
 * 开始事务之前
 */
public class RocketMQTransactionProcedure {

    public void sendTransactionMessage() {
        TransactionMQProducer producer = new TransactionMQProducer(RocketMQConfiguration.ROCKETMQ_GROUP);
        producer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        producer.setCheckThreadPoolMinSize(2);
        producer.setCheckThreadPoolMaxSize(2);
        producer.setCheckRequestHoldMax(2000);
        producer.setTransactionCheckListener(new RocketMQTransactionCheckListener());
        try {
            producer.start();
            RocketMQLocalTransactionExecuter localTransactionExecuter=new RocketMQLocalTransactionExecuter();
            for (int i = 0; i <10 ; i++) {
                Message msg = new Message(RocketMQConfiguration.ROCKETMQ_TOPIC, "transaction", null, ("Hello RocketMQ "+i).getBytes());
                //发送事务消息
                TransactionSendResult sendResult = producer.sendMessageInTransaction(msg, localTransactionExecuter, null);
                System.out.printf("%s%n", sendResult);
            }
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

    /**
     * 事务延迟没有收到确认【提交或者回滚】消息的时候，会扫描prepared消息，然后向consumer发送请求来确认消息是否要提交还是回滚。
     */
    public static class RocketMQTransactionCheckListener implements TransactionCheckListener{
        private AtomicInteger transactionIndex = new AtomicInteger(0);
        @Override
        public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
            System.out.printf("server checking TrMsg " + msg.toString() + "%n");
            int value = this.transactionIndex.getAndIncrement();
            if (value % 6 == 0) {
                throw new RuntimeException("Could not find db");
            } else if (value % 5 == 0) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            } else {
                return value % 4 == 0 ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.UNKNOW;
            }
        }
    }
    /**
     * 事务回调 预备消息发送成功之后 自动回调该方法 来执行自定义的事务操作
     */
    public static class RocketMQLocalTransactionExecuter implements LocalTransactionExecuter{
        private AtomicInteger transactionIndex = new AtomicInteger(0);
        @Override
        public LocalTransactionState executeLocalTransactionBranch(Message message, Object o) {
            int value = this.transactionIndex.getAndIncrement();
            //TODO 消息发送成功,在这里进行事务处理;事务处理成功 提交确认事务消息
            if(value%2==0){
                System.out.println(value+" :commit");
                return LocalTransactionState.COMMIT_MESSAGE;
            }else{
                System.out.println(value+" :rollback");
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }
    }

    public static void main(String[] args) {
        new RocketMQTransactionProcedure().sendTransactionMessage();
    }
}
