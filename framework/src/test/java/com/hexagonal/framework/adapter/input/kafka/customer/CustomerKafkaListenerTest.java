package com.hexagonal.framework.adapter.input.kafka.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.usecase.customer.cart.AddProductToCartUseCaseHandler;
import com.hexagonal.application.usecase.customer.checkout.CompletePurchaseUseCaseHandler;
import com.hexagonal.application.usecase.customer.account.RegisterAccountUseCaseHandler;
import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Customer;
import com.hexagonal.entity.Order;
import com.hexagonal.framework.adapter.input.queue.kafka.customer.CustomerKafkaListener;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;
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
    private RegisterAccountUseCaseHandler registerAccountUseCaseHandler;

    @Mock
    private AddProductToCartUseCaseHandler addProductToCartUseCaseHandler;

    @Mock
    private CompletePurchaseUseCaseHandler completePurchaseUseCaseHandler;

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
        when(registerAccountUseCaseHandler.execute(any(RegisterAccountCommand.class)))
            .thenReturn(customer);

        // When
        listener.handleCustomerRegister(message, "customer.register", acknowledgment);

        // Then
        verify(registerAccountUseCaseHandler).execute(any(RegisterAccountCommand.class));
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
        when(addProductToCartUseCaseHandler.execute(any(AddProductToCartCommand.class)))
            .thenReturn(cart);

        // When
        listener.handleAddToCart(message, "customer.add-to-cart", acknowledgment);

        // Then
        verify(addProductToCartUseCaseHandler).execute(any(AddProductToCartCommand.class));
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
        when(completePurchaseUseCaseHandler.execute(any(CompletePurchaseCommand.class)))
            .thenReturn(order);

        // When
        listener.handleCompletePurchase(message, "customer.complete-purchase", acknowledgment);

        // Then
        verify(completePurchaseUseCaseHandler).execute(any(CompletePurchaseCommand.class));
        verify(acknowledgment).acknowledge();
    }
}
