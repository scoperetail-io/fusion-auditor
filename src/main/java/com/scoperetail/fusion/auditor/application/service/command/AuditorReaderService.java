/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.application.service.command;

import java.io.IOException;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.auditor.adapter.out.persistence.jpa.AuditorPersistenceAdapterJpa;
import com.scoperetail.fusion.auditor.application.port.in.command.create.AuditorReaderUseCase;
import com.scoperetail.fusion.core.common.JsonUtils;
import com.scoperetail.fusion.shared.kernel.common.annotation.UseCase;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import lombok.AllArgsConstructor;

@UseCase
@AllArgsConstructor
public class AuditorReaderService implements AuditorReaderUseCase {

  private final AuditorPersistenceAdapterJpa auditorPersistenceAdapterJpa;

  @Override
  public void readAuditor(final Object message, final boolean isValid) throws Exception {
    final DomainEvent domainEvent = unmarshal(message);
    auditorPersistenceAdapterJpa.insertEventMsgLogAndMsgKey(domainEvent);
  }

  private DomainEvent unmarshal(final Object message) throws IOException {
    return JsonUtils.unmarshal(
        Optional.of((String) message), Optional.of(new TypeReference<DomainEvent>() {}));
  }
}
