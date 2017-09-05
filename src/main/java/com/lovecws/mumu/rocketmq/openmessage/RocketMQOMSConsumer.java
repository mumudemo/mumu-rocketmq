package com.lovecws.mumu.rocketmq.openmessage;

import io.openmessaging.*;
import io.openmessaging.rocketmq.domain.NonStandardKeys;

public class RocketMQOMSConsumer {

    public static void main(String[] args) {
        final MessagingAccessPoint messagingAccessPoint = MessagingAccessPointFactory
                .getMessagingAccessPoint("openmessaging:rocketmq://192.168.11.25:9876");

        final PullConsumer consumer = messagingAccessPoint.createPullConsumer("OMS_HELLO_TOPIC",
                OMS.newKeyValue().put(NonStandardKeys.CONSUMER_GROUP, "OMS_CONSUMER"));

        messagingAccessPoint.startup();
        System.out.printf("MessagingAccessPoint startup OK%n");

        consumer.startup();
        System.out.printf("Consumer startup OK%n");

        Message message = consumer.poll();
        if (message != null) {
            String msgId = message.headers().getString(MessageHeader.MESSAGE_ID);
            System.out.printf("Received one message: %s%n", msgId);
            consumer.ack(msgId);
        }

        consumer.shutdown();
        messagingAccessPoint.shutdown();
    }
}
