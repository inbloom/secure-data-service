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

package org.slc.sli.sif.domain.converter;

import openadk.library.common.AddressType;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.PhoneNumberType;
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolFocusType;
import openadk.library.student.SchoolLevelType;

/**
 * An utility to convert from SIF values to SLI values for mapped SchoolInfo fields.
 *
 * @author slee
 *
 */
public class SchoolConverter
{
    public static String toSliAddressType(String addressType) {
        if ( AddressType.MAILING.getValue().equals(addressType)) {
            return "Mailing";
        }
        if ( AddressType.PHYSICAL_LOCATION.getValue().equals(addressType)) {
            return "Physical";
        }
        if ( AddressType.SHIPPING.getValue().equals(addressType)) {
            return "Billing";
        }
        if ( AddressType.OTHER_HOME.getValue().equals(addressType)) {
            return "Home";
        }
        if ( AddressType.OTHER_ORGANIZATION.getValue().equals(addressType)) {
            return "Other";
        }
        return "Other";
    }

    public static String toSliInstitutionTelephoneNumberType(String phoneNumberType) {
        if ( PhoneNumberType.ANSWERING_SERVICE.getValue().equals(phoneNumberType)) {
            return "Administrative";
        }
        if ( PhoneNumberType.FAX.getValue().equals(phoneNumberType)) {
            return "Fax";
        }
        if ( PhoneNumberType.PRIMARY.getValue().equals(phoneNumberType)) {
            return "Main";
        }
        if ( PhoneNumberType.VOICE_MAIL.getValue().equals(phoneNumberType)) {
            return "Attendance";
        }
        return "Other";
    }

    public static String toSliSchoolType(String schoolFocusType) {
        if ( SchoolFocusType.ALTERNATIVE.getValue().equals(schoolFocusType)) {
            return "Alternative";
        }
        if ( SchoolFocusType.CHARTER.getValue().equals(schoolFocusType)) {
            return "JJAEP";
        }
        if ( SchoolFocusType.MAGNET.getValue().equals(schoolFocusType)) {
            return "DAEP";
        }
        if ( SchoolFocusType.REGULAR.getValue().equals(schoolFocusType)) {
            return "Regular";
        }
        if ( SchoolFocusType.SPECIALED.getValue().equals(schoolFocusType)) {
            return "Special Education";
        }
        if ( SchoolFocusType.VOCATIONAL.getValue().equals(schoolFocusType)) {
            return "Vocational";
        }
        return "Not Supported";
    }

    public static String toSliOperationalStatus(String operationalStatus) {
        if ( OperationalStatus.SCHOOL_CLOSED.getValue().equals(operationalStatus)) {
            return "Closed";
        }
        if ( OperationalStatus.SCHOOL_FUTURE.getValue().equals(operationalStatus)) {
            return "Future";
        }
        if ( OperationalStatus.SCHOOL_INACTIVE.getValue().equals(operationalStatus)) {
            return "Inactive";
        }
        if ( OperationalStatus.SCHOOL_NEW.getValue().equals(operationalStatus)) {
            return "New";
        }
        if ( OperationalStatus.SCHOOL_OPEN.getValue().equals(operationalStatus)) {
            return "Reopened";
        }
        if ( OperationalStatus.CHANGED_BOUNDARY.getValue().equals(operationalStatus)) {
            return "Changed Agency";
        }
        return "Not Supported";
    }

    public static String toSliSchoolCategory(String schoolType) {
        if ( SchoolLevelType._0031_1304_ELEMENTARY.getValue().equals(schoolType)) {
            return "Elementary School";
        }
        if ( SchoolLevelType._0031_0013_ADULT.getValue().equals(schoolType)) {
            return "Adult School";
        }
        if ( SchoolLevelType._0031_0789_PRE_KINDERGARTEN.getValue().equals(schoolType)) {
            return "Preschool/early childhood";
        }
        if ( SchoolLevelType._0031_1302_ALL_LEVELS.getValue().equals(schoolType)) {
            return "Ungraded";
        }
        if ( SchoolLevelType._0031_1981_PRESCHOOL.getValue().equals(schoolType)) {
            return "Infant/toddler School";
        }
        if ( SchoolLevelType._0031_2397_PRIMARY.getValue().equals(schoolType)) {
            return "Primary School";
        }
        if ( SchoolLevelType._0031_2399_INTERMEDIATE.getValue().equals(schoolType)) {
            return "Intermediate School";
        }
        if ( SchoolLevelType._0031_2400_MIDDLE.getValue().equals(schoolType)) {
            return "Middle School";
        }
        if ( SchoolLevelType._0031_2401_JUNIOR.getValue().equals(schoolType)) {
            return "Junior High School";
        }
        if ( SchoolLevelType._0031_2402_HIGH_SCHOOL.getValue().equals(schoolType)) {
            return "High School";
        }
        if ( SchoolLevelType._0031_2403_SECONDARY.getValue().equals(schoolType)) {
            return "SecondarySchool";
        }
        return "Ungraded";
    }

    /**
     *  Method to convert SIF grade value to SLI grade values
     */
    public static String toSliGrade(String gradeCode) {
        if ( GradeLevelCode._01.getValue().equals(gradeCode)) {
            return "First grade";
        }
        if ( GradeLevelCode._02.getValue().equals(gradeCode)) {
            return "Second grade";
        }
        if ( GradeLevelCode._03.getValue().equals(gradeCode)) {
            return "Third grade";
        }
        if ( GradeLevelCode._04.getValue().equals(gradeCode)) {
            return "Fourth grade";
        }
        if ( GradeLevelCode._05.getValue().equals(gradeCode)) {
            return "Fifth grade";
        }
        if ( GradeLevelCode._06.getValue().equals(gradeCode)) {
            return "Sixth grade";
        }
        if ( GradeLevelCode._07.getValue().equals(gradeCode)) {
            return "Seventh grade";
        }
        if ( GradeLevelCode._08.getValue().equals(gradeCode)) {
            return "Eighth grade";
        }
        if ( GradeLevelCode._09.getValue().equals(gradeCode)) {
            return "Ninth grade";
        }
        if ( GradeLevelCode._10.getValue().equals(gradeCode)) {
            return "Tenth grade";
        }
        if ( GradeLevelCode._11.getValue().equals(gradeCode)) {
            return "Eleventh grade";
        }
        if ( GradeLevelCode._12.getValue().equals(gradeCode)) {
            return "Twelfth grade";
        }
        if ( GradeLevelCode.KG.getValue().equals(gradeCode)) {
            return "Kindergarten";
        }
        if ( GradeLevelCode.UN.getValue().equals(gradeCode)) {
            return "Ungraded";
        }
        if ( GradeLevelCode.PG.getValue().equals(gradeCode)) {
            return "Preschool/Prekindergarten";
        }
        if ( GradeLevelCode.PK.getValue().equals(gradeCode)) {
            return "Adult Education";
        }
        if ( GradeLevelCode.OTHER.getValue().equals(gradeCode)) {
            return "Other";
        }
        if ( GradeLevelCode.UNKNOWN.getValue().equals(gradeCode)) {
            return "Not Available";
        }
        return "Not Available";
    }
}
