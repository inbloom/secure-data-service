package org.slc.sli.modeling.tools.wadl2Doc;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;

public final class Wadl2Doc {

    public static void main(final String[] args) {

        try {
            final Map<String, String> prefixMappings = new HashMap<String, String>();
            final Application app = WadlReader.readApplication("wadl-original.xml");
            WadlWriter.writeDocument(app, prefixMappings,"wadl-clean.xml");
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
