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


package org.slc.sli.scaffold;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Document;

/**
 * Namespace resolver for xpath expressions
 * 
 * @author srupasinghe
 */
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
    public Iterator<?> getPrefixes(String namespaceURI) {
        return null;
    }
    
}
