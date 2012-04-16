package org.slc.sli.modeling.xmi2Dpl;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting XMI to Diagrammatic Predicate Logic (or maybe just documentation :).
 */
public final class Xmi2Dpl {
    
    public static void main(final String[] args) {
        try {
            @SuppressWarnings("unused")
            final Model model = XmiReader.readModel("../data/SLI.xmi");
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Xmi2Dpl() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
