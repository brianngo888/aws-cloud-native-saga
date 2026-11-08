package com.example.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;

class OrderServiceTest {
    private final OrderRepository repository = mock(OrderRepository.class);
    private final R2dbcEntityTemplate orders = mock(R2dbcEntityTemplate.class);
    private final TransactionalOperator transaction = mock(TransactionalOperator.class);
    private final SfnAsyncClient sfn = mock(SfnAsyncClient.class);
    private final ObjectMapper json = new ObjectMapper();
    private final OrderController.CreateOrder request =
            new OrderController.CreateOrder("product-\"A\"\\B", 2, new BigDecimal("12.50"));

    @BeforeEach
    void setUp() {
        when(transaction.transactional(ArgumentMatchers.<Mono<OrderEntity>>any()))
                .thenAnswer(call -> call.getArgument(0));
        when(orders.insert(any(OrderEntity.class)))
                .thenAnswer(call -> Mono.just(call.getArgument(0, OrderEntity.class)));
    }

    @Test
    void insertsOrderAndStartsExecutionWithEscapedInput() throws Exception {
        String sagaArn = "arn:aws:states:us-east-2:123456789012:stateMachine:order";
        when(sfn.startExecution(any(StartExecutionRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(StartExecutionResponse.builder().build()));

        OrderEntity order = service(sagaArn).create(request).block();

        verify(orders).insert(order);
        verifyNoInteractions(repository);
        var execution = ArgumentCaptor.forClass(StartExecutionRequest.class);
        verify(sfn).startExecution(execution.capture());
        var payload = json.readTree(execution.getValue().input());
        assertEquals(sagaArn, execution.getValue().stateMachineArn());
        assertEquals(order.id().toString(), execution.getValue().name());
        assertEquals(order.id().toString(), payload.get("orderId").asText());
        assertEquals(request.productId(), payload.get("productId").asText());
        assertEquals(request.quantity(), payload.get("quantity").asInt());
        assertEquals(0, request.amount().compareTo(payload.get("amount").decimalValue()));
    }

    @Test
    void doesNotStartExecutionWhenInsertFails() {
        when(orders.insert(any(OrderEntity.class))).thenReturn(Mono.error(new IllegalStateException("insert failed")));

        StepVerifier.create(service("saga").create(request))
                .expectErrorMessage("insert failed")
                .verify();
        verifyNoInteractions(sfn);
    }

    @Test
    void localModeInsertsWithoutCallingAws() {
        StepVerifier.create(service("").create(request))
                .expectNextMatches(order -> "PENDING".equals(order.status()))
                .verifyComplete();
        verifyNoInteractions(sfn);
    }

    private OrderService service(String sagaArn) {
        return new OrderService(repository, orders, transaction, sfn, json, sagaArn);
    }
}
