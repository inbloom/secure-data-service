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
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.container.standalone.StandaloneApplicationContext;
import org.milyn.delivery.ContentDeliveryConfig;
import org.milyn.delivery.Filter;
import org.milyn.delivery.FilterBypass;
import org.milyn.delivery.Visitor;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.types.FilterLifecycleEvent;
import org.milyn.javabean.context.BeanContext;
import org.milyn.javabean.context.preinstalled.Time;
import org.milyn.javabean.context.preinstalled.UniqueID;
import org.milyn.payload.FilterResult;
import org.milyn.payload.FilterSource;
import org.milyn.payload.JavaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * To implement the missing <code>setDocumentLocator</code> method in the
 * smooks package, four smooks-related classes are extended/replaced, namely:
 * <ol>
 * <li>{@linkplain org.slc.sli.ingestion.smooks.SliSmooks} which extends
 * {@linkplain org.milyn.Smooks},</li>
 * <li>{@linkplain org.slc.sli.ingestion.smooks.SliSmooksSAXFilter} which replaces
 * {@linkplain org.milyn.delivery.sax.SmooksSAXFilter},</li>
 * <li>{@linkplain org.slc.sli.ingestion.smooks.SliSAXParser} which replaces
 * {@linkplain org.milyn.delivery.sax.SAXParser},</li>
 * <li>{@linkplain org.slc.sli.ingestion.smooks.SliSAXHandler} which extends
 * {@linkplain org.milyn.delivery.sax.SAXHandler}.</li>
 * </ol>
 * <p>
 * Why so many classes? It is because the smooks package is a monolithic design, where
 * <p>
 * {@linkplain org.milyn.Smooks#_filter} creates a
 * {@linkplain org.milyn.delivery.sax.SmooksSAXFilter} (via
 * <code>deliveryConfig.newFilter(executionContext))</code>, and
 * <p>
 * {@linkplain org.milyn.delivery.sax.SmooksSAXFilter#SmooksSAXFilter} instantiate a private
 * {@linkplain org.milyn.delivery.sax.SAXParser}, and
 * <p>
 * {@linkplain org.milyn.delivery.sax.SAXParser#parse} create a
 * {@linkplain org.milyn.delivery.sax.SAXHandler} which implements
 * a no-op <code>{@linkplain org.xml.sax.helpers.DefaultHandler#setDocumentLocator}</code>.
 * <p>
 * This <code>SliSmooks</code> class extends  {@linkplain org.milyn.Smooks} and
 * implements {@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler}.
 * It keeps a <code>SmooksEdFiVisitor</code> reference and uses it to
 * call the visitors'
 * <code>{@linkplain org.slc.sli.ingestion.smooks.SliDocumentLocatorHandler#setDocumentLocator}</code> method.
 * <p>
 * Below are the funcionality this class provides beyond <code>Smooks</code>:
 * <ol>
 * <li>In <code>{@linkplain #addVisitor(Visitor, String, String)</code>, it sets the visitor to
 * <code>SmooksEdFiVisitor</code> before calling super's method.</li>
 * <li>In <code>{@linkplain #_filter}</code>, it creates a
 * {@linkplain SliSmooksSAXFilter} (instead of SmooksSAXFilter)</li>
 * <li>It implements {@linkplain #setDocumentLocator}</li>
 * <li>It implements {@linkplain #getFirstSmooksEdFiVisitor}</li>
 * </ol>
 *
 *
 * @author slee
 *
 */
public class SliSmooks extends Smooks implements SliDocumentLocatorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SliSmooks.class);
    /**
     * A SmooksEdFiVisitor that has been initialized and added.
     */
    private SmooksEdFiVisitor edFiVisitor;
    private SliDocumentLocatorHandler documentLocatorHandler;

    /**
     * Public Default Constructor.
     * <p/>
     */
    public SliSmooks() {
        super();
    }

    /**
     * Public Default Constructor.
     *
     * @param context
     */
    public SliSmooks(final StandaloneApplicationContext context) {
        super(context);
    }

    /**
     * Public constructor.
     *
     * @param resourceURI XML resource configuration stream URI.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     */
    public SliSmooks(final String resourceURI) throws IOException, SAXException {
        super(resourceURI);
    }

    /**
     * Public constructor.
     *
     * @param resourceConfigStream XML resource configuration stream.
     * @throws IOException  Error reading resource stream.
     * @throws SAXException Error parsing the resource stream.
     */
    public SliSmooks(InputStream resourceConfigStream) throws IOException, SAXException {
        super(resourceConfigStream);
    }

    /**
     * Add a visitor instance to <code>this</code> Smooks instance.
     * Also set it to <code>edFiVisitor</code> for
     * implementing <code>setDocumentLocator</code>.
     *
     * @param visitor The visitor implementation.
     * @param targetSelector The message fragment target selector.
     * @param targetSelectorNS The message fragment target selector namespace.
     */
    @Override
    public SmooksResourceConfiguration addVisitor(Visitor visitor, String targetSelector, String targetSelectorNS) {
        if (visitor instanceof SmooksEdFiVisitor) {
            edFiVisitor = (SmooksEdFiVisitor) visitor;
        }
        if (visitor instanceof SliDocumentLocatorHandler) {
            documentLocatorHandler = (SliDocumentLocatorHandler) visitor;
        }
        return super.addVisitor(visitor, targetSelector, targetSelectorNS);
    }

    /**
     * Filter the content in the supplied {@link Source} instance, outputing data
     * to the supplied {@link Result} instances.
     * Same as in {@linkplain org.milyn.Smooks}.
     *
     * @param executionContext The {@link ExecutionContext} for this filter operation. See
     *                         {@link #createExecutionContext(String)}.
     * @param source           The filter Source.
     * @param results          The filter Results.
     * @throws SmooksException Failed to filter.
     */
    @Override
    public final void filterSource(final ExecutionContext executionContext, final Source source, Result... results) throws SmooksException {
        AssertArgument.isNotNull(source, "source");
        AssertArgument.isNotNull(executionContext, "executionContext");

        if (super.getClassLoader() != null) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(super.getClassLoader());
            try {
                filter(executionContext, source, results);
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        } else {
            filter(executionContext, source, results);
        }
    }

    /**
     * Same as in {@linkplain org.milyn.Smooks} except
     * at the line where new SliSmooksSAXFilter is used to replace deliveryConfig.newFilter.
     *
     * @param executionContext
     * @param source
     * @param results
     */
    private void filter(final ExecutionContext executionContext, Source source, Result... results) {
        ExecutionEventListener eventListener = executionContext.getEventListener();

        try {
            Filter.setCurrentExecutionContext(executionContext);
            try {
                if (eventListener != null) {
                    eventListener.onEvent(new FilterLifecycleEvent(FilterLifecycleEvent.EventType.STARTED));
                }

                ContentDeliveryConfig deliveryConfig = executionContext.getDeliveryConfig();

                if (results != null && results.length == 1 && results[0] != null) {
                    FilterBypass filterBypass = deliveryConfig.getFilterBypass();
                    if (filterBypass != null && filterBypass.bypass(executionContext, source, results[0])) {
                        // We're done... a filter bypass was applied...
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("FilterBypass '{}' applied.", filterBypass.getClass().getName());
                        }
                        return;
                    }
                }

                Filter messageFilter = new SliSmooksSAXFilter(executionContext, this);
                Filter.setFilter(messageFilter);
                try {
                    // Attach the source and results to the context...
                    FilterSource.setSource(executionContext, source);
                    FilterResult.setResults(executionContext, results);

                    // Add pre installed beans...
                    BeanContext beanContext = executionContext.getBeanContext();
                    beanContext.addBean(Time.BEAN_ID, new Time());
                    beanContext.addBean(UniqueID.BEAN_ID, new UniqueID());

                    try {
                        deliveryConfig.executeHandlerInit(executionContext);
                        messageFilter.doFilter();
                    } finally {
                        try {
                            // We want to make sure that all the beans from the BeanContext are available in the
                            // JavaResult, if one is supplied by the user...
                            JavaResult javaResult = (JavaResult) FilterResult.getResult(executionContext, JavaResult.class);
                            if (javaResult != null) {
                                javaResult.getResultMap().putAll(executionContext.getBeanContext().getBeanMap());
                            }

                            // Remove the pre-installed beans...
                            beanContext.removeBean(Time.BEAN_ID, null);
                            beanContext.removeBean(UniqueID.BEAN_ID, null);
                        } finally {
                            deliveryConfig.executeHandlerCleanup(executionContext);
                        }
                    }
                } catch (SmooksException e) {   // NOPMD - Ignoring ExceptionAsFlowControl as it is not worth fixing Smooks
                    executionContext.setTerminationError(e);
                    throw e;
                } catch (Throwable t) {         // NOPMD - Ignoring AvoidCatchingThrowable as it is not worth fixing Smooks
                    executionContext.setTerminationError(t);
                    throw new SmooksException("Smooks Filtering operation failed.", t);
                } finally {
                    messageFilter.cleanup();
                    Filter.removeCurrentFilter();
                }
            } finally {
                Filter.removeCurrentExecutionContext();
            }
        } finally {
            if (eventListener != null) {
                eventListener.onEvent(new FilterLifecycleEvent(FilterLifecycleEvent.EventType.FINISHED));
            }
        }
    }

    @Override
    public final void setDocumentLocator(final Locator locator) {
        if (documentLocatorHandler != null) {
            documentLocatorHandler.setDocumentLocator(locator);
        }
    }

    /**
     *
     * @return SmooksEdFiVisitor
     */
    public SmooksEdFiVisitor getSmooksEdFiVisitor() {
        return edFiVisitor;
    }

}
