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

package org.slc.sli.ingestion.validation;

import java.util.List;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;

/**
 * Abstract validator
 *
 * @author okrook
 *
 * @param <T>
 */
public class ComplexValidator<T> implements Validator<T> {
    private List<? extends Validator<T>> validators;

    public List<? extends Validator<T>> getValidators() {
        return validators;
    }

    public void setValidators(List<? extends Validator<T>> validators) {
        this.validators = validators;
    }

    @Override
    public boolean isValid(T object, AbstractMessageReport report, ReportStats reportStats, Source source) {
        for (Validator<T> validator : validators) {
            if (!validator.isValid(object, report, reportStats, source)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getStageName() {
        return "Complex Validation";
    }

}
