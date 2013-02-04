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

package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.AbstractParser;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.XMLReaderHierarchyChangeListener;
import org.milyn.xml.hierarchy.HierarchyChangeReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class replaces {@linkplain org.milyn.delivery.sax.SAXParser parser} and
 * provides the identical function except it instantiates a
 * {@linkplain org.slc.sli.ingestion.smooks.SliSAXHandler saxHandler} instead of
 * a {@linkplain org.milyn.delivery.sax.SAXHandler saxHandler} in the
 * {@linkplain org.milyn.delivery.sax.SAXParser parser}.
 * <p>
 * In its constructor, a
 * {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler handler}
 * is injected in order to provide a callback to
 * {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler#setDocumentLocator(Locator)}.
 *
 * @author slee
 *
 */
public class SliSAXParser extends AbstractParser {

    /**
     * {@linkplain org.slc.sli.ingestion.smooks.SliSAXHandler} is used instead of
     * {@linkplain org.milyn.delivery.sax.SAXHandler}.
     */
    private SliSAXHandler saxHandler;
    /**
     * handler where the locator shall be handled.
     */
    private SliDocumentLocatorHandler handler;

    /**
     * Constructor.
     *
     * @param execContext
     * @param handler
     */
    public SliSAXParser(final ExecutionContext execContext,
            final SliDocumentLocatorHandler handler) {
        super(execContext);
        this.handler = handler;
    }

    /**
     * Same as in {@linkplain org.milyn.delivery.sax.SAXParser parser} except
     * at the beginning we force a nullcheck of executionContext and
     * at the line where new SliSAXHandler is used to replace new SAXHandler.
     *
     * @param source
     * @param result
     * @param executionContext
     * @return Writer
     * @throws SAXException
     * @throws IOException
     */
    @SuppressWarnings("unused")
    protected final Writer parse(final Source source, final Result result, final ExecutionContext executionContext) throws SAXException, IOException {

        if (executionContext == null) {
            throw new SAXException("null ExecutionContext");
        }
        Writer writer = getWriter(result, executionContext);
        ContentDeliveryConfig deliveryConfig = executionContext.getDeliveryConfig();
        XMLReader saxReader = getXMLReader(executionContext);

        saxHandler = new SliSAXHandler(getExecContext(), writer, this.handler);

        try {
            if (saxReader == null) {
                saxReader = deliveryConfig.getXMLReader();
            }
            if (saxReader == null) {
                saxReader = createXMLReader();
            }
            attachXMLReader(saxReader, executionContext);

            configureReader(saxReader, saxHandler, executionContext, source);
            if (executionContext != null) {
                if (saxReader instanceof HierarchyChangeReader) {
                    ((HierarchyChangeReader) saxReader).setHierarchyChangeListener(new XMLReaderHierarchyChangeListener(executionContext));
                }
                saxReader.parse(createInputSource(saxReader, source, executionContext.getContentEncoding()));
            } else {
                saxReader.parse(createInputSource(saxReader, source, Charset.defaultCharset().name()));
            }
        } finally {
            try {
                if (executionContext != null && saxReader instanceof HierarchyChangeReader) {
                    ((HierarchyChangeReader) saxReader).setHierarchyChangeListener(null);
                }
            } finally {
                try {
                    if (saxReader != null) {
                        try {
                            detachXMLReader(executionContext);
                        } finally {
                            deliveryConfig.returnXMLReader(saxReader);
                        }
                    }
                } finally {
                    saxHandler.detachHandler();
                }
            }
        }

        return writer;
    }

    /**
     * Same as in {@linkplain org.milyn.delivery.sax.SAXParser parser}.
     *
     */
    public final void cleanup() {
        if (saxHandler != null) {
            saxHandler.cleanup();
        }
    }
}
