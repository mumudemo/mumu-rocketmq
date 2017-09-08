# mumu-rocketmq分布式消息系统
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/babymm/mumu-rocketmq/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/com.weibo/motan.svg?label=Maven%20Central)](https://mvnrepository.com/search?q=motan) 
[![Build Status](https://travis-ci.org/mumudemo/mumu-rocketmq.svg?branch=master)](https://travis-ci.org/mumudemo/mumu-rocketmq)
[![OpenTracing-1.0 Badge](https://img.shields.io/badge/OpenTracing--1.0-enabled-blue.svg)](http://opentracing.io)
> &emsp; rocketmq 是由阿里巴巴开源出来的一个分布式消息服务器，rocketmq是在kafka的基础上进行重构，然后开发出来支撑阿里巴巴双十一高并发量的消息服务器。现在阿里巴巴已经将项目托管到apache基金会。  
> &emsp;相较于ActiveMQ、kafka、RabbitMQ等开源消息服务器，rocketmq增加了许多特性，如：消息事务、消息安序发送、消息快速存储等。如果想要了解更多请访问[Why RocketMQ](https://rocketmq.incubator.apache.org/docs/motivation/)。

![rocket比较图](http://note.youdao.com/yws/api/personal/file/613FFBA6388F4A2189D5FDB7ABBAF6B0?method=download&shareKey=7b6917ba72bb89354c2f2819f3ab1d4f)


## rocketmq 架构设计



## rocketmq 消费者监听
```
/**
** 接受消息
**/
public void receiveMessage() {
        //获取消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConfiguration.ROCKETMQ_GROUP);
        //设置namesrv地址【可以通过环境变量、运行参数来设置】
        consumer.setNamesrvAddr(RocketMQConfiguration.ROCKETMQ_NAMESRV);
        consumer.setVipChannelEnabled(false);
        //设置消费者最大最小线程
        consumer.setConsumeThreadMax(20);
        consumer.setConsumeThreadMin(10);
        try {
            //订阅主题下的所有tag
            consumer.subscribe(RocketMQConfiguration.ROCKETMQ_TOPIC, "*");
            //程序第一次启动从消息队列头取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //随机获取队列中的消息，相应的是MessageListenerOrderly 按照消息发送顺序来接受消息
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext Context) {
                            Message msg = list.get(0);
                            System.out.println(msg.toString());
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            //启动消费端 线程会一直挂起 等待消息的到来
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
```
```
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
            for (int i = 0; i < 100; i++) {
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
```

```
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
```

## rocketmq 主题创建
>rocketmq主题创建建议通过mqadmin来创建。如果通过程序来创建，可能造成负载均衡失调问题。
```
sh mqadmin updateTopic  -c DefaultCluster -n 192.168.11.25:9876 -t babymm
-c 集群名称
-b broker地址（-c -b必填一个)
-n namesrv地址（必填）  
-t 主题名称（必填）  
-w 写队列（默认8个）
-r 读队列（默认8个）
```

## rocketmq 个人测试
由于环境有限rocketmq运行环境为
```
jdk: oracleJDK1.8
jvm: xms:512m xmx:512m
os : docker centos 7
其中namesrv、broker各一台 配置同上
```
运行结果  
`单线程同步发送10字节，测试时间60s`  
**800 TPS**  

`10线程同步发送10字节，测试时间60s`  
**7800 TPS**
 
`20线程同步发送10字节，测试时间60s`  
**11000 TPS**  

遇到的问题    
当线程增加到20的时候，发送消息的时候就开始报错了，说是发送消息超时，查看服务器的报错日志，也没有找到具体的原因。个人估计是namesrv分配的内存太小，无法处理太高的并发量。说起来也真是惭愧啊！没有好的测试服务器很影响功能测试，但是从测试结果来看，rocketmq还是一个很了不起的消息中间件。

## 参考资料
[Apache-rocketmq官方文档](https://rocketmq.incubator.apache.org)  
[分布式开放消息系统(RocketMQ)的原理与实践](http://www.jianshu.com/p/453c6e7ff81c)  
[芋道源码--rocketmq纯源码解析](http://www.yunai.me/categories/RocketMQ/?jianshu)  
[ 分布式消息队列RocketMQ源码分析](http://blog.csdn.net/chunlongyu/article/category/6638499)  
[rocketmq命令整理](http://jameswxx.iteye.com/blog/2091971)

## 联系方式
**以上观点纯属个人看法，如有不同，欢迎指正。  
email:<babymm@aliyun.com>  
github:[https://github.com/babymm](https://github.com/babymm)**