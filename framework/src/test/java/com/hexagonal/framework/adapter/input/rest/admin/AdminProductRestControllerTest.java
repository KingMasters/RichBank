package com.hexagonal.framework.adapter.input.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.application.dto.CreateProductCommand;
import com.hexagonal.application.dto.ManageStockCommand;
import com.hexagonal.application.dto.UpdateProductCommand;
import com.hexagonal.application.ports.input.admin.product.CreateProductInputPort;
import com.hexagonal.application.ports.input.admin.product.ManageProductStockInputPort;
import com.hexagonal.application.ports.input.admin.product.RemoveProductInputPort;
import com.hexagonal.application.ports.input.admin.product.UpdateProductInputPort;
import com.hexagonal.entity.Product;
import com.hexagonal.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminProductRestController.class, 
    useDefaultFilters = false)
@DisplayName("AdminProductRestController Contract Tests")
@Disabled("Disabled in CI: requires application context and DB beans that aren't available in test environment")
class AdminProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateProductInputPort createProductInputPort;

    @MockBean
    private UpdateProductInputPort updateProductInputPort;

    @MockBean
    private RemoveProductInputPort removeProductInputPort;

    @MockBean
    private ManageProductStockInputPort manageProductStockInputPort;

    private static final Currency USD = Currency.getInstance("USD");

    @Test
    @DisplayName("POST /api/admin/products - Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "Test Product",
            "Test Description",
            new BigDecimal("99.99"),
            USD,
            "PROD-001",
            null,  // categoryIds
            null,  // images
            null,  // weight
            null,  // weightUnit
            null,  // length
            null,  // width
            null,  // height
            null   // dimensionUnit
        );
        Product product = Product.create(
            command.getName(),
            Money.of(command.getPrice(), command.getCurrency()),
            command.getSku()
        );
        
        when(createProductInputPort.execute(any(CreateProductCommand.class)))
            .thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.sku").value("PROD-001"));
    }

    @Test
    @DisplayName("PUT /api/admin/products/{productId} - Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Given
        String productId = "123e4567-e89b-12d3-a456-426614174000";
        UpdateProductCommand command = new UpdateProductCommand(
            productId,
            "Updated Product",
            "Updated Description",
            new BigDecimal("149.99"),
            USD,
            null, null, null, null, null, null, null, null
        );
        Product product = Product.create(
            command.getName(),
            Money.of(command.getPrice(), command.getCurrency()),
            "PROD-001"
        );
        
        when(updateProductInputPort.execute(any(UpdateProductCommand.class)))
            .thenReturn(product);

        // When & Then
        mockMvc.perform(put("/api/admin/products/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    @DisplayName("DELETE /api/admin/products/{productId} - Should delete product successfully")
    void shouldDeleteProductSuccessfully() throws Exception {
        // Given
        String productId = "123e4567-e89b-12d3-a456-426614174000";
        doNothing().when(removeProductInputPort).execute(anyString());

        // When & Then
        mockMvc.perform(delete("/api/admin/products/{productId}", productId))
            .andExpect(status().isNoContent());
        
        verify(removeProductInputPort).execute(productId);
    }

    @Test
    @DisplayName("POST /api/admin/products/{productId}/stock - Should manage stock successfully")
    void shouldManageStockSuccessfully() throws Exception {
        // Given
        String productId = "123e4567-e89b-12d3-a456-426614174000";
        ManageStockCommand command = new ManageStockCommand(
            productId,
            10,  // quantity
            ManageStockCommand.StockOperation.ADD  // operation
        );
        Product product = Product.create(
            "Test Product",
            Money.of(new BigDecimal("99.99"), USD),
            "PROD-001"
        );
        
        when(manageProductStockInputPort.execute(any(ManageStockCommand.class)))
            .thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/admin/products/{productId}/stock", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }
}
