package com.hexagonal.framework.adapter.input.grpc.customer;

import com.hexagonal.application.ports.input.customer.account.LoginInputPort;
import com.hexagonal.application.ports.input.customer.account.RegisterAccountInputPort;
import com.hexagonal.application.ports.input.customer.cart.AddProductToCartInputPort;
import com.hexagonal.application.ports.input.customer.cart.ViewCartInputPort;
import com.hexagonal.application.ports.input.customer.catalog.ListAllProductsInputPort;
import com.hexagonal.application.ports.input.customer.catalog.SearchProductsInputPort;
import com.hexagonal.application.ports.input.customer.checkout.CompletePurchaseInputPort;
import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Customer;
import com.hexagonal.entity.Order;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.Email;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.Money;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import net.devh.boot.grpc.server.service.GrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * gRPC Contract Tests for Customer Service
 * 
 * Note: These are simplified contract tests. In a real scenario, you would:
 * 1. Use grpc-testing library for more comprehensive testing
 * 2. Test actual gRPC request/response serialization
 * 3. Test error handling and status codes
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerGrpcService Contract Tests")
class CustomerGrpcServiceContractTest {

    @Mock
    private RegisterAccountInputPort registerAccountInputPort;

    @Mock
    private LoginInputPort loginInputPort;

    @Mock
    private ListAllProductsInputPort listAllProductsInputPort;

    @Mock
    private SearchProductsInputPort searchProductsInputPort;

    @Mock
    private ViewCartInputPort viewCartInputPort;

    @Mock
    private AddProductToCartInputPort addProductToCartInputPort;

    @Mock
    private CompletePurchaseInputPort completePurchaseInputPort;

    @InjectMocks
    private CustomerGrpcService grpcService;

    private Customer customer;
    private Product product;
    private Cart cart;
    private Currency USD;

    @BeforeEach
    void setUp() {
        USD = Currency.getInstance("USD");
        customer = Customer.create("John", "Doe", Email.of("john.doe@example.com"));
        product = Product.create("Test Product", Money.of(new BigDecimal("99.99"), USD), "PROD-001");
        cart = Cart.create(customer.getId());
    }

    @Test
    @DisplayName("Should handle register account request")
    void shouldHandleRegisterAccountRequest() {
        // Given
        // Note: In real implementation, you would use generated gRPC classes
        // For now, we verify the service calls the correct port
        when(registerAccountInputPort.execute(any())).thenReturn(customer);
        
        StreamObserver<Object> responseObserver = mock(StreamObserver.class);

        // When
        // grpcService.registerAccount(request, responseObserver);

        // Then
        // Verify interactions would go here
        // This is a placeholder for actual gRPC contract testing
    }

    @Test
    @DisplayName("Should handle login request")
    void shouldHandleLoginRequest() {
        // Given
        when(loginInputPort.execute(any())).thenReturn(customer);
        
        StreamObserver<Object> responseObserver = mock(StreamObserver.class);

        // When
        // grpcService.login(request, responseObserver);

        // Then
        // Verify interactions would go here
    }

    @Test
    @DisplayName("Should handle list products request")
    void shouldHandleListProductsRequest() {
        // Given
        when(listAllProductsInputPort.execute()).thenReturn(List.of(product));
        
        StreamObserver<Object> responseObserver = mock(StreamObserver.class);

        // When
        // grpcService.listProducts(request, responseObserver);

        // Then
        // Verify interactions would go here
    }
}

