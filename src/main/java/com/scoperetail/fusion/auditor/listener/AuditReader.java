package com.scoperetail.fusion.auditor.listener;

/*-
 * *****
 * fusion-auditor
 * -----
 * Copyright (C) 2018 - 2021 Scope Retail Systems Inc.
 * -----
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =====
 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogEntity;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogKeyEntity;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import com.scoperetail.fusion.auditor.mapper.DomainEventMapper;
import com.scoperetail.fusion.auditor.mapper.JsonUtils;
import com.scoperetail.fusion.jms.service.ListenerJmsService;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("auditReader")
@AllArgsConstructor
public class AuditReader implements ListenerJmsService {
  private DomainEventMapper domainEventMapper;
  private MessageLogRepository messageLogRepository;
  private MessageLogKeyRepository messageLogKeyRepository;

  @Override
  @Transactional
  public void process(String message) {
    log.info("Received message: [{}]", message);
    TaskResult taskResult = TaskResult.FAILURE;
    try {
      LocalDateTime ldt = LocalDateTime.now();
      DomainEvent domainEvent =
          JsonUtils.unmarshal(
              Optional.of(message), Optional.of(new TypeReference<DomainEvent>() {}));
      MessageLogEntity messageLogEntity = domainEventMapper.getMessageLogEntity(domainEvent, ldt);
      messageLogEntity.setStatusCode(
          1); // 1:Success, 2:App_error, 3:sys_error, 4:invalid_message, 5:duplicate
      messageLogRepository.save(messageLogEntity);
      log.info("messageLogEntity successfully inserted");
      MessageLogKeyEntity messageLogKeyEntity =
          getMessageLogKeyEntity(domainEvent.getEventId(), domainEvent.getKeyMap());
      messageLogKeyRepository.save(messageLogKeyEntity);
      log.info("messageLogKeyEntity successfully inserted");
      taskResult = TaskResult.SUCCESS;
    } catch (IOException e) {
      log.error("Invalid Message: ", e);
      taskResult = TaskResult.DISCARD;
    }
  }

  private MessageLogKeyEntity getMessageLogKeyEntity(String logKey, Map<String, String> keys) {
    MessageLogKeyEntity msgLogKey = new MessageLogKeyEntity();
    msgLogKey.setLogKey(logKey);

    int i = 1;
    for (Map.Entry<String, String> entry : keys.entrySet()) {
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
enum TaskResult {
  SUCCESS, FAILURE, DISCARD;
}

