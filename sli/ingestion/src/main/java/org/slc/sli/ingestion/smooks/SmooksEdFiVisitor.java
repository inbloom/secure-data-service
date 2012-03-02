package org.slc.sli.ingestion.smooks;

import java.io.IOException;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.annotation.StreamResultWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.util.NeutralRecordUtils;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Visitor that writes a neutral record or reports errors encountered.
 *
 * @author dduran
 *
 */
@StreamResultWriter
public final class SmooksEdFiVisitor implements SAXElementVisitor {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(SmooksEdFiVisitor.class);

    private ResourceWriter<NeutralRecord> nrMongoStagingWriter;

    private final String beanId;
    private final NeutralRecordFileWriter nrfWriter;
    private final ErrorReport errorReport;

    private SmooksEdFiVisitor(String beanId, NeutralRecordFileWriter nrfWriter, ErrorReport errorReport) {
        this.beanId = beanId;
        this.nrfWriter = nrfWriter;
        this.errorReport = errorReport;
    }

    public static SmooksEdFiVisitor createInstance(String beanId, NeutralRecordFileWriter nrfWriter,
            ErrorReport errorReport) {
        return new SmooksEdFiVisitor(beanId, nrfWriter, errorReport);
    }

    public static SmooksEdFiVisitor createInstance(String beanId, NeutralRecordFileWriter nrfWriter) {
        return new SmooksEdFiVisitor(beanId, nrfWriter, null);
    }

    @Override
    public void visitBefore(SAXElement element, ExecutionContext executionContext) {
        // nothing
    }

    @Override
    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) {
        // nothing
    }

    @Override
    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) {
        // nothing

    }

    @Override
    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws IOException {

        NeutralRecord neutralRecord = (NeutralRecord) executionContext.getBeanContext().getBean(beanId);

        Throwable terminationError = executionContext.getTerminationError();
        if (terminationError == null) {

            // scrub empty strings in NeutralRecord
            neutralRecord.setAttributes(NeutralRecordUtils.scrubEmptyStrings(neutralRecord.getAttributes()));

            // Write NeutralRecord to file
            nrfWriter.writeRecord(neutralRecord);

            // write NeutralRecord to mongodb staging database
            nrMongoStagingWriter.writeResource(neutralRecord);

        } else {

            // Indicate Smooks Validation Failure
            LOG.error(terminationError.getMessage());
            LOG.error("Invalid Neutral Record: " + neutralRecord.toString());

            if (errorReport != null) {
                errorReport.error(terminationError.getMessage(), SmooksEdFiVisitor.class);
            }

        }
    }

    public void setNrMongoStagingWriter(ResourceWriter<NeutralRecord> nrMongoStagingWriter) {
        this.nrMongoStagingWriter = nrMongoStagingWriter;
    }

}
