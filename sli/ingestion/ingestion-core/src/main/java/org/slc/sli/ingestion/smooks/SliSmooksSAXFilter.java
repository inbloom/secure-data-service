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

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.SmooksException;
import org.milyn.cdr.ParameterAccessor;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.delivery.sax.SAXUtil;
import org.milyn.delivery.sax.SmooksSAXFilter;
import org.milyn.delivery.sax.terminate.TerminateException;
import org.milyn.payload.FilterResult;
import org.milyn.payload.FilterSource;
import org.milyn.payload.JavaSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class replaces {@linkplain org.milyn.delivery.sax.SmooksSAXFilter filter} and
 * provides the identical function except it instantiates a
 * {@linkplain org.slc.sli.ingestion.smooks.SliSAXParser parser} instead of
 * a {@linkplain org.milyn.delivery.sax.SAXParser parser} in the original
 * {@linkplain org.milyn.delivery.sax.SmooksSAXFilter filter}.
 * <p>
 * In its constructor, a
 * {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler handler}
 * is injected in order to provide a callback to
 * {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler#setDocumentLocator(Locator)}.
 *
 * @author slee
 *
 */
public class SliSmooksSAXFilter extends Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmooksSAXFilter.class);

    private ExecutionContext executionContext;
    private SliSAXParser parser;
    private boolean closeSource;
    private boolean closeResult;

    /**
     * Constructor.
     *
     * @param executionContext
     */
    public SliSmooksSAXFilter(final ExecutionContext executionContext,
            SliDocumentLocatorHandler handler) {
        this.executionContext = executionContext;
        closeSource = ParameterAccessor.getBoolParameter(Filter.CLOSE_SOURCE, true, executionContext.getDeliveryConfig());
        closeResult = ParameterAccessor.getBoolParameter(Filter.CLOSE_RESULT, true, executionContext.getDeliveryConfig());
        parser = new SliSAXParser(executionContext, handler);
    }

    /**
     * Same as in {@linkplain org.milyn.delivery.sax.SmooksSAXFilter}.
     *
     */
    @Override
    public final void doFilter() throws SmooksException {
        Source source = FilterSource.getSource(executionContext);
        Result result = FilterResult.getResult(executionContext, StreamResult.class);

        doFilter(source, result);
    }

    /**
     * Same as in {@linkplain org.milyn.delivery.sax.SmooksSAXFilter}.
     *
     * @param source
     * @param result
     */
    protected final void doFilter(final Source source, final Result result) {
        if (!(source instanceof StreamSource) && !(source instanceof JavaSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported by the SAX Filter. Only supports StreamSource and JavaSource at present.");
        }
        if (!(result instanceof FilterResult)) {
            if (!(result instanceof StreamResult) && result != null) {
                throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported by the SAX Filter. Only supports StreamResult at present.");
            }
        }

        try {
            Writer writer = parser.parse(source, result, executionContext);
            writer.flush();
        } catch (TerminateException e) {
            if (LOGGER.isDebugEnabled()) {
                if (e.isTerminateBefore()) {
                    LOGGER.debug("Terminated filtering on visitBefore of element '{}'.", SAXUtil.getXPath(e.getElement()));
                } else {
                    LOGGER.debug("Terminated filtering on visitAfter of element '{}'.", SAXUtil.getXPath(e.getElement()));
                }
            }
        } catch (Exception e) {
            throw new SmooksException("Failed to filter source.", e);
        } finally {
            if (closeSource) {
                close(source);
            }
            if (closeResult) {
                close(result);
            }
        }
    }

    /**
     * Same as in {@linkplain org.milyn.delivery.sax.SmooksSAXFilter}.
     */
    @Override
    public final void cleanup() {
        parser.cleanup();
    }
}
