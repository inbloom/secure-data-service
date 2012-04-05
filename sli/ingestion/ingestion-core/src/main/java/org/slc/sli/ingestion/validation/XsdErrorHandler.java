package org.slc.sli.ingestion.validation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(XsdErrorHandler.class);

    private MessageSource messageSource;

    private boolean isValid;

    private Map<String, String> saxToIngestionErrorCodes;

    public XsdErrorHandler() {
        setIsValid(true);
    }

/*    public XsdErrorHandler(ErrorReport errorReport) {
        this.errorReport = errorReport;
        setIsValid(true);
    }*/

    /**
     * Report a SAX parsing warning.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    public void warning(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex.getMessage());
        errorReport.warning(errorMessage, XsdErrorHandler.class);
        LOG.warn("WARNING: " + errorMessage);
    }

    /**
     * Report a SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    public void error(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex.getMessage());
        errorReport.error(errorMessage, XsdErrorHandler.class);
        LOG.error("ERROR: " + errorMessage);
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
        String errorMessage = getErrorMessage(ex.getMessage());
        errorReport.fatal(errorMessage, XsdErrorHandler.class);
        LOG.error("FATAL ERROR: " + errorMessage);
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

        // Split error message using :; error messages will be of the format errorcode:errormessage
        int splitpoint = saxErrorMessage.indexOf(':');
        String saxErrorcode = saxErrorMessage.substring(0, splitpoint);
        String saxErrorMessageDetail = saxErrorMessage.substring(splitpoint + 1, saxErrorMessage.length());

        // Get the corresponding ingestion message code from the SAX error code.
        String messageCode = getMessageCode(saxErrorcode);

        // Extract the arguments from the SAX error message. // Args are delineated by "''".
        String[] saxErrorMessageTokens = saxErrorMessageDetail.split("'");
        String[] messageArgs = new String[saxErrorMessageTokens.length / 2];
        for (int i = 0; i < messageArgs.length; i++) {
            // Remove double-quoted substrings.
            String[] argTokens = saxErrorMessageTokens[(i * 2) + 1].split("\"");
            String arg = new String();
                for (int j = 0; j < argTokens.length; j += 2) {
                    arg += argTokens[j];
                }
                // Remove colons.
/*                arg = arg.replaceAll("[{:}]", "");*/
                arg = arg.replaceAll(":", "");
                messageArgs[i] = arg;
        }

        // Return the ingestion error message.
        return messageSource.getMessage(messageCode, messageArgs, "#?" + messageCode + "?#", null);
    }

    /**
     * Convert the SAX error code into an ingestion error code.
     *
     * @param saxErrorCode
     *            Error message code returned by SAX
     * @return Error message code returned by ingestion
     */
    private String getMessageCode(String saxErrorCode) {
        // Convert the SAX error code into an ingestion error code.
        String errorCode = null;
        if (saxToIngestionErrorCodes.containsKey(saxErrorCode)) {
            errorCode = saxToIngestionErrorCodes.get(saxErrorCode);
        } else {
            errorCode = saxToIngestionErrorCodes.get("UNKNOWN-CVC-CODE");
        }

        return errorCode;
    }

    public Map<String, String> getSaxToIngestionErrorCodes() {
        return this.saxToIngestionErrorCodes;
    }

    public void setSaxToIngestionErrorCodes(Map<String, String> saxToIngestionErrorCodes) {
        this.saxToIngestionErrorCodes = saxToIngestionErrorCodes;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

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

}
