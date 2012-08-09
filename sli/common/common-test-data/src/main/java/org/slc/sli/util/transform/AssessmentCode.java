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


package org.slc.sli.util.transform;

/**
 * 
 * @author dwilliams
 *
 */
public class AssessmentCode implements MongoDataEmitter {
    private String idSystem = null;
    private String id = null;
    
    public AssessmentCode(String id) {
        this("School", id);
    }
    
    public AssessmentCode(String system, String id) {
        this.idSystem = system;
        this.id = id;
    }
    
    @Override
    public String emit() {
        // "assessmentIdentificationCode":[{"identificationSystem":"School","id":"01234B"}]
        StringBuffer answer = new StringBuffer();
        answer.append("{\"identificationSystem\":\"").append(idSystem).append("\",\"id\":\"").append(id).append("\"}");
        return answer.toString();
    }
}
