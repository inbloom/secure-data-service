package org.slc.sli.modeling.uml2Xsd;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmConfigExpander;
import org.slc.sli.modeling.psm.PsmConfigReader;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting UML to Documentation.
 */
public final class Uml2Xsd {
    
    public static void main(final String[] args) {
        try {
            final Mapper model = new DefaultMapper(XmiReader.readModel("../data/SLI.xmi"));
            final PsmConfig<Identifier> temp = PsmConfigReader.readConfig("../data/sli-psm-cfg.xml");
            final PsmConfig<Type> psmConfig = PsmConfigExpander.expand(temp, model);
            Uml2XsdWriter.writeDocument(psmConfig.getClassTypes(), model, new PluginForJson(), "sli-api-json.xsd");
            // Uml2XsdWriter.writeDocument(psmConfig.getClassTypes(), model, new PluginForXml(),
            // "sli-api-xml.xsd");
            // Uml2XsdWriter.writeDocument(psmConfig.getClassTypes(), model, new PluginForMongo(),
            // "sli-mongo.xsd");
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Uml2Xsd() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
