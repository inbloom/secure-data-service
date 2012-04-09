package org.slc.sli.ingestion.validation;

import java.io.File;

import org.springframework.context.MessageSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author npandey
 *
 */
public class XsdErrorHandler implements XsdErrorHandlerInterface {

    private ErrorReport errorReport;

    private MessageSource messageSource;

    private String errorPrefix = "";

    private boolean isValid;

    public XsdErrorHandler() {
        setIsValid(true);
    }

    /**
     * Report a SAX parsing warning.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    @Override
    public void warning(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex);
        errorReport.warning(errorPrefix + errorMessage, XsdErrorHandler.class);
    }

    /**
     * Report a SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    @Override
    public void error(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex);
        errorReport.warning(errorPrefix + errorMessage, XsdErrorHandler.class);
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
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        String errorMessage = getErrorMessage(ex);
        errorReport.warning(errorPrefix + errorMessage, XsdErrorHandler.class);
        setIsValid(false);
        throw ex;
    }

    /**
     * Incorporate the SAX error message into an ingestion error message.
     *
     * @param saxErrorMessage
     *            Error message returned by SAX
     * @return Error message returned by Ingestion
     */
    private String getErrorMessage(SAXParseException ex) {
        // Create an ingestion error message incorporating the SAXParseException information.
        String fullParsefilePathname = (ex.getSystemId() == null) ? "" : ex.getSystemId();
        File parseFile = new File(fullParsefilePathname);
        String[] messageArgs = new String[4];
        messageArgs[0] = parseFile.getName();
        messageArgs[1] = String.valueOf(ex.getLineNumber());
        messageArgs[2] = String.valueOf(ex.getColumnNumber());
        messageArgs[3] = ex.getMessage();

        // Return the ingestion error message.
        return messageSource.getMessage("XSD_VALIDATION_ERROR", messageArgs, "#?XSD_VALIDATION_ERROR?#", null);
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void setErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    @Override
    public void setIsValid(boolean value) {
        isValid = value;

    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    public void setErrorPrefix(String errorPrefix) {
        this.errorPrefix = errorPrefix;
    }

}
