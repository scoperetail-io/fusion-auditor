/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.scoperetail.fusion"})
@AllArgsConstructor
public class SpringAuditorConfig {}
