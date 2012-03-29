package org.slc.sli.ingestion.landingzone.validation;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.slc.sli.ingestion.Fault;

/**
 * Generic Ingestion Validator.
 *
 * @author okrook
 *
 */
public abstract class IngestionValidator<T> implements MessageSourceAware {

    private MessageSource messageSource;

    public abstract boolean isValid(T item, List<Fault> faults);

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getFailureMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
    }
}
