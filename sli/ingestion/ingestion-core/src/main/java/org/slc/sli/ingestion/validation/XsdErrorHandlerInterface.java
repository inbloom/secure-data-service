package org.slc.sli.ingestion.validation;

import org.xml.sax.ErrorHandler;

/**
 * Forgiving XSD validation error handler.
 *
 * @author Thomas Shewchuk
 *
 */
public interface XsdErrorHandlerInterface extends ErrorHandler {

    public void setIsValid(boolean value);

    public boolean isValid();

}
