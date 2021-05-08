package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderPriority {
  @NotNull
  private String code;
  @NotNull
  private String description;
}
