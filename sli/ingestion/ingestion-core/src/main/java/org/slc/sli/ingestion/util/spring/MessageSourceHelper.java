package org.slc.sli.ingestion.util.spring;

import org.springframework.context.MessageSource;

/**
 * Helper for Spring's MessageSource.
 *
 * @author okrook
 *
 */
public final class MessageSourceHelper {

    public static final String getMessage(MessageSource messageSource, String code, Object... args) {
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
    }

}
