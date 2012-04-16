package org.slc.sli.modeling.uml2Doc;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting XMI to Diagrammatic Predicate Logic (or maybe just documentation :).
 */
public final class Uml2Doc {
    
    public static void main(final String[] args) {
        try {
            final Mapper model = new DefaultMapper(XmiReader.readModel("../data/SLI.xmi"));
            final Documentation<Identifier> docSource = DocumentationReader
                    .readDocumentation("../data/sli-pim-cfg.xml");
            final Documentation<Type> docExpanded = DocumentationExpander.expand(docSource, model);
            
            DocumentationWriter.writeDocument(docExpanded, model, "sli-pim-tmp.xml");
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Uml2Doc() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
