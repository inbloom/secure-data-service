package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.referenceresolution.ReferenceConstructor;
import org.slc.sli.ingestion.referenceresolution.ReferenceResolver;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * Handler to resolve and expand references to Ed-Fi references within ingested XML files.
 *
 * @author tshewchuk
 *
 */
public class ReferenceResolutionHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    /**
     * Resolve and expand the references to references within input file entities using the
     * reference map.
     *
     * @param inputFile
     *            Input XML file to be expanded.
     * @param errorReport
     *            Error report to log errors.
     * @param log
     *            Logger to log errors and information.
     */
    private File resolveReferences(File inputFile, ErrorReport errorReport, Logger log) {
        // Construct the reference map in memory.
        Map<String, String> referenceObjects = new HashMap<String, String>();
        ReferenceConstructor rc = new ReferenceConstructor();
        File outputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml"))
                + "_RESOLVED.xml");
        try {
            // Construct reference memory map.
            referenceObjects = rc.execute(inputFile.getPath());

            // Resolve references to references using reference map; write to output file.
            ReferenceResolver rr = new ReferenceResolver(referenceObjects, errorReport);
            rr.execute(inputFile.getPath(), outputFile.getPath());

            // Move the expanded output file to the input file, and return it.
            inputFile.delete();
            if (!outputFile.renameTo(inputFile)) {
                logError("Error renaming " + outputFile.getName() + " to " + inputFile.getName(), errorReport, log);
                return null;
            }
            log.info(inputFile.getName() + ": Resolved references");
            return inputFile;
        } catch (SAXException se) {
            // Delete the output file and report the error.
            outputFile.delete();
            logError("Error resolving references in XML file " + inputFile.getName() + ": " + se.getMessage(),
                    errorReport, log);
            return null;
        } catch (ParserConfigurationException pce) {
            // Delete the output file and report the error.
            outputFile.delete();
            logError("Error configuring parser for XML file " + inputFile.getName() + ": " + pce.getMessage(),
                    errorReport, log);
            return null;
        } catch (IOException ie) {
            // Delete the output file and report the error.
            outputFile.delete();
            logError("Error writing expanded XML file " + inputFile.getName() + ": " + ie.getMessage(), errorReport,
                    log);
            return null;
        }
    }

    /**
     * Write an error message to the log and error files.
     *
     * @param errorMessage
     *            Error message to be written to the log and error files.
     * @param errorReport
     *            Error report to log errors.
     * @param log
     *            Logger to log errors and information.
     */
    private void logError(String errorMessage, ErrorReport errorReport, Logger log) {
        // Log errors.
        log.error(errorMessage);
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
     *
     * @return IngestionFileEntry
     *         Output XML file containing entities with resolved references.
     */
    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        // Create the expanded output file.
        Logger log = LoggerFactory.getLogger(ReferenceResolutionHandler.class);
        File inputFile = fileEntry.getFile();

        // Resolve the references.
        File outputFile = resolveReferences(inputFile, errorReport, log);
        if (outputFile != null) {
            // Return the expanded XML file.
            fileEntry.setFile(outputFile);
        }
        return fileEntry;

    }


}
