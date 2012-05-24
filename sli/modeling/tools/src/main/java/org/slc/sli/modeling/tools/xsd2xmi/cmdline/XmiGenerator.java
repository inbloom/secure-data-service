package org.slc.sli.modeling.tools.xsd2xmi.cmdline;

import java.io.FileNotFoundException;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.tools.xsd2xmi.core.Xsd2Uml;
import org.slc.sli.modeling.tools.xsd2xmi.core.Xsd2UmlPlugin;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.writer.XmiWriter;
import org.slc.sli.modeling.xsd.XsdReader;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with limitations).
 */
public final class XmiGenerator {

    public static void main(final String[] args) {
        try {
            convert("SLI", "../../domain/src/main/resources/sliXsd/", new Xsd2UmlPluginForSLI());
            convert("Ed-Fi-Core-1-0-03", "./", new Xsd2UmlPluginForEdFi(false));
            convert("Ed-Fi-Core-1-0-05", "./", new Xsd2UmlPluginForEdFi(false));
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private XmiGenerator() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }

    private static final void convert(final String name, final String path, final Xsd2UmlPlugin plugin)
            throws FileNotFoundException {
        final String fileName = path.concat(name.concat(".xsd"));

        final XmlSchema schema = XsdReader.readSchema(fileName, new Xsd2XmlResolver(path));

        final Model model = Xsd2Uml.transform(name, schema, plugin);

        final ModelIndex mapper = new DefaultModelIndex(model);

        XmiWriter.writeDocument(model, mapper, name.concat(".xmi"));
    }
}
