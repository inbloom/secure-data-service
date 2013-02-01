/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.modeling.tools.wadlComparator;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.tools.wadl2Doc.WadlViewerHandler;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Compare two WADLs.
 */
public class WadlComparator {

    private static final Logger LOG = LoggerFactory.getLogger(WadlComparator.class);

    private static String pathToGoldenWadl;
    private static String pathToGeneratedWadl;
    private static PrintStream pathToReportFile;

    static final PrintStream STDOUT = System.out;

    public WadlComparator() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3 || args[0].toLowerCase().equals("h") || args[0].equals("?")) {
            STDOUT.println("Usage:");
            STDOUT.println("java WadlComparator pathToGoldenWadl pathToGenerateWadl pathToReportFile");
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
        LOG.info("Unique to Golden Wadl:");
        for (String s : uniqueGolden) {
            LOG.info("\t" + s);
        }
        LOG.info("\nUnique to Generated Wadl:");
        for (String s : uniqueGenerated) {
            LOG.info("\t" + s);
        }
        System.setOut(STDOUT);
    }

    // shamelessly taken from WadlViewer
    private static void walkWadl(String filePath) {
        try {
            final Application app = WadlReader.readApplication(filePath);
            final WadlWalker walker = new WadlWalker(new WadlViewerHandler());
            walker.walk(app);
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }
}
