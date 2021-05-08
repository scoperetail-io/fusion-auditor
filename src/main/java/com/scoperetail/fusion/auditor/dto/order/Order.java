package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
  private Node node;
  private FulfillmentOrder fulfillmentOrder;
}
