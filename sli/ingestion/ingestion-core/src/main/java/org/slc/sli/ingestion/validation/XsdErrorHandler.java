package org.slc.sli.ingestion.validation;

import java.util.HashMap;
import java.util.StringTokenizer;

import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

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

    private final HashMap<String, String> saxToIngestionErrorCodes = new HashMap<String, String>() {
        {
            put("cvc-attribute.3", "XSD_INVALID_ATTRIBUTE");
            put("cvc-attribute.4", "XSD_INVALID_ATTRIBUTE");
            put("cvc-complex-type.3.1", "XSD_INVALID_ATTRIBUTE");
            put("cvc-complex-type.3.2.1", "XSD_INVALID_ATTRIBUTE");
            put("cvc-complex-type.3.2.2", "XSD_INVALID_ATTRIBUTE");
            put("cvc-complex-type.5.1", "XSD_INVALID_ATTRIBUTE");
            put("cvc-complex-type.5.2", "XSD_INVALID_ATTRIBUTE");
            put("cvc-elt.3.1", "XSD_INVALID_ATTRIBUTE");
            put("cvc-complex-type.4", "XSD_MISSING_ATTRIBUTE");
            put("cvc-datatype-valid.1.2.1", "XSD_INVALID_VALUE");
            put("cvc-datatype-valid.1.2.2", "XSD_INVALID_VALUE");
            put("cvc-datatype-valid.1.2.3", "XSD_INVALID_VALUE");
            put("cvc-complex-type.2.1", "XSD_NO_CHILDREN");
            put("cvc-complex-type.2.4.d", "XSD_NO_CHILDREN");
            put("cvc-elt.3.2.1", "XSD_NO_CHILDREN");
            put("cvc-elt.5.2.2.1", "XSD_NO_CHILDREN");
            put("cvc-type.3.1.2", "XSD_NO_CHILDREN");
            put("cvc-complex-type.2.2", "XSD_NO_CHILDREN");
            put("cvc-complex-type.2.3", "XSD_NO_CHILDREN");
            put("cvc-elt.1", "XSD_INVALID_ELEMENT");
            put("cvc-elt.5.2.2.2.1", "XSD_VALUE_DOESNT_MATCH");
            put("cvc-elt.5.2.2.2.2", "XSD_VALUE_DOESNT_MATCH");
            put("cvc-complex-type.2.4.a", "XSD_ELEMENT_OUT_OF_ORDER");
            put("cvc-type.3.1.3", "XSD_INVALID_CONTENT");
            put("cvc-complex-type.2.4.b", "XSD_INCOMPLETE_CONTENT");
            put("cvc-complex-type.2.4.c", "XSD_ELEMENT_MISSING");
            put("cvc-type.3.1.1", "XSD_ELEMENT_CANNOT_HAVE_ATTRIBUTES");
            put("cvc-enumeration-valid", "XSD_ENUMERATION_VALUE_NOT_ALLOWED");
            put("cvc-fractionDigits-valid ", "XSD_TOO_MANY_FRACTION_DIGITS");
            put("cvc-length-valid", "XSD_INCORRECT_LENGTH");
            put("cvc-minLength-valid", "XSD_INCORRECT_LENGTH");
            put("cvc-maxLength-valid", "XSD_INCORRECT_LENGTH");
            put("cvc-maxExclusive-valid", "XSD_VALUE_TOO_LARGE");
            put("cvc-maxInclusive-valid", "XSD_VALUE_TOO_LARGE");
            put("cvc-minExclusive-valid", "XSD_VALUE_TOO_SMALL");
            put("cvc-minInclusive-valid", "XSD_VALUE_TOO_SMALL");
            put("cvc-pattern-valid", "XSD_INCORRECT_FORMAT");
            put("cvc-totalDigits-valid", "XSD_TOO_MANY_DIGITS");
            put("UNKNOWN-CVC-CODE", "XSD_UNKNOWN");
        }
    };

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
        errorReport.error(errorMessage, XsdValidator.class);
        System.out.println("WARNING: " + errorMessage);
    }

    /**
     * Report a SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    public void error(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex.getMessage());
        errorReport.error(errorMessage, XsdValidator.class);
        System.out.println("ERROR: " + errorMessage);
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
        errorReport.error(errorMessage, XsdValidator.class);
        System.out.println("FATAL ERROR: " + errorMessage);
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
                messageArgs[i] = saxErrorMessageTokens[(i * 2) + 1];
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

    public MessageSource getMessageSource() {
        return messageSource;
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
