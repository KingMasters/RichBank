package com.hexagonal.framework.adapter.input.web.rest.customer;

import com.hexagonal.application.dto.ApplyDiscountCodeCommand;
import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.application.dto.EnterShippingInformationCommand;
import com.hexagonal.application.dto.SelectPaymentMethodCommand;
import com.hexagonal.application.port.in.customer.checkout.ApplyDiscountCodeUseCase;
import com.hexagonal.application.port.in.customer.checkout.CompletePurchaseUseCase;
import com.hexagonal.application.port.in.customer.checkout.EnterShippingInformationUseCase;
import com.hexagonal.application.port.in.customer.checkout.SelectPaymentMethodUseCase;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.framework.common.WebAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@WebAdapter
@RestController
@RequestMapping("/api/customer/checkout")
public class CustomerCheckoutController {
    
    private final EnterShippingInformationUseCase enterShippingInformationUseCase;
    private final SelectPaymentMethodUseCase selectPaymentMethodUseCase;
    private final ApplyDiscountCodeUseCase applyDiscountCodeUseCase;
    private final CompletePurchaseUseCase completePurchaseUseCase;
    
    public CustomerCheckoutController(
            EnterShippingInformationUseCase enterShippingInformationUseCase,
            SelectPaymentMethodUseCase selectPaymentMethodUseCase,
            ApplyDiscountCodeUseCase applyDiscountCodeUseCase,
            CompletePurchaseUseCase completePurchaseUseCase) {
        this.enterShippingInformationUseCase = enterShippingInformationUseCase;
        this.selectPaymentMethodUseCase = selectPaymentMethodUseCase;
        this.applyDiscountCodeUseCase = applyDiscountCodeUseCase;
        this.completePurchaseUseCase = completePurchaseUseCase;
    }
    
    @PostMapping("/shipping")
    public ResponseEntity<Void> enterShippingInfo(@RequestBody EnterShippingInformationCommand command) {
        enterShippingInformationUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/payment-method")
    public ResponseEntity<Void> selectPaymentMethod(@RequestBody SelectPaymentMethodCommand command) {
        selectPaymentMethodUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/discount")
    public ResponseEntity<Void> applyDiscountCode(@RequestBody ApplyDiscountCodeCommand command) {
        applyDiscountCodeUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Order> completePurchase(@RequestBody CompletePurchaseCommand command) {
        Order order = completePurchaseUseCase.execute(command);
        return ResponseEntity.ok(order);
    }
}

