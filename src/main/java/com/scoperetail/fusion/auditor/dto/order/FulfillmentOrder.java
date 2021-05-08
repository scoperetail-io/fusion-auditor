package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FulfillmentOrder {
  private String orderNbr;
  private OrderPriority orderPriority;
  private Type type;
  private DestinationBusinessUnit destinationBusinessUnit;
  private String pickDueTime;
  private String expectedOrderPickupTime;
  private String earliestPickTime;
  private String orderSequenceNumber;
  private String loadGroupNumber;
  private String carrierBagAllowed;
  private String recordCarrierBagCount;
  private Lines lines;
}
