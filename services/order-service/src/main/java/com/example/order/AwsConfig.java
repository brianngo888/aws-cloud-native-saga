package com.example.order;
import org.springframework.context.annotation.*;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import io.r2dbc.spi.ConnectionFactory;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
@Configuration
public class AwsConfig {
 @Bean @Lazy SfnAsyncClient sfn() {return SfnAsyncClient.create();}
 @Bean ReactiveTransactionManager transactionManager(ConnectionFactory factory) {return new R2dbcTransactionManager(factory);}
 @Bean TransactionalOperator tx(ReactiveTransactionManager manager) {return TransactionalOperator.create(manager);}
}
