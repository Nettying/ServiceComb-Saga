/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.saga.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servicecomb.saga.core.SagaDefinition;
import io.servicecomb.saga.core.SagaException;
import io.servicecomb.saga.core.application.interpreter.FromJsonFormat;
import io.servicecomb.saga.transports.TransportFactory;
import java.io.IOException;
import kamon.annotation.EnableKamon;
import kamon.annotation.Segment;

@EnableKamon
public class JacksonFromJsonFormat implements FromJsonFormat {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TransportFactory transportFactory;

  public JacksonFromJsonFormat(TransportFactory transportFactory) {
    this.transportFactory = transportFactory;
  }

  @Segment(name = "fromJson", category = "application", library = "kamon")
  @Override
  public SagaDefinition fromJson(String requestJson) {
    try {
      JsonSagaDefinition definition = objectMapper.readValue(requestJson, JsonSagaDefinition.class);

      for (JsonSagaRequest request : definition.requests()) {
        request.with(transportFactory);
      }

      return definition;
    } catch (IOException e) {
      throw new SagaException("Failed to interpret JSON " + requestJson, e);
    }
  }
}
