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

public final class SdkGenGrammarsWrapper implements SdkGenGrammars {

    private List<XmlSchema> xmlSchemas;

    private static final Map<QName, QName> NAME_MAP = new HashMap<QName, QName>();
    static {
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "courseTranscript"),
                new QName("http://www.slcedu.org/api/v1", "studentTranscriptAssociation"));
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "courseTranscriptList"),
                new QName("http://www.slcedu.org/api/v1", "studentTranscriptAssociationList"));
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "staffEducationOrgAssignmentAssociation"),
                new QName("http://www.slcedu.org/api/v1", "staffEducationOrganizationAssociation"));
        NAME_MAP.put(new QName("http://www.slcedu.org/api/v1", "staffEducationOrgAssignmentAssociationList"),
                new QName("http://www.slcedu.org/api/v1", "staffEducationOrganizationAssociationList"));
    }

    public SdkGenGrammarsWrapper(final List<XmlSchema> xmlSchemas) {
        if (xmlSchemas == null) {
            throw new NullPointerException("xmlSchema");
        }
        this.xmlSchemas = Collections.unmodifiableList(new ArrayList<XmlSchema>(xmlSchemas));
    }

    @Override
    public XmlSchemaElement getElement(final QName elementName) {
        if (elementName == null) {
            throw new NullPointerException("elementName");
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
