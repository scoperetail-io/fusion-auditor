package com.scoperetail.fusion.auditor.dto.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class Line {
  @NotNull
  private String lineNbr;
  @NotNull
  private String upc;
  @NotNull
  private String qty;
  @NotNull
  private String weight;

  @Min(0)
  private Integer upperThresholdLimit;

  @Min(0)
  private Integer lowerThresholdLimit;

  @NotNull
  private String pickByType;
  @NotNull
  private String substitutionAllowed;
  @NotNull
  private String itemNbr;
  @NotNull
  private String uom;

  private CrossRefs crossRefs;
}
