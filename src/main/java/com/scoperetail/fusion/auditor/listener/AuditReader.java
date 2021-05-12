/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.al.gif.jms.lib.service.ListenerJmsService;
import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.MessageLogEntity;
import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.MessageLogKeyEntity;
import com.scoperetail.fusion.adapter.out.persistence.jpa.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.adapter.out.persistence.jpa.repository.MessageLogRepository;
import com.scoperetail.fusion.auditor.mapper.DomainEventMapper;
import com.scoperetail.fusion.auditor.mapper.JsonUtils;
import com.scoperetail.fusion.messaging.adapter.in.messaging.jms.TaskResult;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import java.io.IOException;
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
  private static final String K01 = "k01";
  private static final String K02 = "k02";
  private static final String K03 = "k03";
  private static final String K04 = "k04";
  private static final String K05 = "k05";
  private DomainEventMapper domainEventMapper;
  private MessageLogRepository messageLogRepository;
  private MessageLogKeyRepository messageLogKeyRepository;

  @Override
  @Transactional
  public void process(String message) {
    log.info("Received message: [{}]", message);
    TaskResult taskResult = TaskResult.FAILURE;
    try {
      DomainEvent domainEvent =
          JsonUtils.unmarshal(
              Optional.of(message), Optional.of(new TypeReference<DomainEvent>() {}));
      MessageLogEntity messageLogEntity = domainEventMapper.getMessageLogEntity(domainEvent);
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
