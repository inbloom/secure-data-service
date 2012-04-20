package org.slc.sli.modeling.xsd2xmi.cmdline;

import java.io.FileNotFoundException;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.writer.XmiWriter;
import org.slc.sli.modeling.xsd.XsdReader;
import org.slc.sli.modeling.xsd2xmi.core.Xsd2UmlConvert;
import org.slc.sli.modeling.xsd2xmi.core.Xsd2UmlLinker;
import org.slc.sli.modeling.xsd2xmi.core.Xsd2UmlPlugin;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with limitations).
 */
public final class Xsd2Xmi {

    public static void main(final String[] args) {
        try {
            // final String name = "Ed-Fi-Core";
            // final String path = "../../modeling/data/";
            // final Xsd2UmlPlugin plugin = new Xsd2UmlPluginForEdFi();

            final String name = "SLI";
            final String path = "../../domain/src/main/resources/sliXsd/";
            final Xsd2UmlPlugin plugin = new Xsd2UmlPluginForSLI();

            // final String name = "basic";
            // final String path = "../../modeling/test/";
            // final Xsd2UmlConvertPlugin plugin = new ConvertPluginGeneric();

            final String fileName = path.concat(name.concat(".xsd"));

            final XmlSchema schema = XsdReader.readSchema(fileName, new Xsd2XmlResolver(path));

            final Model extractedModel = Xsd2UmlConvert.transform(name, schema, plugin);
            final Model model = Xsd2UmlLinker.link(extractedModel, plugin);

            final Mapper mapper = new DefaultMapper(model);

            XmiWriter.writeDocument(model, mapper, name.concat(".xmi"));

        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Xsd2Xmi() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
