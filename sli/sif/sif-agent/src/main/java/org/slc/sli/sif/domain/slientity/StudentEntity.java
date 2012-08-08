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

package org.slc.sli.sif.domain.slientity;

import java.util.List;

/**
 * Represents the student in SLI datamodel
 *
 * @author slee
 *
 */
public class StudentEntity extends SliEntity
{
    // mandatory fields
    String studentUniqueStateId;
    String name;
    String entryDate;
    String entryGradeLevel;

    // optional fields
    String schoolYear;
    String entryType;
    boolean repeatGradeIndicator;
    String classOf;
    boolean schoolChoiceTransfer;
    String exitWithdrawDate;
    String exitWithdrawType;
    List<String> educationalPlans;

    
    
    
    @Override
    public String entityType()
    {
        return "student";
    }

}
