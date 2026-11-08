package com.example.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
@Service
public class OrderService {
    private final OrderRepository repository;
    private final R2dbcEntityTemplate orders;
    private final TransactionalOperator tx;
    private final SfnAsyncClient sfn;
    private final ObjectMapper json;
    private final String sagaArn;

    public OrderService(OrderRepository repository, R2dbcEntityTemplate orders,
                        TransactionalOperator tx, @Lazy SfnAsyncClient sfn, ObjectMapper json,
                        @Value("${app.saga-arn:}") String sagaArn) {
        this.repository = repository;
        this.orders = orders;
        this.tx = tx;
        this.sfn = sfn;
        this.json = json;
        this.sagaArn = sagaArn;
    }

    public Mono<OrderEntity> create(OrderController.CreateOrder request) {
        UUID id = UUID.randomUUID();
        OrderEntity order = new OrderEntity(id, request.productId(), request.quantity(), request.amount(), "PENDING");
        Mono<OrderEntity> insert = tx.transactional(orders.insert(order));
        if (sagaArn.isBlank()) {
            return insert; // Local ACID exercises.
        }

        SagaInput input = new SagaInput(id, request.productId(), request.quantity(), request.amount());
        return Mono.fromCallable(() -> json.writeValueAsString(input))
                .flatMap(payload -> insert.flatMap(saved -> {
                    StartExecutionRequest execution = StartExecutionRequest.builder()
                            .stateMachineArn(sagaArn)
                            .name(id.toString())
                            .input(payload)
                            .build();
                    return Mono.fromFuture(() -> sfn.startExecution(execution)).thenReturn(saved);
                }));
    }

    public Mono<OrderEntity> get(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    private record SagaInput(UUID orderId, String productId, int quantity, BigDecimal amount) {}
}
