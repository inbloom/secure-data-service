package org.slc.sli.ingestion.smooks;

import java.io.IOException;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.annotation.StreamResultWriter;
import org.milyn.javabean.context.BeanContext;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // TODO Auto-generated method stub
    }

    @Override
    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws IOException {

        BeanContext beanContext = executionContext.getBeanContext();
        NeutralRecord neutralRecord = (NeutralRecord) beanContext.getBean(beanId);

        if (executionContext.getTerminationError() != null) {

            // Indicate Smooks Validation Failure
            LOG.error(executionContext.getTerminationError().getMessage());
            LOG.error("Invalid Neutral Record: " + neutralRecord.toString());

            if (errorReport != null) {
                errorReport.error(executionContext.getTerminationError().getMessage(), SmooksEdFiVisitor.class);
            }
        } else {

            // Write Neutral Record
            nrfWriter.writeRecord(neutralRecord);
        }
    }
}
