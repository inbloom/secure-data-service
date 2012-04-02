package org.slc.sli.ingestion.validation;

import org.springframework.context.MessageSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.validation.EntityValidationException;

/**
 *
 * @author npandey
 *
 */
public class XsdErrorHandler implements XsdErrorHandlerInterface {

    private ErrorReport errorReport;

    private MessageSource messageSource;

    private boolean isValid;

    public XsdErrorHandler(ErrorReport errorReport)
    {
        this.errorReport = errorReport;
        setIsValid(true);
    }

    /**
     * Report a SAX parsing warning.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    public void warning(SAXParseException ex) {
        errorReport.error(getFailureMessage("MessageName", "param", XsdValidator.class));
    }

    /**
     * Report a SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    public void error(SAXParseException ex) {
        errorReport.error(getFailureMessage("MessageName", "param", XsdValidator.class));
        setIsValid(false);

    }

    /**
     * Report a fatal SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     * @throws SAXParseException
     *             Parser exception thrown by SAX
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        setIsValid(false);
        throw ex;
    }


    /**
     * Convert the SAX error message into an ingestion error message.
     *
     * @param saxErrorMessage
     *            Error message returned by SAX
     * @return Error message returned by Ingestion
     */
   private String getErrorMessage(String saxErrorMessage) {
        // Convert the SAX error message into an ingestion error message.
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
    }

   public MessageSource getMessageSource() {
       return messageSource;
   }

   public void setMessageSource(MessageSource messageSource) {
       this.messageSource = messageSource;
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
