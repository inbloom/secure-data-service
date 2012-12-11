package org.slc.sli.ingestion.smooks;

import java.io.Writer;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.SmooksContentHandler;
import org.milyn.delivery.sax.SAXHandler;
import org.xml.sax.Locator;

public class SliSAXHandler extends SAXHandler
{
    private Locator locator;

    public SliSAXHandler(ExecutionContext executionContext, Writer writer) {
        super(executionContext, writer);
    }

    public SliSAXHandler(ExecutionContext executionContext, Writer writer, SmooksContentHandler parentContentHandler) {
        super(executionContext, writer, parentContentHandler);
    }

    public void setDocumentLocator (Locator locator) {
        this.locator = locator;
    }
    
    public Locator getDocumentLocator() {
        return locator;
    }
}
