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


package org.slc.sli.test.edfi.entitiesR1.meta;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;

public class SuperSectionMeta extends SectionMeta {
    

   public final List<String> studentIds;
   public final List<String> teacherIds;
    

    public SuperSectionMeta(String id, SchoolMeta schoolMeta, CourseOfferingMeta courseOfferingMeta, SessionMeta sessionMeta,
            ProgramMeta programMeta) {
        
        super(id, schoolMeta, courseOfferingMeta, sessionMeta, programMeta);
        
        
         this.studentIds = new ArrayList<String>();
         this.teacherIds = new ArrayList<String>();

        // TODO Auto-generated constructor stub
    }


    
    


 //  @Override
//    public String toString() {
//        return "SectionMeta [id=" + id + ", schoolId=" + schoolId + ", courseId=" + courseId + ", sessionId="
//                + sessionId + ", programId=" + programId + "]";
//    }

}
