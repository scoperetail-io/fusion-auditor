package com.scoperetail.fusion.auditor.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.al.gif.jms.lib.service.ListenerJmsService;
import com.scoperetail.fusion.auditor.dto.order.OrderDropRequest;
import com.scoperetail.fusion.auditor.mapper.JsonUtils;
import com.scoperetail.fusion.auditor.mapper.OrderDropFtl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component("orderDropReader")
@AllArgsConstructor
public class OrderDropReader implements ListenerJmsService {
  @Autowired
  OrderDropFtl orderDropFtl;

  @Override
  public void process(String message) {
    log.info("Received msg:[{}]", message);
    try {
      OrderDropRequest orderDropRequest =
        JsonUtils.unmarshal(Optional.of(message), Optional.of(new TypeReference<OrderDropRequest>() {}));
      log.info("Un marshall object:[{}]", orderDropRequest);
      orderDropFtl.mapToOrderDropXml(orderDropRequest);
      log.info("OrderDropRequest FTL map to XML successfully");
    } catch (IOException e) {
      log.error("Invalid message: ", e);
    }
  }
}
