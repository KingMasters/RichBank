package com.hexagonal.framework.adapter.input.web.rest.admin;

import com.hexagonal.application.dto.RefundCommand;
import com.hexagonal.application.port.in.admin.order.HandleReturnOrRefundUseCase;
import com.hexagonal.application.port.in.admin.order.UpdateOrderStatusUseCase;
import com.hexagonal.application.port.in.admin.order.ViewAllOrdersUseCase;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.domain.vo.ID;
import com.hexagonal.domain.vo.OrderStatus;
import com.hexagonal.framework.common.WebAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    
    private final ViewAllOrdersUseCase viewAllOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final HandleReturnOrRefundUseCase handleReturnOrRefundUseCase;
    
    public AdminOrderController(
            ViewAllOrdersUseCase viewAllOrdersUseCase,
            UpdateOrderStatusUseCase updateOrderStatusUseCase,
            HandleReturnOrRefundUseCase handleReturnOrRefundUseCase) {
        this.viewAllOrdersUseCase = viewAllOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.handleReturnOrRefundUseCase = handleReturnOrRefundUseCase;
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> viewAllOrders() {
        List<Order> orders = viewAllOrdersUseCase.execute();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        Order order = updateOrderStatusUseCase.execute(
            ID.of(orderId),
            OrderStatus.valueOf(status)
        );
        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/refund")
    public ResponseEntity<Order> handleRefund(
            @RequestParam String orderId,
            @RequestBody RefundCommand command) {
        Order order = handleReturnOrRefundUseCase.execute(
            ID.of(orderId),
            command
        );
        return ResponseEntity.ok(order);
    }
}

