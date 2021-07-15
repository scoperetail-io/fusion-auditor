/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.application.service.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.auditor.adapter.out.persistence.jpa.AuditorPersistenceAdapterJpa;
import com.scoperetail.fusion.auditor.application.port.in.command.create.AuditorReaderUseCase;
import com.scoperetail.fusion.auditor.common.JsonUtils;
import com.scoperetail.fusion.messaging.adapter.in.messaging.jms.TaskResult;
import com.scoperetail.fusion.shared.kernel.common.annotation.UseCase;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@Slf4j
@AllArgsConstructor
public class AuditorReaderService implements AuditorReaderUseCase {

  private final AuditorPersistenceAdapterJpa auditorPersistenceAdapterJpa;

  @Override
  public void readAuditor(final Object message, final boolean isValid) throws Exception {
    processMessage(message);
  }

  private TaskResult processMessage(final Object message) {
    TaskResult taskResult = TaskResult.FAILURE;
    try {
      DomainEvent domainEvent = unmarshal(message);
      log.trace("message successfully unmarshalled");
      auditorPersistenceAdapterJpa.insertEventMsgLogAndMsgKey(domainEvent);
      log.trace("message was successfully processed");
      taskResult = TaskResult.SUCCESS;
    } catch (IOException e) {
      log.error("Invalid message: ", e);
      taskResult = TaskResult.DISCARD;
    }
    return taskResult;
  }

  private DomainEvent unmarshal(final Object message) throws IOException {
    return JsonUtils.unmarshal(
        Optional.of((String) message), Optional.of(new TypeReference<DomainEvent>() {}));
  }
}
