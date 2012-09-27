package org.slc.sli.modeling.tools.wadl2Doc;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;

public class WadlViewer {
	
	public final static String DEFAULT_INPUT_FILENAME = "SLI.wadl";
	
	public WadlViewer() {
		throw new UnsupportedOperationException();
	}
	
    public static void main(final String[] args) {
    	String inputFilename = (args.length == 1) ? args[0] : DEFAULT_INPUT_FILENAME;
    	
        try {
            //@SuppressWarnings("unused")
            //final ModelIndex model = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
            final Application app = WadlReader.readApplication(inputFilename);
            final WadlWalker walker = new WadlWalker(new WadlViewerHandler());
            walker.walk(app);
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

}
