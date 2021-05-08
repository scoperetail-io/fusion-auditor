package com.scoperetail.fusion.auditor.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DestinationNode {
  @NotNull
  private Location location;

  @NotNull
  private String nodeID;

  @NotNull
  @JsonProperty("cNodeID")
  private String cNodeID;
}
