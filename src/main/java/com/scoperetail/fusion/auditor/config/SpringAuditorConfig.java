/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.scoperetail.fusion.core.FusionCoreConfig;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties
@AllArgsConstructor
@Import({FusionCoreConfig.class})
@EnableScheduling
@ComponentScan(basePackages = {"com.scoperetail"})

public class SpringAuditorConfig {}
