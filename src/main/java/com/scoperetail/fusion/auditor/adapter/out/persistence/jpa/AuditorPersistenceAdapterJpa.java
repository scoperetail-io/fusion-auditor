/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.adapter.out.persistence.jpa;

import java.time.LocalDateTime;
import java.util.Map;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogEntity;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogKeyEntity;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import com.scoperetail.fusion.auditor.application.port.out.persistence.AuditorOutboundPort;
import com.scoperetail.fusion.auditor.common.DomainEventMapper;
import com.scoperetail.fusion.shared.kernel.common.annotation.PersistenceAdapter;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@PersistenceAdapter
@AllArgsConstructor
@Slf4j
public class AuditorPersistenceAdapterJpa implements AuditorOutboundPort {
  private final MessageLogRepository messageLogRepository;
  private final MessageLogKeyRepository messageLogKeyRepository;
  private final DomainEventMapper domainEventMapper;

  @Override
  public void insertEventMsgLogAndMsgKey(final DomainEvent domainEvent) {
    final LocalDateTime ldt = LocalDateTime.now();
    final MessageLogEntity messageLogEntity =
        domainEventMapper.getMessageLogEntity(domainEvent, ldt);
    messageLogEntity.setStatusCode(1);
    messageLogRepository.save(messageLogEntity);
    log.debug("MessageLogEntity successfully inserted: {}", messageLogEntity);
    final MessageLogKeyEntity messageLogKeyEntity =
        getMessageLogKeyEntity(domainEvent.getEventId(), domainEvent.getKeyMap());
    messageLogKeyRepository.save(messageLogKeyEntity);
    log.debug("MessageLogKeyEntity successfully inserted: {}", messageLogKeyEntity);
  }

  private MessageLogKeyEntity getMessageLogKeyEntity(
      final String logKey, final Map<String, String> keys) {
    final MessageLogKeyEntity msgLogKey = new MessageLogKeyEntity();
    msgLogKey.setLogKey(logKey);

    int i = 1;
    for (final Map.Entry<String, String> entry : keys.entrySet()) {
      if (i == 1) msgLogKey.setK01(entry.getValue());
      else if (i == 2) msgLogKey.setK02(entry.getValue());
      else if (i == 3) msgLogKey.setK03(entry.getValue());
      else if (i == 4) msgLogKey.setK04(entry.getValue());
      else if (i == 5) msgLogKey.setK05(entry.getValue());
      i++;
    }
    return msgLogKey;
  }
}
