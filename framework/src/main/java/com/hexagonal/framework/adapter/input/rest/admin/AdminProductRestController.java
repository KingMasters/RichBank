package com.hexagonal.framework.adapter.input.rest.admin;

import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.usecase.admin.product.CreateProductUseCase;
import com.hexagonal.application.usecase.admin.product.ManageProductStockUseCase;
import com.hexagonal.application.usecase.admin.product.RemoveProductUseCase;
import com.hexagonal.application.usecase.admin.product.UpdateProductUseCase;
import com.hexagonal.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductRestController {
    
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final ManageProductStockUseCase manageProductStockUseCase;
    
    public AdminProductRestController(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            RemoveProductUseCase removeProductUseCase,
            ManageProductStockUseCase manageProductStockUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.removeProductUseCase = removeProductUseCase;
        this.manageProductStockUseCase = manageProductStockUseCase;
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductCommand command) {
        Product product = createProductUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String productId,
            @RequestBody UpdateProductCommand command) {
        Product product = updateProductUseCase.execute(command);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProduct(@PathVariable String productId) {
        removeProductUseCase.execute(productId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{productId}/stock")
    public ResponseEntity<Product> manageStock(
            @PathVariable String productId,
            @RequestBody ManageStockCommand command) {
        Product product = manageProductStockUseCase.execute(command);
        return ResponseEntity.ok(product);
    }
}

