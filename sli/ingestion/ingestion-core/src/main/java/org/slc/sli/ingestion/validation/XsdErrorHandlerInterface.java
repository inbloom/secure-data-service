package org.slc.sli.ingestion.validation;

import org.xml.sax.ErrorHandler;

public interface XsdErrorHandlerInterface extends ErrorHandler {

    public void setIsValid(boolean value);

    public boolean isValid();

}
