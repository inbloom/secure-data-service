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
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SliSAXParser extends AbstractParser
{

    private SliSAXHandler saxHandler;

    public SliSAXParser(ExecutionContext execContext) {
        super(execContext);
    }

    public Locator getDocumentLocator() {
        return saxHandler.getDocumentLocator();
    }
    
    protected Writer parse(Source source, Result result, ExecutionContext executionContext) throws SAXException, IOException {

        Writer writer = getWriter(result, executionContext);
        ContentDeliveryConfig deliveryConfig = executionContext.getDeliveryConfig();
        XMLReader saxReader = getXMLReader(executionContext);

        saxHandler = new SliSAXHandler(getExecContext(), writer);

        try {
            if(saxReader == null) {
                saxReader = deliveryConfig.getXMLReader();
            }
            if(saxReader == null) {
                saxReader = createXMLReader();
            }
            attachXMLReader(saxReader, executionContext);

            configureReader(saxReader, saxHandler, executionContext, source);
            if(executionContext != null) {
                if(saxReader instanceof HierarchyChangeReader) {
                    ((HierarchyChangeReader)saxReader).setHierarchyChangeListener(new XMLReaderHierarchyChangeListener(executionContext));
                }
                saxReader.parse(createInputSource(saxReader, source, executionContext.getContentEncoding()));
            } else {
                saxReader.parse(createInputSource(saxReader, source, Charset.defaultCharset().name()));
            }
        } finally {
            try {
                if(executionContext != null && saxReader instanceof HierarchyChangeReader) {
                    ((HierarchyChangeReader)saxReader).setHierarchyChangeListener(null);
                }
            } finally {
                try {
                    if(saxReader != null) {
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

    public void cleanup() {
        if(saxHandler != null) {
            saxHandler.cleanup();
        }
    }
}
