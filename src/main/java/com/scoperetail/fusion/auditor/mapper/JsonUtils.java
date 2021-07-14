package com.scoperetail.fusion.auditor.mapper;

/*-
 * *****
 * fusion-auditor
 * -----
 * Copyright (C) 2018 - 2021 Scope Retail Systems Inc.
 * -----
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =====
 */

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  private JsonUtils() {}

  static final ObjectMapper mapper = new ObjectMapper();

  public static final <T> T unmarshal(
      final Optional<String> message, Optional<TypeReference<T>> typeReference) throws IOException {
    String incomingMessage =
        message.orElseThrow(() -> new IOException("Unable to unmarshal :: Message = null"));
    TypeReference<T> incomingType =
        typeReference.orElseThrow(() -> new IOException("Unable to unmarshal :: Type = null"));
    log.debug("Trying to unmarshal json message {} into {} type", incomingMessage, incomingType);
    mapper.registerModule(new JavaTimeModule());
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    return mapper.readValue(incomingMessage, incomingType);
  }

  public static <E> String marshal(final Optional<E> obj) throws IOException {
    mapper.setSerializationInclusion(NON_NULL);
    return mapper.writeValueAsString(
        obj.orElseThrow(() -> new IOException("Unable to marshal :: obj = null")));
  }
}
