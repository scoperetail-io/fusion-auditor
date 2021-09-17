/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.adapter.in.messaging.jms;

import static com.scoperetail.fusion.auditor.common.Event.AuditEvent;

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

import org.springframework.stereotype.Component;
import com.scoperetail.fusion.auditor.application.port.in.command.create.AuditorReaderUseCase;
import com.scoperetail.fusion.config.FusionConfig;
import com.scoperetail.fusion.core.adapter.in.messaging.jms.AbstractMessageListener;
import com.scoperetail.fusion.core.application.port.in.command.AuditUseCase;
import com.scoperetail.fusion.messaging.adapter.out.messaging.jms.MessageRouterReceiver;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;

@Component
public class AuditorReaderTaskHandler extends AbstractMessageListener {

  private final AuditorReaderUseCase auditorReaderUseCase;

  public AuditorReaderTaskHandler(
      final FusionConfig fusionConfig,
      final MessageRouterReceiver messageRouterReceiver,
      final AuditUseCase auditUseCase,
      final AuditorReaderUseCase auditorReaderUseCase) {
    super(AuditEvent.name(), null, messageRouterReceiver, fusionConfig, auditUseCase);
    this.auditorReaderUseCase = auditorReaderUseCase;
  }

  @Override
  public void handleValidationFailure(final String event) throws Exception {
    auditorReaderUseCase.handleValidationFailure(event);
  }

  @Override
  protected void handleMessage(final Object event) throws Exception {
    auditorReaderUseCase.readAuditor(event, true);
  }

  @Override
  public void handleFailure(final String message) {
    auditorReaderUseCase.send(getBoBrokerId(), getBoQueueName(), message);
  }

  @Override
  protected Class<DomainEvent> getClazz() {
    return DomainEvent.class;
  }
}
