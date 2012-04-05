package org.slc.sli.modeling.uml2Xsd;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting UML to Documentation.
 */
public final class Uml2Xsd {
    
    public static void main(final String[] args) {
        try {
            final Model model = XmiReader.readInterchange("../data/SLI.xmi");
            Uml2XsdWriter.writeDocument(model, "SLI.xsd");
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Uml2Xsd() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
