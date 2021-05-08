package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SourceNode {
  @NotNull
  private Location location;

  @NotNull
  private String nodeID;
}
