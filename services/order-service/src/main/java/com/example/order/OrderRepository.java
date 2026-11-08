package com.example.order;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
public interface OrderRepository extends ReactiveCrudRepository<OrderEntity,UUID> {}
