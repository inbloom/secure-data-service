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

import org.slc.sli.ingestion.Resource;
import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.FileResource;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.validation.ComplexValidator;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Validation Controller reads zip file or ctl file in a given directory and applies set of
 * pre-defined validators.
 *
 * @author mpatel
 */
public class ValidationController {

    private Handler<Resource, File> zipFileHandler;

    private Validator<IngestionFileEntry> complexValidator;

    private Validator<ControlFileDescriptor> controlFilevalidator;

    private AbstractMessageReport messageReport;

    private Source source = null;
    private ReportStats reportStats = null;

    /*
     * retrieve zip file or control file from the input directory and invoke
     * relevant validator
     */
    public void doValidation(File path) {
        if (path.isFile()) {
            source = new JobSource(null, path.getName(), null);
            reportStats = new SimpleReportStats(null, path.getName(), null);

            if (path.getName().endsWith(".ctl")) {
                processControlFile(path);
            } else if (path.getName().endsWith(".zip")) {
                processZip(path);
            } else {
                messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0001);
            }
        } else {
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0001);
        }
    }

    public void processValidators(ControlFile cfile) {
        boolean isValid = false;
        for (IngestionFileEntry ife : cfile.getFileEntries()) {
            if (ife.getFile() != null) {
                messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0002, ife.getFileName());
                isValid = complexValidator.isValid(ife, messageReport, reportStats, source);
                if (!isValid) {
                    messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0003, ife.getFileName());
                    continue;
                }
                messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0004, ife.getFileName());
            }
        }
    }

    public void processZip(File zipFile) {

        messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0005, zipFile.getAbsolutePath());

        FileResource zipFileResource = new FileResource(zipFile.getAbsolutePath());
        File ctlFile = zipFileHandler.handle(zipFileResource, messageReport, reportStats);

        if (!reportStats.hasErrors()) {

            processControlFile(ctlFile);
        }

        messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0006, zipFile.getAbsolutePath());
    }

    public void processControlFile(File ctlFile) {

        messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0007, ctlFile.getAbsolutePath());

        try {
            LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone(ctlFile.getAbsoluteFile().getParentFile());
            ControlFile cfile = ControlFile.parse(ctlFile);

            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);

            controlFilevalidator.isValid(cfd, messageReport, reportStats, source);
            processValidators(cfile);

        } catch (IOException e) {
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0008);
        } catch (SubmissionLevelException exception) {
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0010, exception.getMessage());
        } finally {
            messageReport.info(reportStats, source, ValidationMessageCode.VALIDATION_0009, ctlFile.getAbsolutePath());
        }
    }

    public Handler<Resource, File> getZipFileHandler() {
        return zipFileHandler;
    }

    public void setZipFileHandler(Handler<Resource, File> zipFileHandler) {
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
