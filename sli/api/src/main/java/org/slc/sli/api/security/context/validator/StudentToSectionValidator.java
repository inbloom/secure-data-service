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
package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

/**
 * Validator for student to section
 *
 * @author nbrown
 */
@Component
public class StudentToSectionValidator extends BasicValidator {

    public StudentToSectionValidator() {
        super(false, Arrays.asList(EntityNames.STUDENT, EntityNames.PARENT), EntityNames.SECTION);
    }

    @Override
    protected boolean doValidate(Set<String> ids, String entityType) {

        Set<String> sections = new HashSet<String>();
        for (Entity user : SecurityUtil.getSLIPrincipal().getOwnedStudentEntities()) {

            List<Map<String, Object>> sectionData = user.getDenormalizedData().get("section");

            if (null == sectionData) {
                // If there is no denormalized sections
                continue;
            }
            // stupid java with no first class functions, I could do this in one line in any decent
            // language...
            for (Map<String, Object> section : sectionData) {
                sections.add((String) section.get("_id"));
            }
        }

        return sections.containsAll(ids);
    }

}
