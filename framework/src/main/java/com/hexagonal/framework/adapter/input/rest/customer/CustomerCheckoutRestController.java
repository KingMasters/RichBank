package com.hexagonal.framework.adapter.input.rest.customer;

import com.hexagonal.application.dto.ApplyDiscountCodeCommand;
import com.hexagonal.application.dto.CompletePurchaseCommand;
import com.hexagonal.application.dto.EnterShippingInformationCommand;
import com.hexagonal.application.dto.SelectPaymentMethodCommand;
import com.hexagonal.application.ports.input.customer.checkout.ApplyDiscountCodeInputPort;
import com.hexagonal.application.ports.input.customer.checkout.CompletePurchaseInputPort;
import com.hexagonal.application.ports.input.customer.checkout.EnterShippingInformationInputPort;
import com.hexagonal.application.ports.input.customer.checkout.SelectPaymentMethodInputPort;
import com.hexagonal.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/checkout")
public class CustomerCheckoutRestController {
    
    private final EnterShippingInformationInputPort enterShippingInformationInputPort;
    private final SelectPaymentMethodInputPort selectPaymentMethodInputPort;
    private final ApplyDiscountCodeInputPort applyDiscountCodeInputPort;
    private final CompletePurchaseInputPort completePurchaseInputPort;
    
    public CustomerCheckoutRestController(
            EnterShippingInformationInputPort enterShippingInformationInputPort,
            SelectPaymentMethodInputPort selectPaymentMethodInputPort,
            ApplyDiscountCodeInputPort applyDiscountCodeInputPort,
            CompletePurchaseInputPort completePurchaseInputPort) {
        this.enterShippingInformationInputPort = enterShippingInformationInputPort;
        this.selectPaymentMethodInputPort = selectPaymentMethodInputPort;
        this.applyDiscountCodeInputPort = applyDiscountCodeInputPort;
        this.completePurchaseInputPort = completePurchaseInputPort;
    }
    
    @PostMapping("/shipping")
    public ResponseEntity<Void> enterShippingInfo(@RequestBody EnterShippingInformationCommand command) {
        enterShippingInformationInputPort.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/payment-method")
    public ResponseEntity<Void> selectPaymentMethod(@RequestBody SelectPaymentMethodCommand command) {
        selectPaymentMethodInputPort.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/discount")
    public ResponseEntity<Void> applyDiscountCode(@RequestBody ApplyDiscountCodeCommand command) {
        applyDiscountCodeInputPort.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Order> completePurchase(@RequestBody CompletePurchaseCommand command) {
        Order order = completePurchaseInputPort.execute(command);
        return ResponseEntity.ok(order);
    }
}

