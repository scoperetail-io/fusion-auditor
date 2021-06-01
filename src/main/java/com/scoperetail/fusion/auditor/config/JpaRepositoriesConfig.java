/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.scoperetail.fusion")
@EntityScan(basePackages = {"com.scoperetail.fusion"})
public class JpaRepositoriesConfig {}
