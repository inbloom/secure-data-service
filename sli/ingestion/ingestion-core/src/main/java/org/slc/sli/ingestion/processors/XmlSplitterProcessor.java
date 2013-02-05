/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.queues.MessageType;

/**
 * Splits an XML file into multiple, smaller interchange files that can be packaged and dispatched to internal landing zones.
 * @author smelody
 *
 */
@Component
public class XmlSplitterProcessor  implements Processor {


    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private void setExchangeHeaders(Exchange exchange, boolean hasErrors) {
        exchange.getIn().setHeader("hasErrors", hasErrors);
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.XML_FILE_SPLIT.name());
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        setExchangeHeaders(exchange, false);
    }
}
