/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.mapper;

import com.scoperetail.fusion.audit.persistence.entity.MessageLogEntity;
import com.scoperetail.fusion.auditor.dto.DomainEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainEventMapper {
  @Mapping(target = "logKey", source = "event.eventId")
  @Mapping(target = "eventId", source = "event.eventName")
  @Mapping(target = "payload", source = "event.payload")
  @Mapping(target = "createTs", source = "event.timestamp")
  MessageLogEntity getMessageLogEntity(DomainEvent event);
}
