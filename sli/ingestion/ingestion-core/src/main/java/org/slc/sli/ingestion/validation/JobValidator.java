/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 * Validator for Job interface.
 *
 * @author dduran
 *
 */
@Component
public class JobValidator extends SimpleValidatorSpring<Job> {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public boolean isValid(Job job, ErrorReport callback) {
        while (!batchJobDAO.attemptTentantLockForJob(job.getTenantId(), job.getId())) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

}
