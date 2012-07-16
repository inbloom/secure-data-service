package org.slc.sli.modeling.tools.wadl2Doc;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public class WadlViewer {

    public static void main(final String[] args) {
        try {
            @SuppressWarnings("unused")
            final ModelIndex model = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
            final Application app = WadlReader.readApplication("SLI.wadl");
            final WadlWalker walker = new WadlWalker(new WadlViewerHandler());
            walker.walk(app);
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

}
