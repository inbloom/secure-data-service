package org.slc.sli.modeling.xsd2xmi;

import java.io.FileNotFoundException;

import org.apache.ws.commons.schema.XmlSchema;
import org.slc.sli.modeling.uml.DefaultModelLookup;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xmi.writer.XmiWriter;
import org.slc.sli.modeling.xsd2xmi.xsd.XsdReader;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with limitations).
 */
public final class Xsd2Xmi {
    
    public static void main(final String[] args) {
        try {
            final XmlSchema schema = XsdReader.readSchema("Ed-Fi-Core.xsd");
            final Xsd2UmlContext context = new Xsd2UmlContext();
            final DefaultModelLookup lookup = new DefaultModelLookup();
            final Model model = Xsd2UmlConvert.modelFromXmlSchema(schema, context, lookup);
            lookup.setModel(model);
            XmiWriter.writeDocument(model, "SLI.xmi");
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Xsd2Xmi() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
