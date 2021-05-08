package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Node {
  @NotNull
  private String nodeId;

  @NotNull
  @Size(min = 2, max = 2)
  private String countryCode;
}