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

import java.io.Writer;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.SmooksContentHandler;
import org.milyn.delivery.sax.SAXHandler;
import org.xml.sax.Locator;
/**
 * This class extends {@linkplain org.milyn.delivery.sax.SAXHandler} and implements
 * a concrete {@linkplain #setDocumentLocator(Locator)} method.
 * <p>
 * In its constructor, a
 * {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler documentLocatorHandler}
 * is injected in order to provide a callback to
 * {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler#setDocumentLocator(Locator)}.
 *
 * @author slee
 *
 */
public class SliSAXHandler extends SAXHandler {
    /**
     * documentLocatorHandler where the locator shall be handled.
     */
    private SliDocumentLocatorHandler documentLocatorHandler;

    /**
     * Constructor.
     *
     * @param executionContext
     * @param writer
     * @param handler
     */
    public SliSAXHandler(final ExecutionContext executionContext, final Writer writer,
            final SliDocumentLocatorHandler handler) {
        super(executionContext, writer);
        this.documentLocatorHandler = handler;
    }

    /**
     * Constructor.
     *
     * @param executionContext
     * @param writer
     * @param parentContentHandler
     * @param handler
     */
    public SliSAXHandler(final ExecutionContext executionContext,
            final Writer writer, final SmooksContentHandler parentContentHandler,
            final SliDocumentLocatorHandler handler) {
        super(executionContext, writer, parentContentHandler);
        this.documentLocatorHandler = handler;
    }

    /**
     * Method allows parser to set the locator before parsing a document.
     *
     * @param locator
     */
    public final void setDocumentLocator(final Locator locator) {
        if (documentLocatorHandler != null) {
            documentLocatorHandler.setDocumentLocator(locator);
        }
    }

}
