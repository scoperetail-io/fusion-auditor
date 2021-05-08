package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DestinationBusinessUnit {
  @NotNull
  private Integer destDivisonNumber;

  @NotNull
  private String destBannerName;
}
