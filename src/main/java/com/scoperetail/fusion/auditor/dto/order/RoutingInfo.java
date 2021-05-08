package com.scoperetail.fusion.auditor.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RoutingInfo {
  @NotNull
  @JsonProperty("SourceNode")
  private SourceNode sourceNode;

  @NotNull
  @JsonProperty("DestinationNode")
  private DestinationNode destinationNode;
}
