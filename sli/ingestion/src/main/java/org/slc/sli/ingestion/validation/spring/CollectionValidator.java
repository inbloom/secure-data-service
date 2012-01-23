package org.slc.sli.ingestion.validation.spring;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * Abstract validator with Spring MessageSource support.
 *
 * @author okrook
 *
 */
public abstract class CollectionValidator<T> extends org.slc.sli.ingestion.validation.CollectionValidator<T> implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getFailureMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
    }

}
