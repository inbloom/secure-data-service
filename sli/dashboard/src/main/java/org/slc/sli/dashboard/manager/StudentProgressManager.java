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


package org.slc.sli.dashboard.manager;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;

/**
 * Gathers and provides information needed for the student progress view
 * @author jstokes
 */
@Manager.EntityMappingManager
public interface StudentProgressManager extends Manager {

    @EntityMapping("transcriptHistory")
    GenericEntity getTranscript(String token, Object studentIdObj, Config.Data config);

    @SuppressWarnings("unchecked")
    Map<String, List<GenericEntity>> getStudentHistoricalAssessments(String token, List<String> studentIds,
                                                                     String selectedCourse, String selectedSection);

    SortedSet<String> getSchoolYears(String token, Map<String,
            List<GenericEntity>> historicalData);

    Map<String, Map<String, GenericEntity>> getCurrentProgressForStudents(String token, List<String> studentIds,
                                                                          String selectedSection);

    SortedSet<GenericEntity> retrieveSortedGradebookEntryList(Map<String, Map<String, GenericEntity>> gradebookEntryData);
}
