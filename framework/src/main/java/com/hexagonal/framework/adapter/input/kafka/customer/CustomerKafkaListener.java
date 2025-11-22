package com.hexagonal.framework.adapter.input.kafka.customer;

import com.hexagonal.application.dto.*;
import com.hexagonal.application.port.in.customer.account.RegisterAccountInputPort;
import com.hexagonal.application.port.in.customer.cart.AddProductToCartInputPort;
import com.hexagonal.application.port.in.customer.checkout.CompletePurchaseInputPort;

import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Customer;
import com.hexagonal.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Event Listener for Customer Events
 * 
 * Listens to customer-related events from Kafka topics:
 * - customer.register
 * - customer.add-to-cart
 * - customer.complete-purchase
 */
@Component
public class CustomerKafkaListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerKafkaListener.class);
    
    private final ObjectMapper objectMapper;
    private final RegisterAccountInputPort registerAccountInputPort;
    private final AddProductToCartInputPort addProductToCartInputPort;
    private final CompletePurchaseInputPort completePurchaseInputPort;
    
    public CustomerKafkaListener(
            ObjectMapper objectMapper,
            RegisterAccountInputPort registerAccountInputPort,
            AddProductToCartInputPort addProductToCartInputPort,
            CompletePurchaseInputPort completePurchaseInputPort) {
        this.objectMapper = objectMapper;
        this.registerAccountInputPort = registerAccountInputPort;
        this.addProductToCartInputPort = addProductToCartInputPort;
        this.completePurchaseInputPort = completePurchaseInputPort;
    }
    
    @KafkaListener(topics = "customer.register", groupId = "richbank-customer-group")
    public void handleCustomerRegister(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        try {
            logger.info("Received customer registration event from topic: {}", topic);
            
            RegisterAccountCommand command = objectMapper.readValue(message, RegisterAccountCommand.class);
            Customer customer = registerAccountInputPort.execute(command);
            
            logger.info("Customer registered successfully: {}", customer.getId().getValue());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing customer registration event", e);
            // In production, you might want to send to a dead letter queue
        }
    }
    
    @KafkaListener(topics = "customer.add-to-cart", groupId = "richbank-customer-group")
    public void handleAddToCart(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        try {
            logger.info("Received add to cart event from topic: {}", topic);
            
            AddProductToCartCommand command = objectMapper.readValue(message, AddProductToCartCommand.class);
            Cart cart = addProductToCartInputPort.execute(command);
            
            logger.info("Product added to cart successfully: cartId={}", cart.getId().getValue());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing add to cart event", e);
        }
    }
    
    @KafkaListener(topics = "customer.complete-purchase", groupId = "richbank-customer-group")
    public void handleCompletePurchase(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        try {
            logger.info("Received complete purchase event from topic: {}", topic);
            
            CompletePurchaseCommand command = objectMapper.readValue(message, CompletePurchaseCommand.class);
            Order order = completePurchaseInputPort.execute(command);
            
            logger.info("Purchase completed successfully: orderId={}", order.getId().getValue());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing complete purchase event", e);
        }
    }
}

