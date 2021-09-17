package com.scoperetail.fusion.auditor.eraser;

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

import java.time.LocalDate;
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

  @Value("${retentionDuration}")
  private Integer retentionDuration;

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
    final LocalDateTime pivoteDate = LocalDate.now().minusDays(retentionDuration).atStartOfDay();
    List<String> messageLogKeysToErase = messageLogRepository.findLogKeysToErase(pivoteDate, pageable);
    boolean hasMoreRecords = !messageLogKeysToErase.isEmpty();
    if(hasMoreRecords) {
      log.debug("Deleting records for these message log keys {}",
          messageLogKeysToErase.toString());

      deleteMessageLogRecords(messageLogKeysToErase, eraserData);
      deleteMessageLogKeyRecords(messageLogKeysToErase, eraserData);
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

  private Result generateResult(Integer count, String tableName) {
    return Result
        .builder()
        .count(Long.valueOf(count))
        .target(tableName)
        .operationCode(OperationCode.DELETE)
        .build();
  }

  @Value("${retentionDuration}")
  void setRetentionDuration(final Integer retentionDuration) {
    this.retentionDuration  = retentionDuration;
  }
}
