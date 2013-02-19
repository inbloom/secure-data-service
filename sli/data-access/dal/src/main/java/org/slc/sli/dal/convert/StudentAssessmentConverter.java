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

import org.slc.sli.domain.Entity;

/**
 * studentAssessment converter that transform studentAssessment superdoc to sli studentAssessment
 * schema
 * 
 * @author Dong Liu dliu@wgen.net
 */

public class StudentAssessmentConverter implements SuperdocConverter {
    
    @Override
    public void subdocToBodyField(Entity entity) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void subdocToBodyField(Iterable<Entity> entities) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void bodyFieldToSubdoc(Iterable<Entity> entities) {
        // TODO Auto-generated method stub
        
    }
    
}
