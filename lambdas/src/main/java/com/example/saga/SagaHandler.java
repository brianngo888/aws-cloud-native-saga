package com.example.saga;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
/** All operations are idempotent by orderId; payment is SIMULATED, never a real charge. */
public class SagaHandler implements RequestHandler<Map<String,Object>,Map<String,Object>> {
 private static final DynamoDbClient DB=DynamoDbClient.create();
 private static final String TABLE=System.getenv().getOrDefault("SAGA_TABLE","saga-operations");
 @Override public Map<String,Object> handleRequest(Map<String,Object> event,Context context) {
  String action=Objects.toString(event.get("action"),"");
  String orderId=Objects.toString(event.get("orderId"),"");
  if(orderId.isBlank()) throw new IllegalArgumentException("orderId required");
  if(Boolean.TRUE.equals(event.get("simulateFailure")) && action.equals("ship")) throw new IllegalStateException("Injected shipping failure");
  String key=orderId+"#"+action;
  try {
   DB.putItem(PutItemRequest.builder().tableName(TABLE)
    .item(Map.of("pk",AttributeValue.fromS(key),"state",AttributeValue.fromS("DONE")))
    .conditionExpression("attribute_not_exists(pk)").build());
  } catch(ConditionalCheckFailedException duplicate) { /* previously committed: safe retry */ }
  return new HashMap<>(event) {{ put("lastAction",action); put("lastActionStatus","DONE"); }};
 }
}
