package com.scoperetail.fusion.auditor.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageExtensions {
  @JsonProperty("MessageExtension")
  private MessageExtension messageExtension;
}
