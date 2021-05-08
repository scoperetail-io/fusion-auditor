package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CrossRef {
  @NotNull
  private String upc;
  @NotNull
  private String itemNbr;
}
