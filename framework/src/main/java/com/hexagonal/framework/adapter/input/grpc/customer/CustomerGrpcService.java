package com.hexagonal.framework.adapter.input.grpc.customer;

import com.hexagonal.application.dto.*;
import com.hexagonal.application.port.in.customer.account.LoginInputPort;
import com.hexagonal.application.port.in.customer.account.RegisterAccountInputPort;
import com.hexagonal.application.port.in.customer.cart.AddProductToCartInputPort;
import com.hexagonal.application.port.in.customer.cart.ViewCartInputPort;
import com.hexagonal.application.port.in.customer.catalog.ListAllProductsInputPort;
import com.hexagonal.application.port.in.customer.catalog.SearchProductsInputPort;
import com.hexagonal.application.port.in.customer.checkout.CompletePurchaseInputPort;
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
    
    private final RegisterAccountInputPort registerAccountInputPort;
    private final LoginInputPort loginInputPort;
    private final ListAllProductsInputPort listAllProductsInputPort;
    private final SearchProductsInputPort searchProductsInputPort;
    private final ViewCartInputPort viewCartInputPort;
    private final AddProductToCartInputPort addProductToCartInputPort;
    private final CompletePurchaseInputPort completePurchaseInputPort;
    
    @Autowired
    public CustomerGrpcService(
            RegisterAccountInputPort registerAccountInputPort,
            LoginInputPort loginInputPort,
            ListAllProductsInputPort listAllProductsInputPort,
            SearchProductsInputPort searchProductsInputPort,
            ViewCartInputPort viewCartInputPort,
            AddProductToCartInputPort addProductToCartInputPort,
            CompletePurchaseInputPort completePurchaseInputPort) {
        this.registerAccountInputPort = registerAccountInputPort;
        this.loginInputPort = loginInputPort;
        this.listAllProductsInputPort = listAllProductsInputPort;
        this.searchProductsInputPort = searchProductsInputPort;
        this.viewCartInputPort = viewCartInputPort;
        this.addProductToCartInputPort = addProductToCartInputPort;
        this.completePurchaseInputPort = completePurchaseInputPort;
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
            Customer customer = registerAccountInputPort.execute(command);
            
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
            Customer customer = loginInputPort.execute(command);
            
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
            List<Product> products = listAllProductsInputPort.execute();
            
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
            List<Product> products = searchProductsInputPort.execute(command);
            
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
            Cart cart = viewCartInputPort.execute(command);
            
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
            Cart cart = addProductToCartInputPort.execute(command);
            
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
            Order order = completePurchaseInputPort.execute(command);
            
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
