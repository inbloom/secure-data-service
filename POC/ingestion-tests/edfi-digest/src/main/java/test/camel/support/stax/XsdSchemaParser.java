/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package test.camel.support.stax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import test.camel.support.stax.XsdCache.XsdTypeHolder;

import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * @author okrook
 *
 */
public class XsdSchemaParser {

    private Resource schemaFile;
    private EntityResolver entityResolver;
    private XSSchemaSet schemaSet;
    private Map<String, XsdCache> schemas = new HashMap<String, XsdCache>();

    protected void init() throws SAXException, IOException {
        XSOMParser parser = new XSOMParser();

        parser.setEntityResolver(entityResolver);
        parser.parse(schemaFile.getURL());

        schemaSet = parser.getResult();

        for (XSSchema schema : schemaSet.getSchemas()) {
            schemas.put(schema.getTargetNamespace(), XsdCache.init(schema));
        }
    }

    public XsdTypeHolder getRoot(QName root) {
        XsdCache xsd = schemas.get(root.getNamespaceURI());

        if (xsd != null) {
            return xsd.getTypeForElement(root.getLocalPart());
        }

        return null;
    }

    public Resource getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(Resource schemaFile) {
        this.schemaFile = schemaFile;
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
}
