package com.example.order;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class OrderEntityTest {
 @Test void recordHoldsOrderValues() {
  var order=new OrderEntity(UUID.randomUUID(),"laptop",1,new BigDecimal("100.00"),"PENDING");
  assertEquals("PENDING",order.status());
 }
}
