/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.scoperetail.fusion.core.FusionCoreConfig;
import lombok.AllArgsConstructor;

@Configuration
@EnableConfigurationProperties
@AllArgsConstructor
@Import({FusionCoreConfig.class})
public class SpringAuditorConfig {}
