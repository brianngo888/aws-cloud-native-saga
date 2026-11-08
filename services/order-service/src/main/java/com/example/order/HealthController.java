package com.example.order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
public class HealthController {
    @GetMapping("/api/hello")
    public Mono<String> hello() { return Mono.just("AWS Saga lab: order-service online"); }
}
