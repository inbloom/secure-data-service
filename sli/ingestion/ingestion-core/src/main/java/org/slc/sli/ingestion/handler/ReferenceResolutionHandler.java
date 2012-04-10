package org.slc.sli.ingestion.handler;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.ReferenceConstructor;
import org.slc.sli.ingestion.ReferenceResolver;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * Handler to resolve and expand references to Ed-Fi references within ingested XML files.
 *
 * @author tshewchuk
 *
 */
public class ReferenceResolutionHandler extends AbstractIngestionHandler<File, File> {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceResolutionHandler.class);

    private ErrorReport errorReport = null;

    private Map<String, String> referenceObjects = new HashMap<String, String>();

    private File xmlInputFile = null;

    private Set<String> referenceObjectList = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add("StudentReference");
            add("AssessmentReference");
        }
    };

    /**
     * Construct the memory map of reference bodies.
     *
     * @param referenceSet
     *            Set of viable reference names.
     * @param inputFilePath
     *            Full pathname of input XML file to be expanded.
     */
    private void constructReferenceMap(Set<String> referenceSet, String inputFilePath) {
        long startTime = System.currentTimeMillis();

        ReferenceConstructor rc = new ReferenceConstructor(referenceSet);
        try {
            referenceObjects = rc.execute(inputFilePath);
        } catch (Exception e) {
            // Report the error.
            log("Cannot extract references from XML file " + xmlInputFile.getName());
        }

        log("--- Reference Map Population took (ms): " + (System.currentTimeMillis() - startTime));
    }

    /**
     * Resolve and expand the references to references within input file entities using the
     * reference map.
     *
     * @param referenceSet
     *            Set of viable reference names.
     * @param inputFilePath
     *            Full pathname of input XML file to be expanded.
     * @param outputFilePath
     *            Full pathname of expanded output XML file to be written.
     */
    private void resolveReferences(Set<String> referenceSet, String inputFilePath, String outputFilePath) {
        long startTime = System.currentTimeMillis();

        ReferenceResolver rr = new ReferenceResolver(referenceSet, referenceObjects);
        File outputFile = new File(outputFilePath);
        try {
            rr.execute(inputFilePath, outputFilePath);
        } catch (Exception e) {
            // Delete the output file and report the error.
            outputFile.delete();
            log("Cannot resolve references in XML file " + xmlInputFile.getName());
        }

        log("--- XML Denormalization took (ms): " + (System.currentTimeMillis() - startTime));
    }

    private void log(String errorMessage) {
        LOG.error(errorMessage);
        errorReport.error(errorMessage, ReferenceResolutionHandler.class);
    }

    /**
     * Do the actual reference resolution handling of the input file.
     *
     * @param item
     *            Input XML file containing entities with references to references and the reference
     *            bodies.
     * @param errorReport
     *            Error report for reporting errors/warnings.
     */
    @Override
    File doHandling(File item, ErrorReport errorReport) {
        // Create the expanded output file.
        xmlInputFile = item;
        this.errorReport = errorReport;
        String inputFilePathname = xmlInputFile.getPath();
        String outputFilePathname = inputFilePathname.substring(0, inputFilePathname.lastIndexOf(".xml"))
                + "_RESOLVED.xml";
        constructReferenceMap(referenceObjectList, inputFilePathname);
        resolveReferences(referenceObjectList, inputFilePathname, outputFilePathname);

        // Move the expanded output file to the input file, and return it.
        return null;
    }

    public void setReferenceObjectList(Set<String> referenceObjectList) {
        this.referenceObjectList = referenceObjectList;
    }
}
