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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class AdminValidator implements IContextValidator {
    
    @Override
    public boolean canValidate(String entityType, boolean through) {
        String userType = SecurityUtil.getSLIPrincipal().getEntity().getType();
        if (userType.equals("user")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (entityType.equals(EntityNames.EDUCATION_ORGANIZATION)) {
            return true;
        }
        return false;
    }
    
    //TODO: implement it
    public Set<String> getValid(String entityType, Set<String> ids) {
    	 if (entityType.equals(EntityNames.EDUCATION_ORGANIZATION)) {
    		 return ids;
    	 }
    	 
    	 return Collections.emptySet();
    }
}
