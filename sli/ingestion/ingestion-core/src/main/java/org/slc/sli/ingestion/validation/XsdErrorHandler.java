package org.slc.sli.ingestion.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author npandey
 *
 */
public class XsdErrorHandler implements ErrorHandler {

    ErrorReport errorReport;

    public XsdErrorHandler(ErrorReport errorReport)
    {
        this.errorReport = errorReport;
    }
    public void warning(SAXParseException ex) {
        //errorReport.error(getFailureMessage("MessageName", "param" ,XsdValidator.class));
    }

    public void error(SAXParseException ex) {
        //errorReport.error(getFailureMessage("MessageName", "param",XsdValidator.class));
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }

}