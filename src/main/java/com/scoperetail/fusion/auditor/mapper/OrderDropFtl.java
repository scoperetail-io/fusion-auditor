package com.scoperetail.fusion.auditor.mapper;

import com.scoperetail.fusion.auditor.dto.order.OrderDropRequest;
import freemarker.template.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Locale;

@Slf4j
@Component
public class OrderDropFtl {

  public void mapToOrderDropXml(OrderDropRequest orderDropRequest) {
    try {
      Configuration config = new Configuration();
      config.setClassForTemplateLoading(OrderDropFtl.class, "/templates/");
      config.setIncompatibleImprovements(new Version(2, 3, 20));
      config.setDefaultEncoding("UTF-8");
      config.setLocale(Locale.US);
      config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

      //Map<String, Object> mapper = new HashMap<String, Object>();
      Template template = config.getTemplate("PickingSubsystemOrderReleaseMessage.ftl");

      Writer consoleWriter = new OutputStreamWriter(System.out);
      template.process(orderDropRequest, consoleWriter);
      // For the sake of example, also write output into a file:
      Writer fileWriter = new FileWriter(new File("order_drop.xml"));
      try {
        template.process(orderDropRequest, fileWriter);
      } finally {
        fileWriter.close();
      }
    } catch (IOException e) {
      log.error("IOException: ", e);
    } catch (TemplateException e) {
      log.error("TemplateException: ", e);
    }
  }
}
