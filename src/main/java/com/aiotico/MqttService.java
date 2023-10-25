package com.aiotico;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class MqttService {
    private final MessageChannel mqttOutboundChannel;

    public MqttService(MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    // Publish a message to an MQTT topic
    public void publishToMqttTopic(String topic, String message) {
        Message<String> mqttMessage = MessageBuilder
                .withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MessageHeaders.CONTENT_TYPE, "text/plain")
                .build();
        mqttOutboundChannel.send(mqttMessage);
    }

    // Subscribe to MQTT topic and handle received messages
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void subscribeToMqttTopic(String message, @Header(MqttHeaders.RECEIVED_TOPIC) String topic) {
        // Handle the received message here
        System.out.println("Received message from topic: " + topic);
        System.out.println("Message content: " + message);
    }
}

