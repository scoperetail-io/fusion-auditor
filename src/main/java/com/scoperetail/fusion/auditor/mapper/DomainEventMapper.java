/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.mapper;

import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.MessageLogEntity;
import com.scoperetail.fusion.auditor.dto.DomainEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainEventMapper {
  @Mapping(target = "logKey", source = "event.eventId")
  @Mapping(target = "eventId", source = "event.eventName")
  @Mapping(target = "sourceTs", source = "event.sourceTs")
  @Mapping(target = "payload", source = "event.payload")
  @Mapping(target = "createTs", source = "event.timestamp")
  MessageLogEntity getMessageLogEntity(DomainEvent event);
}
