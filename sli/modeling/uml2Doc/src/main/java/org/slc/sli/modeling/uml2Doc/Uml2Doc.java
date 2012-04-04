package org.slc.sli.modeling.uml2Doc;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting UML to Documentation.
 */
public final class Uml2Doc {
    
    public static void main(final String[] args) {
        try {
            @SuppressWarnings("unused")
            final Model model = XmiReader.readInterchange("../data/SLI.xmi");
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Uml2Doc() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
