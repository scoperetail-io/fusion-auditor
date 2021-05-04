/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.al.gif.jms.lib.service.ListenerJmsService;
import com.scoperetail.commons.json.util.JsonUtils;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogEntity;
import com.scoperetail.fusion.audit.persistence.entity.MessageLogKeyEntity;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import com.scoperetail.fusion.auditor.dto.DomainEvent;
import com.scoperetail.fusion.auditor.enums.TaskResult;
import com.scoperetail.fusion.auditor.mapper.DomainEventMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("auditReader")
public class AuditReader implements ListenerJmsService {
  @Autowired private DomainEventMapper domainEventMapper;
  @Autowired private MessageLogRepository messageLogRepository;
  @Autowired private MessageLogKeyRepository messageLogKeyRepository;

  @Override
  @Transactional
  public void process(String message) {
    log.info("Received message :: [{}]", message);
    TaskResult taskResult = TaskResult.FAILURE;
    try {
      DomainEvent domainEvent =
          JsonUtils.unmarshal(
              Optional.of(message), Optional.of(new TypeReference<DomainEvent>() {}));
      MessageLogEntity messageLogEntity = domainEventMapper.getMessageLogEntity(domainEvent);
      messageLogEntity.setMessageStatus(
          1); // 1:Success, 2:App_error, 3:sys_error, 4:invalid_message, 5:duplicate
      messageLogRepository.save(messageLogEntity);
      log.info("messageLogEntity successfully inserted");
      MessageLogKeyEntity messageLogKeyEntity =
          getMessageLogKeyEntity(domainEvent.getEventId(), domainEvent.getEventIdKeys());
      messageLogKeyRepository.save(messageLogKeyEntity);
      log.info("messageLogKeyEntity successfully inserted");
      taskResult = TaskResult.SUCCESS;
    } catch (IOException e) {
      log.error("Data Exception :: ", e);
      taskResult = TaskResult.DISCARD;
    } catch (Exception e) {
      log.error("General Exception {}, {}", message, e);
      taskResult = TaskResult.DISCARD;
    }
  }

  private MessageLogKeyEntity getMessageLogKeyEntity(String logKey, List<String> keys) {
    return MessageLogKeyEntity.builder()
        .logKey(logKey)
        .k01(keys.get(0))
        .k02(keys.size() > 1 ? keys.get(1) : null)
        .k03(keys.size() > 2 ? keys.get(2) : null)
        .k04(keys.size() > 3 ? keys.get(3) : null)
        .k05(keys.size() > 4 ? keys.get(4) : null)
        .build();
  }
}
