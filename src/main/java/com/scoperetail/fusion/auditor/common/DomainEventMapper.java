/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.common;

import com.scoperetail.fusion.audit.persistence.entity.MessageLogEntity;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainEventMapper {
  @Mapping(target = "logKey", source = "event.eventId")
  @Mapping(target = "eventId", source = "event.event")
  @Mapping(target = "sourceTs", source = "event.timestamp")
  @Mapping(target = "payload", source = "event.payload")
  @Mapping(target = "createTs", source = "now")
  MessageLogEntity getMessageLogEntity(DomainEvent event, LocalDateTime now);
}
