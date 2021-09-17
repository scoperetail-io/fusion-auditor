/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.adapter.out.persistence.jpa;

/*-
 * *****
 * fusion-auditor
 * -----
 * Copyright (C) 2018 - 2021 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====
 */

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogEntity;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogKeyEntity;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import com.scoperetail.fusion.auditor.application.port.out.persistence.AuditorOutboundPort;
import com.scoperetail.fusion.auditor.common.DomainEventMapper;
import com.scoperetail.fusion.shared.kernel.common.annotation.PersistenceAdapter;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import com.scoperetail.fusion.shared.kernel.events.DomainProperty;
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
    saveMessageLogKeyEntity(domainEvent.getEventId(), domainEvent.getDomainProperties());
  }

  private void saveMessageLogKeyEntity(
      final String logKey, final Set<DomainProperty> domainProperties) {
    final Optional<MessageLogKeyEntity> optMessageLogKeyEntity =
        messageLogKeyRepository.findById(logKey);
    if (!optMessageLogKeyEntity.isPresent()) {
      final MessageLogKeyEntity msgLogKey = new MessageLogKeyEntity();
      msgLogKey.setLogKey(logKey);
      final TreeSet<DomainProperty> sortedDomainProperties = new TreeSet<>(domainProperties);
      final Iterator<DomainProperty> iterator = sortedDomainProperties.iterator();
      msgLogKey.setK01(iterator.hasNext() ? iterator.next().getValue() : null);
      msgLogKey.setK02(iterator.hasNext() ? iterator.next().getValue() : null);
      msgLogKey.setK03(iterator.hasNext() ? iterator.next().getValue() : null);
      msgLogKey.setK04(iterator.hasNext() ? iterator.next().getValue() : null);
      msgLogKey.setK05(iterator.hasNext() ? iterator.next().getValue() : null);
      messageLogKeyRepository.save(msgLogKey);
      log.debug("MessageLogKeyEntity successfully inserted: {}", msgLogKey);
    }
  }
}
