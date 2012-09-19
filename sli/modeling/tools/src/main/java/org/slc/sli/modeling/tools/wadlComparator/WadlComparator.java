package org.slc.sli.modeling.tools.wadlComparator;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.tools.wadl2Doc.WadlViewerHandler;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WadlComparator {

    private static String pathToGoldenWadl;
    private static String pathToGeneratedWadl;
    private static PrintStream pathToReportFile;

    final static PrintStream stdout = System.out;
    
    public WadlComparator() {
    	throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3 || args[0].toLowerCase().equals("h") || args[0].equals("?")) {
            stdout.println("Usage:");
            stdout.println("java WadlComparator pathToGoldenWadl pathToGenerateWadl pathToReportFile");
            System.exit(1);
        }

        pathToGoldenWadl = args[0];
        pathToGeneratedWadl = args[1];
        pathToReportFile = new PrintStream(new File(args[2]));

        // read uris as strings from wadls
        OutputStream goldenStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(goldenStream));
        walkWadl(pathToGoldenWadl);
        List<String> golden = Arrays.asList(goldenStream.toString().split("\n"));
        goldenStream.close();

        OutputStream generatedStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(generatedStream));
        walkWadl(pathToGeneratedWadl);
        List<String> generated = Arrays.asList(generatedStream.toString().split("\n"));
        generatedStream.close();

        // identify common/different uris, and sort
        Map<String, Boolean> uniqueGoldenMap = new HashMap<String, Boolean>();
        for (String s : golden) {
            uniqueGoldenMap.put(s, true);
        }
        List<String> uniqueGenerated = new ArrayList<String>();
        for (String s : generated) {
            if (uniqueGoldenMap.containsKey(s)) {
                uniqueGoldenMap.remove(s);
            } else {
                uniqueGenerated.add(s);
            }
        }
        List<String> uniqueGolden = new ArrayList<String>(uniqueGoldenMap.keySet());
        Collections.sort(uniqueGolden);
        Collections.sort(uniqueGenerated);

        // print
        System.setOut(pathToReportFile);
        System.out.println("Unique to Golden Wadl:");
        for (String s : uniqueGolden) {
            System.out.println("\t" + s);
        }
        System.out.println("\nUnique to Generated Wadl:");
        for (String s : uniqueGenerated) {
            System.out.println("\t" + s);
        }
        System.setOut(stdout);
    }

    // shamelessly taken from WadlViewer
    private static void walkWadl(String filePath) {
        try {
            final Application app = WadlReader.readApplication(filePath);
            final WadlWalker walker = new WadlWalker(new WadlViewerHandler());
            walker.walk(app);
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
