package com.aiotico;


import org.springframework.beans.factory.annotation.Value;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;


@Configuration
public class MqttBeans {


	@Value("${mqtt.broker.host}")
	private String mqttBrokerHost;

	@Value("${mqtt.broker.port}")
	private int mqttBrokerPort;

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		MqttConnectOptions options = new MqttConnectOptions();

		options.setServerURIs(new String[] { "tcp://" + mqttBrokerHost + ":" + mqttBrokerPort });
		options.setUserName("bllraslen");
		String pass = "bll.raslen";
		options.setPassword(pass.toCharArray());
		options.setCleanSession(true);

		factory.setConnectionOptions(options);

		return factory;
	}
	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}
	
	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
				mqttClientFactory(), "#");

		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(2);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}
	
	
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
				/*if(topic.equals("myTopic")) {
					System.out.println("This is the topic");
				}*/
				System.out.println("mqtt topic : " + topic);
				System.out.println("mqtt port : " + mqttBrokerPort);
				System.out.println("mqtt host : " + mqttBrokerHost);
				System.out.println("mqtt message : " + message.getPayload());
				System.out.println("broker : " + "tcp://" + mqttBrokerHost + ":" + mqttBrokerPort);
				System.out.println("--------------------------");

			}

		};
	}
	@Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		String customClientId = "your_custom_client_id";  // Set your custom client ID here

		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(customClientId, mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("#");
		messageHandler.setDefaultRetained(false);

		// Print the MQTT client ID
		System.out.println("MQTT Client ID: " + customClientId);

		return messageHandler;
	}


}
