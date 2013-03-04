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


package org.slc.sli.test.generators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCAttendanceEvent;
import org.slc.sli.test.edfi.entities.AttendanceEventCategoryType;
import org.slc.sli.test.edfi.entities.AttendanceEventType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCSectionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSectionReferenceType;

public class AttendanceEventGenerator {

    private static final boolean INCLUDE_OPTIONAL_DATA = false;

    private static final Random RANDOM = new Random(31);
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static Calendar calendar = new GregorianCalendar(2012, 0, 1);
    
    private static String[] attendance = {"In Attendance","Excused Absence","Unexcused Absence", "Tardy", "Early departure"};

    public static SLCAttendanceEvent generateLowFi(String studentID, String schoolID, String sectionCode) {
        SLCAttendanceEvent ae = new SLCAttendanceEvent();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        ae.setEventDate(DATE_FORMATTER.format(calendar.getTime()));
        ae.setAttendanceEventCategory(getAttendanceEventCategoryTypeMedFi());
        ae.setStudentReference(StudentGenerator.getStudentReferenceType(studentID));
        // TODO this will need to be changed when SLCEducationalOrgReferenceType is used
//        ae.setSchoolReference(SchoolGenerator.getEducationalOrgReferenceType(schoolID));
        SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType edOrgId = new SLCEducationalOrgIdentityType();
        edOrgId.setStateOrganizationId(schoolID);
        edOrgRef.setEducationalOrgIdentity(edOrgId);
        ae.setSchoolReference(edOrgRef);

        if (INCLUDE_OPTIONAL_DATA) {
            ae.setAttendanceEventType(getAttendanceEventType());
            ae.setAttendanceEventReason("My dog ate my homework.");
            ae.setEducationalEnvironment(getEducationalEnvironmentType());

            SLCSectionReferenceType sectionReferenceType = new SLCSectionReferenceType();
            SLCSectionIdentityType sectionIdentityType = new SLCSectionIdentityType();
            SLCEducationalOrgReferenceType slcEdOrgRef = new SLCEducationalOrgReferenceType();
            SLCEducationalOrgIdentityType slcEdOrgId = new SLCEducationalOrgIdentityType();
            slcEdOrgId.setStateOrganizationId(schoolID);
            slcEdOrgRef.setEducationalOrgIdentity(slcEdOrgId);
            sectionIdentityType.setUniqueSectionCode(sectionCode);
            sectionIdentityType.setEducationalOrgReference(slcEdOrgRef);
            sectionReferenceType.setSectionIdentity(sectionIdentityType);
            ae.setSectionReference(sectionReferenceType);
        }

        return ae;
    }

    public static AttendanceEventType getAttendanceEventType() {
        Random random = new Random(31);
        return AttendanceEventType.values()[random.nextInt(AttendanceEventType.values().length)];
    }

    public static AttendanceEventCategoryType getAttendanceEventCategoryType() {
        Random random = new Random(31);
        int index = random.nextInt(AttendanceEventCategoryType.values().length);
        if (index == 1)
            index = index + 1;
        
        return AttendanceEventCategoryType.values()[index];
    }

   // ["In Attendance", "Absence". "Excused Absence","Unexcused Absence", "Tardy", "Early departure"]
   // ["In Attendance", "Excused Absence","Unexcused Absence", "Tardy", "Early departure"]

    
    public static AttendanceEventCategoryType getAttendanceEventCategoryTypeMedFi() {

        Random seededRandom = new Random(31);
        int roll = seededRandom.nextInt(100);
        
        switch (roll) {
        case 1 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);        
        case 2 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 3 : 
            return AttendanceEventCategoryType.fromValue(attendance[1]);
        case 4 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 5 : 
            return AttendanceEventCategoryType.fromValue(attendance[1]);
        case 6:
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 7 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);        
        case 8 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 9 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);        
        case 10 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 11 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);        
        case 12 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 13 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);        
        case 14 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 15 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);
        case 16 :
            return AttendanceEventCategoryType.fromValue(attendance[2]);
        case 17 :
            return AttendanceEventCategoryType.fromValue(attendance[1]);
        case 18:
            return AttendanceEventCategoryType.fromValue(attendance[3]);
        case 19:
            return AttendanceEventCategoryType.fromValue(attendance[4]);
        default : 
            return AttendanceEventCategoryType.fromValue(attendance[0]);
                    
        }
    }
        
    
    public static EducationalEnvironmentType getEducationalEnvironmentType() {
        return EducationalEnvironmentType.values()[RANDOM.nextInt(EducationalEnvironmentType.values().length)];
    }

    public static void resetCalendar() {
        calendar = new GregorianCalendar(2012, 0, 1);
    }

    public static void main(String args[]) throws Exception{
    
        
        AttendanceEventGenerator test = new AttendanceEventGenerator();
        test.getAttendanceEventCategoryTypeMedFi();
        String studentID = "sid";
        String schoolID = "schid";
        String sectionCode = "secode";
        test.generateLowFi(studentID, schoolID, sectionCode);
        test.generateLowFi(studentID, schoolID, sectionCode);
        test.generateLowFi(studentID, schoolID, sectionCode);
        
    }
}


