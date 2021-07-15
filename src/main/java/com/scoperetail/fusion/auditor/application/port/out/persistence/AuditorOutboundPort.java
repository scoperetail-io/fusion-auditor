/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.application.port.out.persistence;

import com.scoperetail.fusion.shared.kernel.events.DomainEvent;

public interface AuditorOutboundPort {
  void insertEventMsgLogAndMsgKey(DomainEvent domainEvent);
}
