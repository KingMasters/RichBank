package com.hexagonal.framework.adapter.input.web.grpc.customer;

import com.hexagonal.application.dto.*;
import com.hexagonal.application.port.in.customer.account.LoginUseCase;
import com.hexagonal.application.port.in.customer.account.RegisterAccountUseCase;
import com.hexagonal.application.port.in.customer.cart.AddProductToCartUseCase;
import com.hexagonal.application.port.in.customer.cart.ViewCartUseCase;
import com.hexagonal.application.port.in.customer.catalog.ListAllProductsUseCase;
import com.hexagonal.application.port.in.customer.catalog.SearchProductsUseCase;
import com.hexagonal.application.port.in.customer.checkout.CompletePurchaseUseCase;
import com.hexagonal.entity.Cart;
import com.hexagonal.entity.Customer;
import com.hexagonal.entity.Order;
import com.hexagonal.entity.Product;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Service Implementation for Customer
 * 
 * Note: This is a simplified implementation. In a real scenario, you would:
 * 1. Run 'mvn compile' to generate gRPC classes from .proto files
 * 2. Use the generated classes instead of manual mapping
 * 3. Handle errors properly with gRPC status codes
 */
@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {
    
    private final RegisterAccountUseCase registerAccountUseCase;
    private final LoginUseCase loginUseCase;
    private final ListAllProductsUseCase listAllProductsUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final ViewCartUseCase viewCartUseCase;
    private final AddProductToCartUseCase addProductToCartUseCase;
    private final CompletePurchaseUseCase completePurchaseUseCase;
    
    @Autowired
    public CustomerGrpcService(
            RegisterAccountUseCase registerAccountUseCase,
            LoginUseCase loginUseCase,
            ListAllProductsUseCase listAllProductsUseCase,
            SearchProductsUseCase searchProductsUseCase,
            ViewCartUseCase viewCartUseCase,
            AddProductToCartUseCase addProductToCartUseCase,
            CompletePurchaseUseCase completePurchaseUseCase) {
        this.registerAccountUseCase = registerAccountUseCase;
        this.loginUseCase = loginUseCase;
        this.listAllProductsUseCase = listAllProductsUseCase;
        this.searchProductsUseCase = searchProductsUseCase;
        this.viewCartUseCase = viewCartUseCase;
        this.addProductToCartUseCase = addProductToCartUseCase;
        this.completePurchaseUseCase = completePurchaseUseCase;
    }
    
    @Override
    public void registerAccount(RegisterAccountRequest request, StreamObserver<CustomerResponse> responseObserver) {
        try {
            RegisterAccountCommand command = new RegisterAccountCommand(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber()
            );
            Customer customer = registerAccountUseCase.execute(command);
            
            CustomerResponse response = CustomerResponse.newBuilder()
                .setId(customer.getId().getValue().toString())
                .setFirstName(customer.getFirstName())
                .setLastName(customer.getLastName())
                .setEmail(customer.getEmail().getValue())
                .setActive(customer.isActive())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void login(LoginRequest request, StreamObserver<CustomerResponse> responseObserver) {
        try {
            LoginCommand command = new LoginCommand(request.getEmail(), request.getPassword());
            Customer customer = loginUseCase.execute(command);
            
            CustomerResponse response = CustomerResponse.newBuilder()
                .setId(customer.getId().getValue().toString())
                .setFirstName(customer.getFirstName())
                .setLastName(customer.getLastName())
                .setEmail(customer.getEmail().getValue())
                .setActive(customer.isActive())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        try {
            List<Product> products = listAllProductsUseCase.execute();
            
            ListProductsResponse response = ListProductsResponse.newBuilder()
                .addAllProducts(products.stream()
                    .map(this::toProductResponse)
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void searchProducts(SearchProductsRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        try {
            SearchProductsCommand command = new SearchProductsCommand(request.getSearchTerm());
            List<Product> products = searchProductsUseCase.execute(command);
            
            ListProductsResponse response = ListProductsResponse.newBuilder()
                .addAllProducts(products.stream()
                    .map(this::toProductResponse)
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void viewCart(ViewCartRequest request, StreamObserver<CartResponse> responseObserver) {
        try {
            ViewCartCommand command = new ViewCartCommand(request.getCustomerId());
            Cart cart = viewCartUseCase.execute(command);
            
            CartResponse response = CartResponse.newBuilder()
                .setId(cart.getId().getValue().toString())
                .setCustomerId(cart.getCustomerId().getValue().toString())
                .addAllItems(cart.getItems().stream()
                    .map(item -> CartItemResponse.newBuilder()
                        .setProductId(item.getProductId().getValue().toString())
                        .setQuantity(item.getQuantity().getValue())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void addProductToCart(AddProductToCartRequest request, StreamObserver<CartResponse> responseObserver) {
        try {
            AddProductToCartCommand command = new AddProductToCartCommand(
                request.getCustomerId(),
                request.getProductId(),
                request.getQuantity(),
                /* unitPrice */ null,
                /* currency */ null
            );
            Cart cart = addProductToCartUseCase.execute(command);
            
            CartResponse response = CartResponse.newBuilder()
                .setId(cart.getId().getValue().toString())
                .setCustomerId(cart.getCustomerId().getValue().toString())
                .addAllItems(cart.getItems().stream()
                    .map(item -> CartItemResponse.newBuilder()
                        .setProductId(item.getProductId().getValue().toString())
                        .setQuantity(item.getQuantity().getValue())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void completePurchase(CompletePurchaseRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            CompletePurchaseCommand command = new CompletePurchaseCommand(
                request.getCustomerId(),
                request.getCartId(),
                /* billingAddressId */ null,
                /* paymentMethod */ null,
                /* discountCode */ null
            );
            Order order = completePurchaseUseCase.execute(command);
            
            OrderResponse response = OrderResponse.newBuilder()
                .setId(order.getId().getValue().toString())
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setStatus(order.getStatus().name())
                .setTotalAmount(order.getTotalAmount().getAmount().toString())
                .setCurrency(order.getTotalAmount().getCurrency().getCurrencyCode())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.newBuilder()
            .setId(product.getId().getValue().toString())
            .setName(product.getName())
            .setDescription(product.getDescription() != null ? product.getDescription() : "")
            .setPriceAmount(product.getPrice().getAmount().toString())
            .setPriceCurrency(product.getPrice().getCurrency().getCurrencyCode())
            .setSku(product.getSku())
            .setStockQuantity(product.getStockQuantity().getValue())
            .setStatus(product.getStatus().name())
            .build();
    }
}
