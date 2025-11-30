package com.hexagonal.framework.adapter.input.queue.kafka.customer;

import com.hexagonal.application.dto.*;
import com.hexagonal.application.port.in.customer.account.RegisterAccountUseCase;
import com.hexagonal.application.port.in.customer.cart.AddProductToCartUseCase;
import com.hexagonal.application.port.in.customer.checkout.CompletePurchaseUseCase;

import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.framework.adapter.input.queue.kafka.QueueAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Kafka Event Listener for Customer Events
 * 
 * Listens to customer-related events from Kafka topics:
 * - customer.register
 * - customer.add-to-cart
 * - customer.complete-purchase
 */
@QueueAdapter
public class CustomerKafkaListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerKafkaListener.class);
    
    private final ObjectMapper objectMapper;
    private final RegisterAccountUseCase registerAccountUseCase;
    private final AddProductToCartUseCase addProductToCartUseCase;
    private final CompletePurchaseUseCase completePurchaseUseCase;
    
    public CustomerKafkaListener(
            ObjectMapper objectMapper,
            RegisterAccountUseCase registerAccountUseCase,
            AddProductToCartUseCase addProductToCartUseCase,
            CompletePurchaseUseCase completePurchaseUseCase) {
        this.objectMapper = objectMapper;
        this.registerAccountUseCase = registerAccountUseCase;
        this.addProductToCartUseCase = addProductToCartUseCase;
        this.completePurchaseUseCase = completePurchaseUseCase;
    }
    
    @KafkaListener(topics = "customer.register", groupId = "richbank-customer-group")
    public void handleCustomerRegister(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        try {
            logger.info("Received customer registration event from topic: {}", topic);
            
            RegisterAccountCommand command = objectMapper.readValue(message, RegisterAccountCommand.class);
            Customer customer = registerAccountUseCase.execute(command);
            
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
            Cart cart = addProductToCartUseCase.execute(command);
            
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
            Order order = completePurchaseUseCase.execute(command);
            
            logger.info("Purchase completed successfully: orderId={}", order.getId().getValue());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing complete purchase event", e);
        }
    }
}

