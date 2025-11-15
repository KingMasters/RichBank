package com.hexagonal.application.dto;

import lombok.Value;

@Value
public class ManageStockCommand {
    String productId;
    int quantity;
    StockOperation operation;
    
    public enum StockOperation {
        ADD,
        REMOVE,
        SET
    }
}
