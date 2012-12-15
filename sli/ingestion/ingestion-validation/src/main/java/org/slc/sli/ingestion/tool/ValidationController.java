/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.SimpleReportStats;
import org.slc.sli.ingestion.reporting.SimpleSource;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.validation.ComplexValidator;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Validation Controller reads zip file or ctl file in a given directory and applies set of
 * pre-defined validators.
 *
 * @author mpatel
 */
public class ValidationController {

    private Handler<File, File> zipFileHandler;

    private Validator<IngestionFileEntry> complexValidator;

    private Validator<ControlFileDescriptor> controlFilevalidator;

    private static Logger logger = LoggerUtil.getLogger();

    private AbstractMessageReport messageReport;

    private Source source = null;
    private ReportStats reportStats = null;

    /*
     * retrieve zip file or control file from the input directory and invoke
     * relevant validator
     */
    public void doValidation(File path) {
        if (path.isFile()) {
            source = new SimpleSource(null, path.getName(), null);
            reportStats = new SimpleReportStats(source);

            if (path.getName().endsWith(".ctl")) {
                processControlFile(path);
            } else if (path.getName().endsWith(".zip")) {
                processZip(path);
            } else {
                logger.error("Invalid input: No clt/zip file found");
            }
        } else {
            logger.error("Invalid input: No clt/zip file found");
        }
    }

    public void processValidators(ControlFile cfile) {
        boolean isValid = false;
        for (IngestionFileEntry ife : cfile.getFileEntries()) {
            if (ife.getFile() != null) {
                logger.info("Processing file: {} ...", ife.getFileName());
                isValid = complexValidator.isValid(ife, messageReport, reportStats);
                if (!isValid) {
                    logger.info("Processing of file: {} resulted in errors.", ife.getFileName());

                    continue;
                }
                logger.info("Processing of file: {} completed.", ife.getFileName());
            }
        }
    }

    public void processZip(File zipFile) {

        logger.info("Processing a zip file [{}] ...", zipFile.getAbsolutePath());

        File ctlFile = zipFileHandler.handle(zipFile, messageReport, reportStats);

        if (!reportStats.hasErrors()) {

            processControlFile(ctlFile);
        }

        logger.info("Zip file [{}] processing is complete.", zipFile.getAbsolutePath());
    }

    public void processControlFile(File ctlFile) {

        logger.info("Processing a control file [{}] ...", ctlFile.getAbsolutePath());

        try {
            LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
            lz.setDirectory(ctlFile.getAbsoluteFile().getParentFile());
            ControlFile cfile = ControlFile.parse(ctlFile);

            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);

            controlFilevalidator.isValid(cfd, messageReport, reportStats);
                processValidators(cfile);

        } catch (IOException e) {
            logger.error("Cannot parse control file", ValidationController.class);
        } catch (SubmissionLevelException exception) {
            logger.error(exception.getMessage());
        } finally {
            logger.info("Control file [{}] processing is complete.", ctlFile.getAbsolutePath());
        }
    }

    public Handler<File, File> getZipFileHandler() {
        return zipFileHandler;
    }

    public void setZipFileHandler(Handler<File, File> zipFileHandler) {
        this.zipFileHandler = zipFileHandler;
    }

    public ComplexValidator<IngestionFileEntry> getComplexValidator() {
        return (ComplexValidator<IngestionFileEntry>) complexValidator;
    }

    public void setComplexValidator(ComplexValidator<IngestionFileEntry> complexValidator) {
        this.complexValidator = complexValidator;
    }

    public Validator<ControlFileDescriptor> getControlFilevalidator() {
        return controlFilevalidator;
    }

    public void setControlFilevalidator(Validator<ControlFileDescriptor> controlFilevalidator) {
        this.controlFilevalidator = controlFilevalidator;
    }

    public AbstractMessageReport getMessageReport() {
        return messageReport;
    }

    public void setMessageReport(AbstractMessageReport messageReport) {
        this.messageReport = messageReport;
    }

}
