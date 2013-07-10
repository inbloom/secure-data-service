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

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * User: dkornishev
 */
@Component
public class TransitiveStudentToParentValidator extends BasicValidator {

    public TransitiveStudentToParentValidator() {
        super(true, Arrays.asList(EntityNames.STUDENT), EntityNames.PARENT);
    }

    @Override
    protected boolean doValidate(Set<String> ids, String entityType) {

        for (Entity me : SecurityUtil.getSLIPrincipal().getOwnedStudentEntities()) {
            List<Entity> spas = me.getEmbeddedData().get("studentParentAssociation");

            if (spas != null) {
                for (Entity spa : spas) {
                    String parentId = (String) spa.getBody().get("parentId");
                    ids.remove(parentId);
                }
            }
        }
        return ids.isEmpty();
    }

}
