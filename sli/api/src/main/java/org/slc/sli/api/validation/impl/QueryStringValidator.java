package org.slc.sli.api.validation.impl;

import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.validation.strategy.AbstractBlacklistStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 6/14/12
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class QueryStringValidator implements URLValidator {

    @Resource(name = "validationStrategyList")
    private List<AbstractBlacklistStrategy> validationStrategyList;

    @Override
    public boolean validate(URI url) {
        String queryString = url.getQuery();

        if (queryString != null && !queryString.isEmpty()) {
            for (AbstractBlacklistStrategy abstractBlacklistStrategy : validationStrategyList) {
                if (!abstractBlacklistStrategy.isValid("", queryString)) {
                    return false;
                }
            }
        }

        return true;
    }
}
