package org.slc.sli.modeling.xmigen;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.uml.Model;

public final class Xsd2Uml {
    public static final Model transform(final String name, final XmlSchema schema, final Xsd2UmlPlugin plugin) {
        final Model extract = Xsd2UmlConvert.extract(name, schema, plugin);
        return Xsd2UmlLinker.link(extract, plugin);
    }
}
