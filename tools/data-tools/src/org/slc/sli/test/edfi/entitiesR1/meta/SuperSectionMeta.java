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


package org.slc.sli.test.edfi.entitiesR1.meta;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.test.edfi.entities.meta.CourseMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entitiesR1.Teacher;

public class SuperSectionMeta extends SectionMeta {
	



//	public  final String id;
   // public final SectionMeta sectionMeta;
  //  public final List<String> teacherIds;
  //  public final List<StudentMeta> studenMetas;
//    public final StudentMeta studenMeta;
  //  public final List<String> assessmentId;
    
//    public final String schoolId;
//    public final String courseId;
//    public final String sessionId;
//    public final String programId;
    //public final List<String> sectionIds;
   public final List<String> studentIds;
   public final List<String> teacherIds;
    

//   public final String simpleId;
    
	public SuperSectionMeta(String id, SchoolMeta schoolMeta, CourseMeta courseMeta, SessionMeta sessionMeta,
			ProgramMeta programMeta) {
		
		super(id, schoolMeta, courseMeta, sessionMeta, programMeta);
		
		
		 this.studentIds = new ArrayList<String>();
		 this.teacherIds = new ArrayList<String>();

		// TODO Auto-generated constructor stub
	}

//    public SuperSectionMeta(String id, SchoolMeta schoolMeta, CourseMeta courseMeta, SessionMeta sessionMeta,
//            ProgramMeta programMeta) {
//    	
//    	
//    }
    
    
//    public SuperSectionMeta(String id, SectionMeta sectionMeta)  {
//
////        String schoolIdNoAlpha = schoolMeta.id.replaceAll("[a-z]", "");
////        String sessionIdNoAlpha = courseMeta.simpleId.replaceAll("[a-z]", "");
////        String courseIdNoAlpha = sessionMeta.simpleId.replaceAll("[a-z]", "");
//
//        //this.id = schoolIdNoAlpha + MetaRelations.ID_DELIMITER + sessionIdNoAlpha + MetaRelations.ID_DELIMITER + courseIdNoAlpha + MetaRelations.ID_DELIMITER + id;
//
//       this.id = sectionMeta.id;
//       this.sectionIds = new ArrayList<String>();
//       this.sectionIds.add(sectionMeta.id);
//       
//       this.studentIds = new ArrayList<String>();
//
//       this.simpleId = id;
////        this.schoolId = schoolMeta.id;
////        this.courseId = courseMeta.id;
////        this.sessionId = sessionMeta.id;
////        this.programId = programMeta == null ? null : programMeta.id;
//       // this.studentId = studentMeta.id;
//
//      //  this.simpleId = id;
//    }

 //  @Override
//    public String toString() {
//        return "SectionMeta [id=" + id + ", schoolId=" + schoolId + ", courseId=" + courseId + ", sessionId="
//                + sessionId + ", programId=" + programId + "]";
//    }

}
