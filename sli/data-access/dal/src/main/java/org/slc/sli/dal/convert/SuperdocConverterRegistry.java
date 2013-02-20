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

package org.slc.sli.dal.convert;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.api.constants.EntityNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * superdoc converters registry that map each superdoc type to its corresponding converter
 * 
 * @author Dong Liu dliu@wgen.net
 */

@Component
public class SuperdocConverterRegistry {
    private Map<String, SuperdocConverter> converters = new HashMap<String, SuperdocConverter>();

    @Autowired
    AssessmentConverter assessmentConverter;
    
    @Autowired
    StudentAssessmentConverter studentAssessmentConverter;
    
    @PostConstruct
    public void init() {
        converters.put(EntityNames.ASSESSMENT, assessmentConverter);
        converters.put(EntityNames.STUDENT_ASSESSMENT, studentAssessmentConverter);
    }

    public SuperdocConverter getConverter(String entityType) {
        return converters.get(entityType);
    }

}
