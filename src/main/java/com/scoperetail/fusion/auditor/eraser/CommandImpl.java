package com.scoperetail.fusion.auditor.eraser;

import com.scoperetail.eraser.dto.Result;
import com.scoperetail.eraser.processor.Command;
import com.scoperetail.eraser.processor.CommandResult;
import com.scoperetail.eraser.properties.ConfigProperties;
import com.scoperetail.eraser.utility.OperationCode;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CommandImpl implements Command {
  private final MessageLogRepository messageLogRepository;
  private final MessageLogKeyRepository messageLogKeyRepository;

  public CommandImpl(MessageLogRepository messageLogRepository, MessageLogKeyRepository messageLogKeyRepository) {
    this.messageLogRepository = messageLogRepository;
    this.messageLogKeyRepository = messageLogKeyRepository;
  }

  private static final String MESSAGE_LOG_TABLE_NAME = "message_log";
  private static final String MESSAGE_LOG_KEY_TABLE_NAME = "message_log_key";
  private static final String LOG_FILES = "auditor_log_file";

  @Value("${retentionDuration}")
  private Integer retentionDuration;

  @Value("${logsDirectory}")
  private String logsDirectory;

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
    final LocalDateTime pivoteDate = LocalDateTime.now().minusDays(retentionDuration);
    List<String> messageLogKeysToErase = messageLogRepository.findLogKeysToErase(pivoteDate, pageable);
    boolean hasMoreRecords = !messageLogKeysToErase.isEmpty();
    if(hasMoreRecords) {
      log.debug("Deleting records for these message log keys {}",
          messageLogKeysToErase.toString());

      deleteMessageLogRecords(messageLogKeysToErase, eraserData);
      deleteMessageLogKeyRecords(messageLogKeysToErase, eraserData);
    }
    //Also remove older log files
    deleteLogFiles(eraserData);
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

  private void deleteLogFiles(List<Result> eraserData) {
    Integer count = 0;
    File directory = new File(logsDirectory);
    if(directory.exists()) {
      File[] listFiles = directory.listFiles();
      long purgeTime = System.currentTimeMillis() - (retentionDuration * 24 * 60 * 60 * 1000);
      assert listFiles != null;
      for(File listFile : listFiles) {
        if(listFile.lastModified() < purgeTime) {
          if(!listFile.delete()) {
            log.error("Unable to delete file: {}", listFile);
          } else {
            count++;
            log.trace("Deleted file {}", listFile);
          }
        }
      }
      eraserData.add(generateResult(count, LOG_FILES));
      log.trace("Log files deleted for {} table is {}", LOG_FILES, count);
    } else {
      log.warn("Files were not deleted, directory {} doesn't exist!", logsDirectory);
    }
  }

  private Result generateResult(Integer count, String tableName) {
    return Result
        .builder()
        .count(Long.valueOf(count))
        .target(tableName)
        .operationCode(OperationCode.DELETE)
        .build();
  }
}
