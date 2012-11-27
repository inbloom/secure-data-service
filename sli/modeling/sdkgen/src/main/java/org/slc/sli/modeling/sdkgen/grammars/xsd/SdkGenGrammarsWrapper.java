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

package org.slc.sli.modeling.sdkgen.grammars.xsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;

import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;

/**
 * Wrap SDK grammar
 */
public final class SdkGenGrammarsWrapper implements SdkGenGrammars {

    private List<XmlSchema> xmlSchemas;

    private static final Map<QName, QName> NAME_MAP = new HashMap<QName, QName>();
    static {
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "courseTranscript"),
                new QName("http://www.slcedu.org/api/v1", "courseTranscript"));
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "courseTranscriptList"),
                new QName("http://www.slcedu.org/api/v1", "courseTranscriptList"));
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "staffEducationOrgAssignmentAssociation"),
                new QName("http://www.slcedu.org/api/v1", "staffEducationOrganizationAssociation"));
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "staffEducationOrgAssignmentAssociationList"),
                new QName("http://www.slcedu.org/api/v1", "staffEducationOrganizationAssociationList"));
    }

    public SdkGenGrammarsWrapper(final List<XmlSchema> xmlSchemas) {
        if (xmlSchemas == null) {
            throw new IllegalArgumentException("xmlSchema");
        }
        this.xmlSchemas = Collections.unmodifiableList(new ArrayList<XmlSchema>(xmlSchemas));
    }

    @Override
    public XmlSchemaElement getElement(final QName elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("elementName");
        }
        for (final XmlSchema xmlSchema : xmlSchemas) {
            final XmlSchemaElement element = xmlSchema.getElementByName(elementName);
            if (element != null) {
                return element;
            }
        }

        QName name = NAME_MAP.get(elementName);
        if (name != null) {
            return getElement(NAME_MAP.get(elementName));
        }

        return null;
    }
}
