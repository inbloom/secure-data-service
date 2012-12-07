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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.validation.indexes.DbIndexValidator;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 * Checks if the indexes are present for all the dbs before processing this job.
 * This validator fails if index files are not defined, it is only to add an
 * error message in the log file if the index counts do not match.
 *
 * @author unavani
 *
 */
public class IndexValidator extends SimpleValidatorSpring<Object> {

    private Logger log = LoggerFactory.getLogger(IndexValidator.class);

    @Autowired
    private DbIndexValidator tenantDBIndexValidator;


    @Override
    public boolean isValid(Object object, ErrorReport callback) {

        tenantDBIndexValidator.verifyIndexes();

        return true;
    }

}
