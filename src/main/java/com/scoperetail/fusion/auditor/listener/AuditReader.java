/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.al.gif.jms.lib.service.ListenerJmsService;
import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.DedupeKeyEntity;
import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.MessageLogEntity;
import com.scoperetail.fusion.adapter.out.persistence.jpa.entity.MessageLogKeyEntity;
import com.scoperetail.fusion.adapter.out.persistence.jpa.repository.DedupeKeyRepository;
import com.scoperetail.fusion.adapter.out.persistence.jpa.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.adapter.out.persistence.jpa.repository.MessageLogRepository;
import com.scoperetail.fusion.auditor.dto.DomainEvent;
import com.scoperetail.fusion.auditor.enums.TaskResult;
import com.scoperetail.fusion.auditor.mapper.DomainEventMapper;
import com.scoperetail.fusion.auditor.mapper.JsonUtils;
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
  private static final String K01 = "k01";
  private static final String K02 = "k02";
  private static final String K03 = "k03";
  private static final String K04 = "k04";
  private static final String K05 = "k05";
  private DomainEventMapper domainEventMapper;
  private MessageLogRepository messageLogRepository;
  private MessageLogKeyRepository messageLogKeyRepository;
  private DedupeKeyRepository dedupeKeyRepository;

  @Override
  @Transactional
  public void process(String message) {
    log.info("Received message :: [{}]", message);
    TaskResult taskResult = TaskResult.FAILURE;
    try {
      DomainEvent domainEvent =
          JsonUtils.unmarshal(
              Optional.of(message), Optional.of(new TypeReference<DomainEvent>() {}));
      // If logKey is in database, update the status to duplicate
      Optional<MessageLogEntity> optMessageLogEntity =
          messageLogRepository.findByLogKey(domainEvent.getEventId());
      if (optMessageLogEntity.isPresent()) {
        optMessageLogEntity.get().setStatusCode(5);
        messageLogRepository.save(optMessageLogEntity.get());
        log.info("messageLogEntity successfully updated as duplicate");
        // TODO: Ask Tushar if this is needed
        DedupeKeyEntity dedupeKeyEntity =
            DedupeKeyEntity.builder()
                .logKey(domainEvent.getEventId())
                .createTs(LocalDateTime.now())
                .build();
        dedupeKeyRepository.save(dedupeKeyEntity);
        taskResult = TaskResult.DISCARD;
      } else {
        MessageLogEntity messageLogEntity = domainEventMapper.getMessageLogEntity(domainEvent);
        messageLogEntity.setStatusCode(
            1); // 1:Success, 2:App_error, 3:sys_error, 4:invalid_message, 5:duplicate
        messageLogRepository.save(messageLogEntity);
        log.info("messageLogEntity successfully inserted");
        MessageLogKeyEntity messageLogKeyEntity =
            getMessageLogKeyEntity(domainEvent.getEventId(), domainEvent.getEventIdKeys());
        messageLogKeyRepository.save(messageLogKeyEntity);
        log.info("messageLogKeyEntity successfully inserted");
        taskResult = TaskResult.SUCCESS;
      }
    } catch (IOException e) {
      log.error("Data Exception :: ", e);
      taskResult = TaskResult.DISCARD;
    } catch (Exception e) {
      log.error("General Exception {}, {}", message, e);
      taskResult = TaskResult.DISCARD;
    }
  }

  private MessageLogKeyEntity getMessageLogKeyEntity(String logKey, Map<String, String> keys) {
    MessageLogKeyEntity msgLogKey = new MessageLogKeyEntity();
    msgLogKey.setLogKey(logKey);
    keys.forEach((k, v) -> loadMsgKey(msgLogKey, k, v));
    return msgLogKey;
  }

  private void loadMsgKey(MessageLogKeyEntity entity, String key, String value) {
    if (key.equalsIgnoreCase(K01)) entity.setK01(value);
    else if (key.equalsIgnoreCase(K02)) entity.setK02(value);
    else if (key.equalsIgnoreCase(K03)) entity.setK03(value);
    else if (key.equalsIgnoreCase(K04)) entity.setK04(value);
    else if (key.equalsIgnoreCase(K05)) entity.setK05(value);
  }
}
