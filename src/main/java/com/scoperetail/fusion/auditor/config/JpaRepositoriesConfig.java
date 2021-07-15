/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.scoperetail.fusion"})
public class JpaRepositoriesConfig {}
