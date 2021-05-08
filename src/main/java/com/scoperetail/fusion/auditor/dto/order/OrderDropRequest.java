package com.scoperetail.fusion.auditor.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDropRequest {
  @NotNull
  @JsonProperty("RoutingInfo")
  private RoutingInfo routingInfo;

  @NotNull
  @JsonProperty("Order")
  private Order order;

  @NotNull
  @JsonProperty("MessageExtensions")
  private MessageExtensions messageExtensions;
}
