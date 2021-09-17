/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.application.service.command;

import static com.scoperetail.fusion.auditor.common.Event.AuditEvent;
import static com.scoperetail.fusion.config.Adapter.UsecaseResult.FAILURE;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.auditor.adapter.out.persistence.jpa.AuditorPersistenceAdapterJpa;
import com.scoperetail.fusion.auditor.application.port.in.command.create.AuditorReaderUseCase;
import com.scoperetail.fusion.config.Adapter;
import com.scoperetail.fusion.config.Config;
import com.scoperetail.fusion.config.FusionConfig;
import com.scoperetail.fusion.core.application.port.out.jms.PosterOutboundJmsPort;
import com.scoperetail.fusion.core.common.JsonUtils;
import com.scoperetail.fusion.shared.kernel.common.annotation.UseCase;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;
import lombok.AllArgsConstructor;

@UseCase
@AllArgsConstructor
public class AuditorReaderService implements AuditorReaderUseCase {

  private final AuditorPersistenceAdapterJpa auditorPersistenceAdapterJpa;
  private final PosterOutboundJmsPort posterOutboundJmsPort;
  private final FusionConfig fusionConfig;

  @Override
  public void handleValidationFailure(final String event) {
    final List<Adapter> failureAdapters = getFailuerOutboundAdapter();
    failureAdapters.forEach(adapter -> send(adapter.getBrokerId(), adapter.getQueueName(), event));
  }

  @Override
  public void readAuditor(final Object message, final boolean isValid) throws Exception {
    final DomainEvent domainEvent = unmarshal(message);
    auditorPersistenceAdapterJpa.insertEventMsgLogAndMsgKey(domainEvent);
  }

  @Override
  public void send(final String brokerId, final String queueName, final String message) {
    posterOutboundJmsPort.post(brokerId, queueName, message);
  }

  private DomainEvent unmarshal(final Object message) throws IOException {
    return JsonUtils.unmarshal(
        Optional.of((String) message), Optional.of(new TypeReference<DomainEvent>() {}));
  }

  private List<Adapter> getFailuerOutboundAdapter() {
    final Optional<Config> activeConfig = fusionConfig.getActiveConfig(AuditEvent.name());
    List<Adapter> adapters = new ArrayList<>(1);
    if (activeConfig.isPresent()) {
      adapters =
          activeConfig
              .get()
              .getAdapters()
              .stream()
              .filter(a -> FAILURE.equals(a.getUsecaseResult()))
              .collect(Collectors.toList());
    }
    return adapters;
  }
}
