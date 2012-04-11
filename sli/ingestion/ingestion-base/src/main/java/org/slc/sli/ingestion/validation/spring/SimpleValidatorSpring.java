package org.slc.sli.ingestion.validation.spring;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.slc.sli.ingestion.util.spring.MessageSourceHelper;

/**
 * Abstract validator with Spring MessageSource support.
 *
 * @author okrook
 *
 */
public abstract class SimpleValidatorSpring<T> extends org.slc.sli.ingestion.validation.SimpleValidator<T> implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getFailureMessage(String code, Object... args) {
        return MessageSourceHelper.getMessage(messageSource, code, args);
    }

}
