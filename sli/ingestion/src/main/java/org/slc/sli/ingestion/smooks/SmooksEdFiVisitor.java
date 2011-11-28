package org.slc.sli.ingestion.smooks;

import java.io.IOException;

import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.annotation.StreamResultWriter;
import org.milyn.javabean.context.BeanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;

/**
* Basic transformer that simply renames an element.
*
* @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
*/

@StreamResultWriter
public class SmooksEdFiVisitor implements SAXElementVisitor {

    // Logging
    Logger log = LoggerFactory.getLogger(SmooksEdFiVisitor.class);
    
    protected String beanId;
    protected NeutralRecordFileWriter nrfWriter;
    
    public SmooksEdFiVisitor(String beanId, 
            NeutralRecordFileWriter nrfWriter) {
        this.beanId = beanId;
        this.nrfWriter = nrfWriter;
    }
    
    @Override
    public void visitBefore(SAXElement element, 
            ExecutionContext executionContext) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onChildElement(SAXElement element, SAXElement childElement,
            ExecutionContext executionContext) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onChildText(SAXElement element, SAXText childText, 
            ExecutionContext executionContext) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitAfter(SAXElement element, 
            ExecutionContext executionContext)
            throws IOException {

        BeanContext beanContext = executionContext.getBeanContext();
        NeutralRecord neutralRecord = (NeutralRecord) beanContext.getBean(beanId);
        
        if (executionContext.getTerminationError() != null) {
            
            // Indicate Smooks Validation Failure
            log.error(executionContext.getTerminationError().getMessage());
            log.error("Invalid Neutral Record: " + neutralRecord.toString());
        } else {
            
            // Write Neutral Record
            nrfWriter.writeRecord(neutralRecord);
        }
    }
}
