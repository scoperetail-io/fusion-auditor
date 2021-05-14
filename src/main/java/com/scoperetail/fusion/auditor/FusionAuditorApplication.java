/* ScopeRetail (C)2021 */
package com.scoperetail.fusion.auditor;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

@SpringBootApplication
@ComponentScan(basePackages = {"com.scoperetail.fusion.jms", "com.scoperetail.fusion.auditor"})
@EntityScan(
    basePackages = {
      "com.scoperetail.fusion.adapter.out.persistence",
      "com.scoperetail.fusion.auditor"
    })
@EnableJpaRepositories(basePackages = {"com.scoperetail.fusion.adapter.out.persistence"})
@EnableJms
@Configuration
public class FusionAuditorApplication extends SpringBootServletInitializer {
  @Value("${port.retry.broker.url}")
  private String DEFAULT_BROKER_URL;

  public static void main(String[] args) {
    SpringApplication.run(FusionAuditorApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(FusionAuditorApplication.class);
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
    return connectionFactory;
  }

  @Bean
  public JmsTemplate jmsTemplate() {
    JmsTemplate template = new JmsTemplate();
    template.setConnectionFactory(connectionFactory());
    return template;
  }

  @Bean
  MessageConverter converter() {
    return new SimpleMessageConverter();
  }
}
