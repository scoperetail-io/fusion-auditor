/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.application.port.in.command.create;

public interface AuditorReaderUseCase {
  void readAuditor(Object event, boolean isValid) throws Exception;
}
