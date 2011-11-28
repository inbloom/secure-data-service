package org.sli.ingestion.smooks;

import java.io.IOException;

import org.milyn.SmooksException;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXElementVisitor;
import org.milyn.delivery.sax.SAXText;
import org.milyn.delivery.sax.annotation.StreamResultWriter;
import org.milyn.javabean.context.BeanContext;
import org.milyn.container.ExecutionContext;
import org.sli.ingestion.NeutralRecord;
import org.sli.ingestion.NeutralRecordFileWriter;

/**
* Basic transformer that simply renames an element.
*
* @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
*/

@StreamResultWriter
public class SmooksEdFiVisitor implements SAXElementVisitor {

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
            throws SmooksException, IOException {

        BeanContext bc = executionContext.getBeanContext();
        NeutralRecord nr = (NeutralRecord) bc.getBean(beanId);
        // following line is only needed if we are using smooks' internal 
        // result stream writer.
        //Writer writer = element.getWriter(this);
        nrfWriter.writeRecord(nr);
    }
}
