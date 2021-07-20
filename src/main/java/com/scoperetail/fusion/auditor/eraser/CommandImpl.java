package com.scoperetail.fusion.auditor.eraser;

import com.scoperetail.eraser.dto.Result;
import com.scoperetail.eraser.processor.Command;
import com.scoperetail.eraser.processor.CommandResult;
import com.scoperetail.eraser.properties.ConfigProperties;
import com.scoperetail.eraser.utility.OperationCode;
import com.scoperetail.fusion.audit.persistence.repository.DedupeKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CommandImpl implements Command, InitializingBean {
  private final MessageLogRepository messageLogRepository;
  private final MessageLogKeyRepository messageLogKeyRepository;
  private final DedupeKeyRepository dedupeKeyRepository;

  public CommandImpl(MessageLogRepository messageLogRepository, MessageLogKeyRepository messageLogKeyRepository,
                     DedupeKeyRepository dedupeKeyRepository) {
    this.messageLogRepository = messageLogRepository;
    this.messageLogKeyRepository = messageLogKeyRepository;
    this.dedupeKeyRepository = dedupeKeyRepository;
  }

  private static final String MESSAGE_LOG_TABLE_NAME = "message_log";
  private static final String MESSAGE_LOG_KEY_TABLE_NAME = "message_log_key";
  private static final String DEDUPE_KEY_TABLE_NAME = "dedupe_key";

  @Value("${retentionDuration}")
  private Integer retentionDuration;
  @Value("${eraser.frequency}")
  private String frequency;

  @Override
  public CommandResult execute(ConfigProperties configProperties, List<Result> resultList) {
    CommandResult commandResult = CommandResult.STOP;
    try {
      Boolean resultMessageLog = purgeAuditorRecords(configProperties, resultList);
      if(resultMessageLog)
        commandResult = CommandResult.CONTINUE;
    } catch (final Exception e) {
      commandResult = CommandResult.DELAY;
      log.error("Exception occurred while deleting records: ", e);
    }
    return commandResult;
  }

  private Boolean purgeAuditorRecords(final ConfigProperties configProperties, final List<Result> eraserData) {
    final Pageable pageable = PageRequest.of(0, configProperties.getBatchSize());
    final LocalDateTime pivoteDate = LocalDateTime.now().minusDays(retentionDuration); // remove records prior to last 7 days
    List<String> messageLogKeysToErase = messageLogRepository.findLogKeysToErase(pivoteDate, pageable);
    boolean hasMoreRecords = !messageLogKeysToErase.isEmpty();
    if(hasMoreRecords) {
      log.debug("Deleting records for these message log keys {}",
          messageLogKeysToErase.toString());

      deleteMessageLogRecords(messageLogKeysToErase, eraserData);
      deleteMessageLogKeyRecords(messageLogKeysToErase, eraserData);
      deleteDedupeKeyRecords(messageLogKeysToErase, eraserData);
    }
    return hasMoreRecords;
  }

  private void deleteMessageLogRecords(List<String> messageLogKeysToErase, List<Result> eraserData) {
    final Integer count = messageLogRepository.deleteMessageLog(messageLogKeysToErase);
    eraserData.add(generateResult(count, MESSAGE_LOG_TABLE_NAME));
    log.trace("Records deleted for {} table is {}", MESSAGE_LOG_TABLE_NAME, count);
  }

  private void deleteMessageLogKeyRecords(List<String> messageLogKeysToErase, List<Result> eraserData) {
    final Integer count = messageLogKeyRepository.deleteMessageLogKey(messageLogKeysToErase);
    eraserData.add(generateResult(count, MESSAGE_LOG_KEY_TABLE_NAME));
    log.trace("Records deleted for {} table is {}", MESSAGE_LOG_KEY_TABLE_NAME, count);
  }

  private void deleteDedupeKeyRecords(List<String> messageLogKeysToErase, List<Result> eraserData) {
    final Integer count = dedupeKeyRepository.deleteDedupeKey(messageLogKeysToErase);
    eraserData.add(generateResult(count, DEDUPE_KEY_TABLE_NAME));
    log.trace("Records deleted for {} table is {}", DEDUPE_KEY_TABLE_NAME, count);
  }

  private Result generateResult(Integer count, String tableName) {
    return Result
        .builder()
        .count(Long.valueOf(count))
        .target(tableName)
        .operationCode(OperationCode.DELETE)
        .build();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    log.info("retentionDuration {}", retentionDuration);
    log.info("frequency {}", frequency);
  }
}
