package org.slc.sli.ingestion.validation;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author npandey
 *
 */
public class XsdErrorHandler implements XsdErrorHandlerInterface {

    ErrorReport errorReport;

    private boolean isValid;

    public XsdErrorHandler(ErrorReport errReport) {
        errorReport = errReport;
        setIsValid(true);
    }

    public void warning(SAXParseException ex) {
        // errorReport.error(getFailureMessage("MessageName", "param" ,XsdValidator.class));
    }

    public void error(SAXParseException ex) {
        // errorReport.error(getFailureMessage("MessageName", "param",XsdValidator.class));

        setIsValid(false);
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        setIsValid(false);
        throw ex;
    }

    @Override
    public void setIsValid(boolean value) {
        isValid = value;

    }

    @Override
    public boolean isValid() {
        return isValid;
    }

}