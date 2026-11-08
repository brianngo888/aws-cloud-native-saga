package com.example.order;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
@RestController @RequestMapping("/orders")
public class OrderController {
 private final OrderService service;
 public OrderController(OrderService service) { this.service=service; }
 public record CreateOrder(@NotBlank String productId,@Min(1) int quantity,@NotNull @DecimalMin("0.01") BigDecimal amount) {}
 @PostMapping public Mono<ResponseEntity<OrderEntity>> create(@Valid @RequestBody CreateOrder req) {
  return service.create(req).map(o->ResponseEntity.status(HttpStatus.ACCEPTED).body(o));
 }
 @GetMapping("/{id}") public Mono<OrderEntity> get(@PathVariable UUID id) { return service.get(id); }
}
