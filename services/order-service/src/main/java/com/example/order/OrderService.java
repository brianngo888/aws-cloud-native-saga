package com.example.order;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
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
 private final TransactionalOperator tx;
 private final SfnAsyncClient sfn;
 private final String sagaArn;
 public OrderService(OrderRepository repository,TransactionalOperator tx,SfnAsyncClient sfn,@Value("${app.saga-arn:}") String sagaArn) {
  this.repository=repository;this.tx=tx;this.sfn=sfn;this.sagaArn=sagaArn;
 }
 public Mono<OrderEntity> create(OrderController.CreateOrder request) {
  UUID id=UUID.randomUUID();
  OrderEntity order=new OrderEntity(id,request.productId(),request.quantity(),request.amount(),"PENDING");
  return tx.transactional(repository.save(order)).flatMap(saved -> {
   if (sagaArn.isBlank()) return Mono.just(saved); // local ACID exercises only
   String payload="{\"orderId\":\""+id+"\",\"productId\":\""+request.productId()+"\",\"quantity\":"+request.quantity()+",\"amount\":"+request.amount()+"}";
   return Mono.fromFuture(sfn.startExecution(StartExecutionRequest.builder().stateMachineArn(sagaArn).name(id.toString()).input(payload).build()))
    .thenReturn(saved);
  });
 }
 public Mono<OrderEntity> get(UUID id) {return repository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));}
}
