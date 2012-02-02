package org.slc.sli.scaffold;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Document;

public class DocumentNamespaceResolver implements NamespaceContext {
    private Document document = null;
    
    public DocumentNamespaceResolver(Document document) {
        this.document = document;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return document.lookupNamespaceURI(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
    
}
