package org.slc.sli.modeling.tools.uml2Xsd.cmdline;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmConfigReader;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdWriter;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting UML and the platform specific model to W3C XMl Schemas.
 */
public final class WxsGenerator {

    public static void main(final String[] args) {
        try {
            // The UML model provides the types for the logical model.
            final ModelIndex model = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
            // The platform-specific model provides the implementation mappings.
            final PsmConfig<Type> psmConfig = PsmConfigReader.readConfig("../data/documents.xml", model);

            Uml2XsdWriter.writeDocuments(psmConfig.getDocuments(), model, new PluginForREST(), "sli-api.xsd");
            Uml2XsdWriter.writeDocuments(psmConfig.getDocuments(), model, new PluginForMongo(), "sli-mongo.xsd");
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private WxsGenerator() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
