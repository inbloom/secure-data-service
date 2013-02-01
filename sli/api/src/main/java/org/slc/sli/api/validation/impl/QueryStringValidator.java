/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
            //removing valid characters
            queryString = queryString.replaceAll(">", "").replaceAll("<", "");

            for (AbstractBlacklistStrategy abstractBlacklistStrategy : validationStrategyList) {
                if (!abstractBlacklistStrategy.isValid("", queryString)) {
                    return false;
                }
            }
        }

        return true;
    }
}
