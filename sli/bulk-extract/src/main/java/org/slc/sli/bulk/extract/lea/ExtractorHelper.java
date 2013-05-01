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

package org.slc.sli.bulk.extract.lea;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

public class ExtractorHelper {
    
    private DateHelper dateHelper;

    public Set<String> fetchCurrentSchoolsFromStudent(Entity student) {
        if (dateHelper == null) {
            dateHelper = new DateHelper();
        }
        Set<String> studentSchools = new HashSet<String>();
        Map<String, List<Map<String, Object>>> data = student.getDenormalizedData();
        if (!data.containsKey("schools")) {
            return studentSchools;
        }
        List<Map<String, Object>> schools = data.get("schools");
        for (Map<String, Object> school : schools) {
            if (dateHelper.isFieldExpired(school, "exitWithdrawDate")) {
                continue;
            }
            if (school.containsKey("edOrgs")) {
                List<String> edorgs = (List<String>) school.get("edOrgs");
                studentSchools.addAll(edorgs);
            }
        }
        return studentSchools;
    }
    
    public void setDateHelper(DateHelper helper) {
        this.dateHelper = helper;
    }
}
