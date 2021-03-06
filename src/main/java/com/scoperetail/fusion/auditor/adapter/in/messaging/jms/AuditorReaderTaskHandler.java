/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.adapter.in.messaging.jms;

import org.springframework.stereotype.Component;
import com.scoperetail.fusion.auditor.application.port.in.command.create.AuditorReaderUseCase;
import com.scoperetail.fusion.core.adapter.in.messaging.jms.AbstractMessageListener;
import com.scoperetail.fusion.messaging.adapter.out.messaging.jms.MessageRouterReceiver;
import com.scoperetail.fusion.shared.kernel.events.DomainEvent;

@Component
public class AuditorReaderTaskHandler extends AbstractMessageListener {

  private final AuditorReaderUseCase auditorReaderUseCase;

  public AuditorReaderTaskHandler(
      final MessageRouterReceiver messageRouterReceiver,
      final AuditorReaderUseCase auditorReaderUseCase) {
    super("fusionBroker", "FUSION.AUDIT.IN", MessageType.JSON, null, null, messageRouterReceiver);
    this.auditorReaderUseCase = auditorReaderUseCase;
  }

  @Override
  protected void handleMessage(final Object event, final boolean isValid) throws Exception {
    auditorReaderUseCase.readAuditor(event, isValid);
  }

  @Override
  protected Class<DomainEvent> getClazz() {
    return DomainEvent.class;
  }
}
