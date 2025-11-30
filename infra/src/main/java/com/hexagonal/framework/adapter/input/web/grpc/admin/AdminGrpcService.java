package com.hexagonal.framework.adapter.input.web.grpc.admin;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.port.in.admin.order.UpdateOrderStatusUseCase;
import com.hexagonal.application.port.in.admin.order.ViewAllOrdersUseCase;
import com.hexagonal.application.port.in.admin.product.CreateProductUseCase;
import com.hexagonal.application.port.in.admin.product.UpdateProductUseCase;
import com.hexagonal.application.port.in.admin.user.ToggleCustomerActiveUseCase;
import com.hexagonal.application.port.in.admin.user.ViewCustomersUseCase;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.OrderStatus;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Service Implementation for Admin
 * 
 * Note: This is a simplified implementation. In a real scenario, you would:
 * 1. Run 'mvn compile' to generate gRPC classes from .proto files
 * 2. Use the generated classes instead of manual mapping
 * 3. Handle errors properly with gRPC status codes
 */
@GrpcService
public class AdminGrpcService extends AdminServiceGrpc.AdminServiceImplBase {
    
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ViewAllOrdersUseCase viewAllOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final ViewCustomersUseCase viewCustomersUseCase;
    private final ToggleCustomerActiveUseCase toggleCustomerActiveUseCase;
    
    @Autowired
    public AdminGrpcService(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            ViewAllOrdersUseCase viewAllOrdersUseCase,
            UpdateOrderStatusUseCase updateOrderStatusUseCase,
            ViewCustomersUseCase viewCustomersUseCase,
            ToggleCustomerActiveUseCase toggleCustomerActiveUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.viewAllOrdersUseCase = viewAllOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.viewCustomersUseCase = viewCustomersUseCase;
        this.toggleCustomerActiveUseCase = toggleCustomerActiveUseCase;
    }
    
    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            CreateProductCommand command = new CreateProductCommand(
                request.getName(),
                request.getDescription(),
                new BigDecimal(request.getPriceAmount()),
                Currency.getInstance(request.getPriceCurrency()),
                request.getSku(),
                /* categoryIds */ null,
                /* images */ null,
                /* weight */ null,
                /* weightUnit */ null,
                /* length */ null,
                /* width */ null,
                /* height */ null,
                /* dimensionUnit */ null
            );
            Product product = createProductUseCase.execute(command);
            
            ProductResponse response = toProductResponse(product);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            UpdateProductCommand command = new UpdateProductCommand(
                request.getProductId(),
                request.getName(),
                request.getDescription(),
                new BigDecimal(request.getPriceAmount()),
                Currency.getInstance(request.getPriceCurrency()),
                /* categoryIds */ null,
                /* images */ null,
                /* weight */ null,
                /* weightUnit */ null,
                /* length */ null,
                /* width */ null,
                /* height */ null,
                /* dimensionUnit */ null
            );
            Product product = updateProductUseCase.execute(command);
            
            ProductResponse response = toProductResponse(product);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void viewAllOrders(ViewAllOrdersRequest request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {
            List<Order> orders = viewAllOrdersUseCase.execute();
            
            ListOrdersResponse response = ListOrdersResponse.newBuilder()
                .addAllOrders(orders.stream()
                    .map(this::toOrderResponse)
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void updateOrderStatus(UpdateOrderStatusRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            Order order = updateOrderStatusUseCase.execute(
                ID.of(request.getOrderId()),
                OrderStatus.valueOf(request.getStatus())
            );
            
            OrderResponse response = toOrderResponse(order);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void viewCustomers(ViewCustomersRequest request, StreamObserver<ListCustomersResponse> responseObserver) {
        try {
            List<Customer> customers = viewCustomersUseCase.execute();
            
            ListCustomersResponse response = ListCustomersResponse.newBuilder()
                .addAllCustomers(customers.stream()
                    .map(this::toCustomerResponse)
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void toggleCustomerActive(ToggleCustomerActiveRequest request, StreamObserver<CustomerResponse> responseObserver) {
        try {
            Customer customer = toggleCustomerActiveUseCase.execute(
                ID.of(request.getCustomerId()),
                request.getEnable()
            );
            
            CustomerResponse response = toCustomerResponse(customer);
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
    
    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.newBuilder()
            .setId(order.getId().getValue().toString())
            .setCustomerId(order.getCustomerId().getValue().toString())
            .setStatus(order.getStatus().name())
            .setTotalAmount(order.getTotalAmount().getAmount().toString())
            .setCurrency(order.getTotalAmount().getCurrency().getCurrencyCode())
            .build();
    }
    
    private CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.newBuilder()
            .setId(customer.getId().getValue().toString())
            .setFirstName(customer.getFirstName())
            .setLastName(customer.getLastName())
            .setEmail(customer.getEmail().getValue())
            .setActive(customer.isActive())
            .build();
    }
}

