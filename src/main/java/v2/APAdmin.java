/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

/**
 *
 * @author Flashed
 */
public class APAdmin {
    
    
       
    private static final String EXCHANGE_NAME = "topic_laks";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("datdb.cphbusiness.dk");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "topic");
    String queueName = channel.queueDeclare().getQueue();
    String bindingKey = "AP.*";



      channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
        //REPLY HERE
        String response = "You are in, breh";
        BasicProperties replyProps = new BasicProperties
                                     .Builder()
                                     .correlationId(properties.getCorrelationId())
                                     .build();
        channel.basicPublish( "", properties.getReplyTo(), replyProps, response.getBytes());
      }
    };
    channel.basicConsume(queueName, true, consumer);
  }
    
    
}
