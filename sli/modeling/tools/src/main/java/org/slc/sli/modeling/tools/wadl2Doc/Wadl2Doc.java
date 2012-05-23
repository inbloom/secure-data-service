package org.slc.sli.modeling.tools.wadl2Doc;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;

public final class Wadl2Doc {

    public static void main(final String[] args) {

        try {
            final Application app = WadlReader.readApplication("wadl-original.xml");
            WadlWriter.writeDocument(app, "wadl-clean.xml");
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
