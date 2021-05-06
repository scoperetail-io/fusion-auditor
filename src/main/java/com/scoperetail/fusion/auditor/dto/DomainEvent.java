/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scoperetail.fusion.auditor.enums.Event;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DomainEvent {
  public String eventId;
  public Event eventName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  public LocalDateTime sourceTs;

  private Map<String, String> eventIdKeys;
  public String payload;
  @Builder.Default public LocalDateTime timestamp = LocalDateTime.now();
}
