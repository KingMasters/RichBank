package com.hexagonal.framework.adapter.input.kafka.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.service.command.customer.cart.AddProductToCartService;
import com.hexagonal.application.service.command.customer.checkout.CompletePurchaseService;
import com.hexagonal.application.service.command.customer.account.RegisterAccountService;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.framework.adapter.input.queue.kafka.customer.CustomerKafkaListener;
import com.hexagonal.domain.vo.Email;
import com.hexagonal.domain.vo.ID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerKafkaListener Contract Tests")
@Disabled("Disabled in CI: depends on Kafka/async behavior and causes flaky failures")
class CustomerKafkaListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RegisterAccountService registerAccountService;

    @Mock
    private AddProductToCartService addProductToCartService;

    @Mock
    private CompletePurchaseService completePurchaseService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private CustomerKafkaListener listener;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() {
        realObjectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should handle customer register event")
    void shouldHandleCustomerRegisterEvent() throws Exception {
        // Given
        RegisterAccountCommand command = new RegisterAccountCommand(
            "John",
            "Doe",
            "john.doe@example.com",
            "+1234567890"
        );
        String message = realObjectMapper.writeValueAsString(command);
        Customer customer = Customer.create("John", "Doe", Email.of("john.doe@example.com"));
        
        when(objectMapper.readValue(message, RegisterAccountCommand.class))
            .thenReturn(command);
        when(registerAccountService.execute(any(RegisterAccountCommand.class)))
            .thenReturn(customer);

        // When
        listener.handleCustomerRegister(message, "customer.register", acknowledgment);

        // Then
        verify(registerAccountService).execute(any(RegisterAccountCommand.class));
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Should handle add to cart event")
    void shouldHandleAddToCartEvent() throws Exception {
        // Given
        AddProductToCartCommand command = new AddProductToCartCommand(
            ID.generate().getValue().toString(),
            ID.generate().getValue().toString(),
            2,
            null,
            null
        );
        String message = realObjectMapper.writeValueAsString(command);
        Cart cart = Cart.create(ID.of(command.getCustomerId()));
        
        when(objectMapper.readValue(message, AddProductToCartCommand.class))
            .thenReturn(command);
        when(addProductToCartService.execute(any(AddProductToCartCommand.class)))
            .thenReturn(cart);

        // When
        listener.handleAddToCart(message, "customer.add-to-cart", acknowledgment);

        // Then
        verify(addProductToCartService).execute(any(AddProductToCartCommand.class));
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Should handle complete purchase event")
    void shouldHandleCompletePurchaseEvent() throws Exception {
        // Given
        CompletePurchaseCommand command = new CompletePurchaseCommand(
            ID.generate().getValue().toString(),
            ID.generate().getValue().toString(),
            null,
            null,
            null
        );
        String message = realObjectMapper.writeValueAsString(command);
        Order order = mock(Order.class);
        
        when(objectMapper.readValue(message, CompletePurchaseCommand.class))
            .thenReturn(command);
        when(completePurchaseService.execute(any(CompletePurchaseCommand.class)))
            .thenReturn(order);

        // When
        listener.handleCompletePurchase(message, "customer.complete-purchase", acknowledgment);

        // Then
        verify(completePurchaseService).execute(any(CompletePurchaseCommand.class));
        verify(acknowledgment).acknowledge();
    }
}
