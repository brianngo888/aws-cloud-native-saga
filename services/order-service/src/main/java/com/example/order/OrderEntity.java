package com.example.order;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("orders")
public record OrderEntity(@Id UUID id, String productId, int quantity, BigDecimal amount, String status) {}
