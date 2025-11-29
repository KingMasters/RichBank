package com.hexagonal.framework.adapter.input.queue.kafka.admin;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.dto.SupportIssueCommand;
import com.hexagonal.application.port.in.admin.product.CreateProductUseCase;
import com.hexagonal.application.port.in.admin.user.HandleSupportIssueUseCase;
import com.hexagonal.domain.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.framework.common.QueueAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Kafka Event Listener for Admin Events
 * 
 * Listens to admin-related events from Kafka topics:
 * - admin.create-product
 * - admin.handle-support-issue
 */
@QueueAdapter
public class AdminKafkaListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminKafkaListener.class);
    
    private final ObjectMapper objectMapper;
    private final CreateProductUseCase createProductUseCase;
    private final HandleSupportIssueUseCase handleSupportIssueUseCase;
    
    public AdminKafkaListener(
            ObjectMapper objectMapper,
            CreateProductUseCase createProductUseCase,
            HandleSupportIssueUseCase handleSupportIssueUseCase) {
        this.objectMapper = objectMapper;
        this.createProductUseCase = createProductUseCase;
        this.handleSupportIssueUseCase = handleSupportIssueUseCase;
    }
    
    @KafkaListener(topics = "admin.create-product", groupId = "richbank-admin-group")
    public void handleCreateProduct(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        try {
            logger.info("Received create product event from topic: {}", topic);
            
            CreateProductCommand command = objectMapper.readValue(message, CreateProductCommand.class);
            Product product = createProductUseCase.execute(command);
            
            logger.info("Product created successfully: productId={}, sku={}", 
                product.getId().getValue(), product.getSku());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing create product event", e);
            // In production, you might want to send to a dead letter queue
        }
    }
    
    @KafkaListener(topics = "admin.handle-support-issue", groupId = "richbank-admin-group")
    public void handleSupportIssue(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        try {
            logger.info("Received support issue event from topic: {}", topic);
            
            SupportIssueCommand command = objectMapper.readValue(message, SupportIssueCommand.class);
            handleSupportIssueUseCase.execute(command);
            
            logger.info("Support issue handled successfully: customerId={}", command.getCustomerId().getValue());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing support issue event", e);
        }
    }
}

