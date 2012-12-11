package org.slc.sli.ingestion.smooks;

import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.xml.sax.Locator;

public class SliSmooksSAXFilter extends Filter
{
    private static Log logger = LogFactory.getLog(SmooksSAXFilter.class);
    
    private ExecutionContext executionContext;
    private SliSAXParser parser;
    private boolean closeSource;
    private boolean closeResult;

    public SliSmooksSAXFilter(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        closeSource = ParameterAccessor.getBoolParameter(Filter.CLOSE_SOURCE, true, executionContext.getDeliveryConfig());
        closeResult = ParameterAccessor.getBoolParameter(Filter.CLOSE_RESULT, true, executionContext.getDeliveryConfig());
        parser = new SliSAXParser(executionContext);
    }

    public Locator getDocumentLocator() {
        return parser.getDocumentLocator();
    }
    
    public void doFilter() throws SmooksException {
        Source source = FilterSource.getSource(executionContext);
        Result result = FilterResult.getResult(executionContext, StreamResult.class);

        doFilter(source, result);
    }

    protected void doFilter(Source source, Result result) {
        if (!(source instanceof StreamSource) && !(source instanceof JavaSource)) {
            throw new IllegalArgumentException(source.getClass().getName() + " Source types not yet supported by the SAX Filter. Only supports StreamSource and JavaSource at present.");
        }
        if(!(result instanceof FilterResult)) {
            if (!(result instanceof StreamResult) && result != null) {
                throw new IllegalArgumentException(result.getClass().getName() + " Result types not yet supported by the SAX Filter. Only supports StreamResult at present.");
            }
        }

        try {
            Writer writer = parser.parse(source, result, executionContext);
            writer.flush();
        } catch (TerminateException e) {
            if(logger.isDebugEnabled()) {
                if(e.isTerminateBefore()) {
                    logger.debug("Terminated filtering on visitBefore of element '" + SAXUtil.getXPath(e.getElement()) + "'.");
                } else {
                    logger.debug("Terminated filtering on visitAfter of element '" + SAXUtil.getXPath(e.getElement()) + "'.");                  
                }
            }
        } catch (Exception e) {
            throw new SmooksException("Failed to filter source.", e);
        } finally {
            if(closeSource) {
                close(source);
            }
            if(closeResult) {
                close(result);
            }
        }
    }

    public void cleanup() {
        parser.cleanup();
    }
}
