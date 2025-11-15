package com.hexagonal.framework.adapter.input.grpc.admin;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.ports.input.admin.order.UpdateOrderStatusInputPort;
import com.hexagonal.application.ports.input.admin.order.ViewAllOrdersInputPort;
import com.hexagonal.application.ports.input.admin.product.CreateProductInputPort;
import com.hexagonal.application.ports.input.admin.product.UpdateProductInputPort;
import com.hexagonal.application.ports.input.admin.user.ToggleCustomerActiveInputPort;
import com.hexagonal.application.ports.input.admin.user.ViewCustomersInputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.entity.Order;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.ID;
import com.hexagonal.vo.OrderStatus;
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
    
    private final CreateProductInputPort createProductInputPort;
    private final UpdateProductInputPort updateProductInputPort;
    private final ViewAllOrdersInputPort viewAllOrdersInputPort;
    private final UpdateOrderStatusInputPort updateOrderStatusInputPort;
    private final ViewCustomersInputPort viewCustomersInputPort;
    private final ToggleCustomerActiveInputPort toggleCustomerActiveInputPort;
    
    @Autowired
    public AdminGrpcService(
            CreateProductInputPort createProductInputPort,
            UpdateProductInputPort updateProductInputPort,
            ViewAllOrdersInputPort viewAllOrdersInputPort,
            UpdateOrderStatusInputPort updateOrderStatusInputPort,
            ViewCustomersInputPort viewCustomersInputPort,
            ToggleCustomerActiveInputPort toggleCustomerActiveInputPort) {
        this.createProductInputPort = createProductInputPort;
        this.updateProductInputPort = updateProductInputPort;
        this.viewAllOrdersInputPort = viewAllOrdersInputPort;
        this.updateOrderStatusInputPort = updateOrderStatusInputPort;
        this.viewCustomersInputPort = viewCustomersInputPort;
        this.toggleCustomerActiveInputPort = toggleCustomerActiveInputPort;
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
            Product product = createProductInputPort.execute(command);
            
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
            Product product = updateProductInputPort.execute(command);
            
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
            List<Order> orders = viewAllOrdersInputPort.execute();
            
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
            Order order = updateOrderStatusInputPort.execute(
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
            List<Customer> customers = viewCustomersInputPort.execute();
            
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
            Customer customer = toggleCustomerActiveInputPort.execute(
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

