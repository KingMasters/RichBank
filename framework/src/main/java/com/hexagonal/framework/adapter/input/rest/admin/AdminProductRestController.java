package com.hexagonal.framework.adapter.input.rest.admin;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.port.in.admin.product.CreateProductInputPort;
import com.hexagonal.application.port.in.admin.product.ManageProductStockInputPort;
import com.hexagonal.application.port.in.admin.product.RemoveProductInputPort;
import com.hexagonal.application.port.in.admin.product.UpdateProductInputPort;
import com.hexagonal.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductRestController {
    
    private final CreateProductInputPort createProductInputPort;
    private final UpdateProductInputPort updateProductInputPort;
    private final RemoveProductInputPort removeProductInputPort;
    private final ManageProductStockInputPort manageProductStockInputPort;
    
    public AdminProductRestController(
            CreateProductInputPort createProductInputPort,
            UpdateProductInputPort updateProductInputPort,
            RemoveProductInputPort removeProductInputPort,
            ManageProductStockInputPort manageProductStockInputPort) {
        this.createProductInputPort = createProductInputPort;
        this.updateProductInputPort = updateProductInputPort;
        this.removeProductInputPort = removeProductInputPort;
        this.manageProductStockInputPort = manageProductStockInputPort;
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductCommand command) {
        Product product = createProductInputPort.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String productId,
            @RequestBody UpdateProductCommand command) {
        Product product = updateProductInputPort.execute(command);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProduct(@PathVariable String productId) {
        removeProductInputPort.execute(productId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{productId}/stock")
    public ResponseEntity<Product> manageStock(
            @PathVariable String productId,
            @RequestBody ManageStockCommand command) {
        Product product = manageProductStockInputPort.execute(command);
        return ResponseEntity.ok(product);
    }
}

