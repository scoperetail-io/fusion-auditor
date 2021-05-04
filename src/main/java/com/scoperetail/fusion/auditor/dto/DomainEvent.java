/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.dto;

import com.scoperetail.fusion.auditor.enums.Event;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DomainEvent {
  public String eventId;
  public Event eventName;
  private List<String> eventIdKeys;
  public String payload;
  @Builder.Default public LocalDateTime timestamp = LocalDateTime.now();
}
