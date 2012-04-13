package org.slc.sli.modeling.xsd2xmi;

import java.io.FileNotFoundException;

import org.apache.ws.commons.schema.XmlSchema;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xmi.writer.XmiWriter;
import org.slc.sli.modeling.xsd2xmi.extract.Xsd2UmlConvert;
import org.slc.sli.modeling.xsd2xmi.linker.Xsd2UmlLinker;
import org.slc.sli.modeling.xsd2xmi.xsd.XsdReader;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with limitations).
 */
public final class Xsd2Xmi {
    
    public static void main(final String[] args) {
        try {
//            final String name = "SLI";
            final String name = "Ed-Fi-Core";
            final String path = "../../common/domain/src/main/resources/sliXsd/";
//            final String fileName = path.concat(name.concat(".xsd"));
            final String fileName = "../../modeling/data/Ed-Fi-Core.xsd";
            
            final XmlSchema schema = XsdReader.readSchema(fileName, new Xsd2XmlResolver(path));
            
            final Model extractedModel = Xsd2UmlConvert.extractModel(name, schema);
            
            final Model linkedModel = Xsd2UmlLinker.link(extractedModel);
            
            XmiWriter.writeDocument(linkedModel, name.concat(".xmi"));
            
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Xsd2Xmi() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
