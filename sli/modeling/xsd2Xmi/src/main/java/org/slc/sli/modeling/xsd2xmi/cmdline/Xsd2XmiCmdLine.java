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
public final class Xsd2XmiCmdLine {

    public static void main(final String[] args) {
        try {
            convert("SLI", "../../domain/src/main/resources/sliXsd/", new Xsd2UmlPluginForSLI());
            convert("Ed-Fi-Core", "../../modeling/data/", new Xsd2UmlPluginForEdFi());

        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Xsd2XmiCmdLine() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }

    private static final void convert(final String name, final String path, final Xsd2UmlPlugin plugin)
            throws FileNotFoundException {
        final String fileName = path.concat(name.concat(".xsd"));

        final XmlSchema schema = XsdReader.readSchema(fileName, new Xsd2XmlResolver(path));

        final Model extractedModel = Xsd2UmlConvert.transform(name, schema, plugin);
        final Model model = Xsd2UmlLinker.link(extractedModel, plugin);

        final Mapper mapper = new DefaultMapper(model);

        XmiWriter.writeDocument(model, mapper, name.concat(".xmi"));
    }
}
