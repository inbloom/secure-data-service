package org.slc.sli.ingestion.handler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.ReferenceConstructor;
import org.slc.sli.ingestion.ReferenceResolver;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.MD5;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * Handler to resolve and expand references to Ed-Fi references within ingested XML files.
 *
 * @author tshewchuk
 *
 */
public class ReferenceResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceResolutionHandler.class);

    private ErrorReport errorReport = null;

    private Map<String, String> referenceObjects = new HashMap<String, String>();

    private File xmlInputFile = null;
    private File xmlOutputFile = null;

    private long runTimeMSec = 0;

    /**
     * Construct the memory map of reference bodies.
     *
     * @param inputFilePath
     *            Full pathname of input XML file to be expanded.
     */
    private boolean constructReferenceMap(String inputFilePath, int[] numReferences) {
        // Construct the reference map in memory.
        long startTime = System.currentTimeMillis();
        ReferenceConstructor rc = new ReferenceConstructor();
        try {
            referenceObjects = rc.execute(inputFilePath, numReferences);
        } catch (Exception e) {
            // Report the error.
            logError("Cannot extract references from XML file " + xmlInputFile.getName());
            return false;
        }
        runTimeMSec += System.currentTimeMillis() - startTime;
        return true;
    }

    /**
     * Resolve and expand the references to references within input file entities using the
     * reference map.
     *
     * @param inputFilePath
     *            Full pathname of input XML file to be expanded.
     * @param outputFilePath
     *            Full pathname of expanded output XML file to be written.
     */
    private boolean resolveReferences(String inputFilePath, String outputFilePath, int[] numRefsToReferences,
            int[] numEntities) {
        // Resolve references to references using reference map; write to output file.
        long startTime = System.currentTimeMillis();
        xmlOutputFile = new File(outputFilePath);
        ReferenceResolver rr = new ReferenceResolver(referenceObjects);
        try {
            rr.execute(inputFilePath, outputFilePath, numRefsToReferences, numEntities);
        } catch (Exception e) {
            // Delete the output file and report the error.
            xmlOutputFile.delete();
            logError("Cannot resolve references in XML file " + xmlInputFile.getName());
            return false;
        }
        runTimeMSec += System.currentTimeMillis() - startTime;
        return true;
    }

    /**
     * Write an error message to the log and error files.
     *
     * @param errorMessage
     *            Error message to be written to the log and error files.
     */
    private void logError(String errorMessage) {
        // Log errors.
        LOG.error(errorMessage);
        errorReport.error(errorMessage, ReferenceResolutionHandler.class);
    }

    /**
     * Do the actual reference resolution handling of the input file.
     *
     * @param fileEntry
     *            Input XML file containing entities with references to references and the reference
     *            bodies.
     * @param errorReport
     *            Error report for reporting errors/warnings.
     */
    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        // Create the expanded output file.
        xmlInputFile = fileEntry.getFile();
        this.errorReport = errorReport;
        String inputFilePathname = xmlInputFile.getPath();
        String outputFilePathname = inputFilePathname.substring(0, inputFilePathname.lastIndexOf(".xml"))
                + "_RESOLVED.xml";

        // First, construct the reference list.
        int[] numReferences = { 0 };
        boolean referenceMapConstructed = constructReferenceMap(inputFilePathname, numReferences);
        if (referenceMapConstructed) {
            // Next, resolve the references.
            int[] numRefsToReferences = { 0 }, numEntities = { 0 };
            boolean referencesResolved = resolveReferences(inputFilePathname, outputFilePathname, numRefsToReferences,
                    numEntities);
            if (referencesResolved) {
                // Move the expanded output file to the input file, and return it.
                xmlInputFile.delete();
                if (!xmlOutputFile.renameTo(xmlInputFile)) {
                    logError("Error renaming " + xmlOutputFile.getName() + " to " + xmlInputFile.getName());
                }
                IngestionFileEntry outputFileEntry = new IngestionFileEntry(fileEntry.getFileFormat(),
                        fileEntry.getFileType(), xmlOutputFile.getName(), MD5.calculate(xmlOutputFile));
                outputFileEntry.setFile(xmlOutputFile);
                double runTimeSec = runTimeMSec / 1000.0;
                LOG.info(xmlInputFile.getName() + ": Resolved " + numRefsToReferences[0] + " references to "
                        + numReferences[0] + " Ed-Fi references for " + numEntities[0] + " Ed-Fi entities in "
                        + runTimeSec + " seconds");
                return outputFileEntry;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
