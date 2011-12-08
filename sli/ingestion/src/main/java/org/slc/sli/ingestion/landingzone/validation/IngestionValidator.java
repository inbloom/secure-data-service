package org.slc.sli.ingestion.landingzone.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * Generic Ingestion Validator.
 *
 * @author okrook
 *
 */
public abstract class IngestionValidator<T> implements MessageSourceAware {

    private MessageSource messageSource;

    public abstract void validate(T item) throws IngestionException;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getFailureMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
    }
}
