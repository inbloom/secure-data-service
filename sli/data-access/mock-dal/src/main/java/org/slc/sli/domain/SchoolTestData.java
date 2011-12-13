package org.slc.sli.domain;

import org.slc.sli.domain.enums.AdministrativeFundingControlType;
import org.slc.sli.domain.enums.CharterStatusType;
import org.slc.sli.domain.enums.MagnetSpecialProgramEmphasisSchoolType;
import org.slc.sli.domain.enums.OperationalStatusType;
import org.slc.sli.domain.enums.SchoolType;
import org.slc.sli.domain.enums.TitleIPartASchoolDesignationType;

/**
 * Test data for school
 * 
 */
public class SchoolTestData {
    public static School buildTestSchool1() {
        School school = new School();
        school.setFullName("Plymounth-Canton High School");
        school.setShortName("Canton");
        school.setCharterStatus(CharterStatusType.NOT_A_CHARTER_SCHOOL);
        school.setAdministrativeFundingControl(AdministrativeFundingControlType.PUBLIC_SCHOOL);
        school.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.NO_STUDENTS_PARTICIPATE);
        school.setOperationalStatus(OperationalStatusType.ACTIVE);
        school.setSchoolType(SchoolType.REGULAR);
        school.setStateOrganizationId("MI-ID-PCEP-001");
        school.setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType.NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL);
        school.setWebSite("http://pcep.pccs.k12.mi.us/");
        return school;
    }
    
    public static School buildTestSchool2() {
        School school = new School();
        school.setFullName("Plymounth-Salem High School");
        school.setShortName("Salem");
        school.setCharterStatus(CharterStatusType.NOT_A_CHARTER_SCHOOL);
        school.setAdministrativeFundingControl(AdministrativeFundingControlType.PUBLIC_SCHOOL);
        school.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.NO_STUDENTS_PARTICIPATE);
        school.setOperationalStatus(OperationalStatusType.ACTIVE);
        school.setSchoolType(SchoolType.REGULAR);
        school.setStateOrganizationId("MI-ID-PCEP-001");
        school.setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType.NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL);
        school.setWebSite("http://pcep.pccs.k12.mi.us/");
        return school;
    }
}
