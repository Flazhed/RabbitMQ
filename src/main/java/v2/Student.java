/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2;

/**
 *
 * @author Flashed
 */
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.QueueingConsumer;


public class Student {
    
 private static final String EXCHANGE_NAME = "topic_laks";

  public static void main(String[] argv) {
    Connection connection = null;
    Channel channel = null;
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("datdb.cphbusiness.dk");

      connection = factory.newConnection();
      channel = connection.createChannel();

      channel.exchangeDeclare(EXCHANGE_NAME, "topic");

      String routingKey = "AP.DAT";//getRouting(argv);
      String message = "Plz enroll me, breh";//getMessage(argv);
      String corrId = java.util.UUID.randomUUID().toString();
      String callbackQueueName = channel.queueDeclare().getQueue();
      String response = null;
      QueueingConsumer consumer = new QueueingConsumer(channel);
      channel.basicConsume(callbackQueueName, true, consumer);
      


      BasicProperties props = new BasicProperties
                            .Builder()
                            .replyTo(callbackQueueName)
                            .build();

      channel.basicPublish(EXCHANGE_NAME, routingKey, props, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
      
          while (true) {
              System.out.println("blocked?");
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
              System.out.println("unblocked");
              System.out.println(delivery);
              response = new String(delivery.getBody());
            System.out.println(response);
//        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
//            response = new String(delivery.getBody());
//            System.out.println(response);
//            break;
//        }
    }



    }
    catch  (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (connection != null) {
        try {
          connection.close();
        }
        catch (Exception ignore) {}
      }
    }
  }
    
}
