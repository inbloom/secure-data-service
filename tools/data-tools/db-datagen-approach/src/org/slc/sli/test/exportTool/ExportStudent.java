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


package org.slc.sli.test.exportTool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.Address;
import org.slc.sli.test.edfi.entities.AddressType;
import org.slc.sli.test.edfi.entities.BirthData;
import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Student;

public class ExportStudent {
    private ObjectFactory factory = new ObjectFactory();
    private InterchangeStudent interchangeStudent = factory
            .createInterchangeStudent();

    private ResultSet studentResultSet;
    public String studentQuery = new StringBuilder()
            .append("SELECT StudentUSI \n")
            .append("    ,PersonalTitlePrefixTypeId \n")
            .append("    ,FirstName \n").append("    ,MiddleName \n")
            .append("    ,LastSurname \n")
            .append("    ,GenerationCodeSuffixTypeId \n")
            .append("    ,MaidenName \n")
            .append("    ,PersonalInformationVerificationTypeId \n")
            .append("    ,st.CodeValue \n").append("    ,BirthDate \n")
            .append("    ,CityOfBirth \n")
            .append("    ,StateOfBirthAbbreviationTypeId \n")
            .append("    ,CountryOfBirthCodeTypeId \n")
            .append("    ,DateEnteredUS \n")
            .append("    ,MultipleBirthStatus \n")
            .append("    ,ProfileThumbnail \n")
            .append("    ,HispanicLatinoEthnicity \n")
            .append("    ,OldEthnicityTypeId \n")
            .append("    ,EconomicDisadvantaged \n")
            .append("    ,SchoolFoodServicesEligibilityTypeId \n")
            .append("    ,LimitedEnglishProficiencyTypeId \n")
            .append("    ,DisplacementStatusType \n")
            .append("    ,LoginId \n")
            .append("FROM edfi.edfi.Student s, edfi.edfi.SexType st \n")
            .append("WHERE s.SexTypeId = st.SexTypeId \n")
            .append("ORDER BY StudentUSI \n").toString();

    private ResultSet studentAddressResultSet;
    public String studentAddressResultSetQuery = new StringBuilder()
            .append("SELECT StudentUSI \n")
            .append("      ,at.CodeValue as AddressType \n")
            .append("      ,StreetNumberName \n")
            .append("      ,ApartmentRoomSuiteNumber \n")
            .append("      ,BuildingSiteNumber \n")
            .append("      ,City \n")
            .append("      ,sat.CodeValue as StateAbbreviation \n")
            .append("      ,PostalCode \n")
            .append("      ,NameOfCounty \n")
            .append("      ,CountyFIPSCode \n")
            .append("      ,CountryCodeTypeId \n")
            .append("      ,Latitude \n")
            .append("      ,Longitude \n")
            .append("      ,BeginDate \n")
            .append("      ,EndDate \n")
            .append("FROM edfi.edfi.StudentAddress sa, edfi.edfi.StateAbbreviationType sat, edfi.edfi.AddressType at \n")
            .append("WHERE sa.StateAbbreviationTypeId = sat.StateAbbreviationTypeId \n")
            .append("    AND sa.AddressTypeId = at.AddressTypeId \n")
            .append("ORDER BY StudentUSI \n").toString();

    /**
     * @param args
     */
    public static void main(String[] args) {
        ExportStudent es = new ExportStudent();
        System.out.println(es.studentQuery);
        System.out.println(es.studentAddressResultSetQuery);
        es.getDataSet();
        es.getStudents();
        es.generateXML();
    }

    private void getDataSet() {
        Connection conn = Utility.getConnection();
        studentResultSet = Utility.getResultSet(conn, this.studentQuery);
        studentAddressResultSet = Utility.getResultSet(conn, this.studentAddressResultSetQuery);
    }

    private Student getStudent(ResultSet srs) {
        Student student = factory.createStudent();
        try {
            String studentUniqueStateId = srs.getString("StudentUSI");
            student.setStudentUniqueStateId(studentUniqueStateId);

            Name studentName = factory.createName();
            studentName.setFirstName(this.studentResultSet
                    .getString("FirstName"));
            studentName.setLastSurname(this.studentResultSet
                    .getString("LastSurname"));
            student.setName(studentName);

            student.setSex(SexType.fromValue(this.studentResultSet
                    .getString("CodeValue")));

            BirthData birthData = factory.createBirthData();
            birthData.setBirthDate("2011-01-05");
            student.setBirthData(birthData);

            if (studentAddressResultSet != null) {
                try {
                    String studentUSI = this.studentAddressResultSet
                            .getString("StudentUSI");
                    while (studentUSI.compareTo(studentUniqueStateId) == 0) {
                        Address studentAddress = factory.createAddress();
                        studentAddress
                                .setStreetNumberName(this.studentAddressResultSet
                                        .getString("StreetNumberName"));
                        studentAddress.setCity(this.studentAddressResultSet
                                .getString("City"));
                        studentAddress.setStateAbbreviation(StateAbbreviationType
                                .fromValue(this.studentAddressResultSet
                                        .getString("StateAbbreviation")));
                        studentAddress.setPostalCode(this.studentAddressResultSet
                                .getString("PostalCode"));
                        studentAddress.setAddressType(AddressType
                                .fromValue(this.studentAddressResultSet
                                        .getString("AddressType")));

                        student.getAddress().add(studentAddress);

                        if (this.studentAddressResultSet.next())
                            studentUSI = this.studentAddressResultSet
                                    .getString("StudentUSI");
                        else
                            studentUSI = "";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

//            student.setProfileThumbnail(this.studentResultSet.getString("ProfileThumbnail"));
            student.setProfileThumbnail(null);

            student.setHispanicLatinoEthnicity(this.studentResultSet
                    .getBoolean("HispanicLatinoEthnicity"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    private InterchangeStudent getStudents() {
        if (studentResultSet != null) {
            try {
                do {
                    interchangeStudent.getStudent().add(
                            this.getStudent(this.studentResultSet));
                } while (this.studentResultSet.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return interchangeStudent;
    }

    private void generateXML() {
        QName qName = new QName("InterchangeStudent");
        JAXBContext context;
        try {
            context = JAXBContext.newInstance("org.slc.sli.test.edfi.entities");
            JAXBElement<InterchangeStudent> element = new JAXBElement<InterchangeStudent>(
                    qName, InterchangeStudent.class, interchangeStudent);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.marshal(element, System.out);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
