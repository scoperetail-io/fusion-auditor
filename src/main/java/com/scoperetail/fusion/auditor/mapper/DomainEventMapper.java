/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.mapper;

import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.MessageLogEntity;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface DomainEventMapper {
  @Mapping(target = "logKey", source = "event.eventId")
  @Mapping(target = "eventId", source = "event.event")
  @Mapping(target = "sourceTs", source = "event.timestamp")
  @Mapping(target = "payload", source = "event.payload")
  @Mapping(target = "createTs", source = "now")
  MessageLogEntity getMessageLogEntity(DomainEvent event, LocalDateTime now);
}
