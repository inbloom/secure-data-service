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


package org.slc.sli.sample.transform;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.slc.sli.sample.entitiesR1.Address;
import org.slc.sli.sample.entitiesR1.AddressType;
import org.slc.sli.sample.entitiesR1.BirthData;
import org.slc.sli.sample.entitiesR1.GenerationCodeSuffixType;
import org.slc.sli.sample.entitiesR1.InterchangeStudentParent;
import org.slc.sli.sample.entitiesR1.LanguageItemType;
import org.slc.sli.sample.entitiesR1.LanguagesType;
import org.slc.sli.sample.entitiesR1.Name;
import org.slc.sli.sample.entitiesR1.Parent;
import org.slc.sli.sample.entitiesR1.ParentIdentityType;
import org.slc.sli.sample.entitiesR1.ParentReferenceType;
import org.slc.sli.sample.entitiesR1.PersonalInformationVerificationType;
import org.slc.sli.sample.entitiesR1.PersonalTitlePrefixType;
import org.slc.sli.sample.entitiesR1.RaceItemType;
import org.slc.sli.sample.entitiesR1.RaceType;
import org.slc.sli.sample.entitiesR1.RelationType;
import org.slc.sli.sample.entitiesR1.SexType;
import org.slc.sli.sample.entitiesR1.StateAbbreviationType;
import org.slc.sli.sample.entitiesR1.Student;
import org.slc.sli.sample.entitiesR1.StudentIdentityType;
import org.slc.sli.sample.entitiesR1.StudentParentAssociation;
import org.slc.sli.sample.entitiesR1.StudentReferenceType;

/**
 *
 * This class converts CSV files into an Ed-Fi xml file.
 * In this sample we have the following assumption for csv files:
 * 1. The records in Student.csv, StudentAddress.csv and StudentLanguage.csv
 *    are sorted by StudentUSI in ascending order.
 * 2. The references (e.g. StudentUSI and ParentUSI) in StudentParentAssociation.csv
 *    are correct and both exit in Student.csv and Parent.csv. This sample code does
 *    not check reference integrity check across csv files.
 *
 */
public class CSV2XMLTransformer {
    // CSV readers for student related files
    private CSVReader studentReader;
    private CSVReader studentAddressReader;
    private CSVReader studentLanguageReader;

    // CSV reader for parent related files
    private CSVReader parentReader;

    // CSV reader for studentParentAssociation files
    private CSVReader studentParentAssociationReader;

    // input csv files
    private static final String studentFile = "data/Student.csv";
    private static final String studentAddressFile = "data/StudentAddress.csv";
    private static final String studentLanguagesFile = "data/StudentLanguage.csv";
    private static final String parentFile = "data/Parent.csv";
    private static final String studentParentAssociationFile = "data/StudentParentAssociation.csv";

    // output Ed-Fi xml file
    private static final String interchangeStudentParentFile = "data/InterchangeStudentParent.xml";
    private static final String outputPath = "data/";

    /**
     * main method
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CSV2XMLTransformer transformer = new CSV2XMLTransformer();
        transformer.loadData();

        PrintStream ps = new PrintStream(new File(interchangeStudentParentFile));
        transformer.printInterchangeStudentParent(ps);

        SchemaValidator.check(outputPath);
    }

    /**
     * open csv files and create CSV reader for each file
     * and load the first record for each reader
     *
     * @throws IOException
     */
    private void loadData() throws IOException {
        // load student data
        studentReader = new CSVReader(studentFile);
        studentAddressReader = new CSVReader(studentAddressFile);
        studentLanguageReader = new CSVReader(studentLanguagesFile);

        // load parent data
        parentReader = new CSVReader(parentFile);

        // load studentParentAssociation data
        studentParentAssociationReader = new CSVReader(studentParentAssociationFile);
    }

    /**
     * Iterate through Student, Parent, and studentParentAssociation records in the CSV files,
     * converts them into JAXB java objects, and then marshals them into SLI-EdFi xml file.
     *
     * @param ps
     * @throws JAXBException
     */
    private void printInterchangeStudentParent(PrintStream ps) throws JAXBException {
        int studentCounter = 0;
        int parentCounter = 0;
        int studentParentAssociationCounter = 0;

        Marshaller marshaller = getMarshaller();

        InterchangeStudentParent interchangeStudentParent = new InterchangeStudentParent();
        List<Object> list = interchangeStudentParent.getStudentOrParentOrStudentParentAssociation();

        // process student
        while (studentReader.getCurrentRecord() != null) {
            list.add(this.getStudent());
            studentReader.getNextRecord();
            studentCounter++;
        }

        // process parent
        while (parentReader.getCurrentRecord() != null) {
            list.add(this.getParent());
            parentReader.getNextRecord();
            parentCounter++;
        }

        // process studentParentAssociation
        while (studentParentAssociationReader.getCurrentRecord() != null) {
            list.add(this.getStudentParentAssociation());
            studentParentAssociationReader.getNextRecord();
            studentParentAssociationCounter++;
        }

        marshaller.marshal(interchangeStudentParent, ps);

        System.out.println("Total " + studentCounter + " students are exported.");
        System.out.println("Total " + parentCounter + " parents are exported.");
        System.out.println("Total " + studentParentAssociationCounter
                + " student-parent-associations are exported.");
        System.out.println("Total " + ( studentCounter + parentCounter + studentParentAssociationCounter)
                + " entities are exported.");
    }

    private Marshaller getMarshaller() throws JAXBException, PropertyException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentParent.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        return marshaller;
    }

    /**
     * generate student jaxb object from csv records
     *
     * @return one jaxb student object
     */
    private Student getStudent() {
        Map<String, String> studentRecord = studentReader.getCurrentRecord();

        Student student = new Student();

        // set id
        String studentId = studentRecord.get("StudentUSI");
        student.setStudentUniqueStateId(studentId);
        student.setId("STUD_" + studentId);

        // set name
        student.setName(this.getName(studentRecord));

        // set sex
        student.setSex(SexType.fromValue(studentRecord.get("Sex")));

        // set birthData
        BirthData birthData = new BirthData();
        birthData.setBirthDate(CSVReader.getDate(studentRecord.get("BirthDate")));
        student.setBirthData(birthData);

        // set ProfileThumbnail
        String profile = studentRecord.get("ProfileThumbnail");
        if (!profile.isEmpty()) {
            student.setProfileThumbnail(profile);
        }

        // set HispanicLatinoEthnicity
        student.setHispanicLatinoEthnicity(studentRecord.get("HispanicLatinoEthnicity") == "1" ? true : false);

        // set raceType
        String racial = studentRecord.get("RacialCategory");
        if (!racial.isEmpty()) {
            RaceType raceType = new RaceType();
            raceType.getRacialCategory().add(RaceItemType.fromValue(racial));
            student.setRace(raceType);
        }

        // set addresses
        addStudentAddresses(student);

        // set languages
        addStudentLanguages(student);

        return student;
    }

    /**
     * Iterate through studentAddress csv file to add all addresses for a specific student
     *
     * @param student
     */
    private void addStudentAddresses(Student student) {
        String studentId = student.getStudentUniqueStateId();

        while (studentAddressReader.getCurrentRecord() != null) {
            Map<String, String> studentAddressRecord = studentAddressReader.getCurrentRecord();

            String id = studentAddressRecord.get("StudentUSI");
            if (id.compareTo(studentId) > 0) {
                // if the studentUSI of the address record is larger than studentUSI of the student
                // which means the current address record belongs to next student, not the current one.
                break;
            } else if (id.equals(studentId)) {
                student.getAddress().add(this.getAddress(studentAddressRecord));
            }

            studentAddressReader.getNextRecord();
        }
    }

    /**
     * Iterate through studentLanguage csv file to add all languages for a specific student
     *
     * @param student
     */
    private void addStudentLanguages(Student student) {
        String studentId = student.getStudentUniqueStateId();

        LanguagesType languages = new LanguagesType();
        while (studentLanguageReader.getCurrentRecord() != null) {
            Map<String, String> studentLanguageRecord = studentLanguageReader.getCurrentRecord();

            String id = studentLanguageRecord.get("StudentUSI");
            if (id.compareTo(studentId) > 0) {
                // if the studentUSI of the language record is larger than studentUSI of the student
                // which means the current language record belongs to next student, not the current one.
                break;
            } else if (id.equals(studentId)) {
                String ls = studentLanguageRecord.get("Language");
                if (!ls.isEmpty()) {
                    LanguageItemType language = LanguageItemType.fromValue(ls);
                    languages.getLanguage().add(language);
                }
            }

            studentLanguageReader.getNextRecord();
        }
        if (languages.getLanguage().size() > 0) {
            student.setLanguages(languages);
        }
    }

    /**
     * generate jaxb parent object from csv record
     *
     * @return jaxb parent object
     */
    private Parent getParent() {
        Map<String, String> parentRecord = parentReader.getCurrentRecord();

        Parent parent = new Parent();

        // set Id
        String parentId = parentRecord.get("ParentUSI");
        parent.setParentUniqueStateId(parentId);
        parent.setId("PRNT_" + parentId);

        // set name
        parent.setName(this.getName(parentRecord));

        // set sex
        parent.setSex(SexType.fromValue(parentRecord.get("Sex")));

        return parent;
    }

    /**
     * generate StudentParentAssociation jaxb object from csv record
     *
     * @return a studentParentAssociation jaxb object
     */
    private StudentParentAssociation getStudentParentAssociation() {
        Map<String, String> studentParentAssociationRecord = studentParentAssociationReader.getCurrentRecord();

        StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

        // set student reference
        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentParentAssociationRecord.get("StudentUSI"));
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        studentParentAssociation.setStudentReference(srt);

        // set parent reference
        ParentIdentityType pit = new ParentIdentityType();
        pit.setParentUniqueStateId(studentParentAssociationRecord.get("ParentUSI"));
        ParentReferenceType prt = new ParentReferenceType();
        prt.setParentIdentity(pit);
        studentParentAssociation.setParentReference(prt);


        // set relation
        if (!studentParentAssociationRecord.get("Relation").isEmpty()) {
            studentParentAssociation
                    .setRelation(RelationType.fromValue(studentParentAssociationRecord.get("Relation")));
        }

        // set primary contact status
        String primaryContact = studentParentAssociationRecord.get("PrimaryContactStatus");
        if (!primaryContact.isEmpty()) {
            studentParentAssociation.setPrimaryContactStatus(primaryContact.equals("1") ? true : false);
        }

        // set lives with
        String livesWith = studentParentAssociationRecord.get("LivesWith");
        if (!livesWith.isEmpty()) {
            studentParentAssociation.setLivesWith(livesWith.equals("1") ? true : false);
        }

        // set emergency contact status
        String emergencyContact = studentParentAssociationRecord.get("EmergencyContactStatus");
        if (!emergencyContact.isEmpty()) {
            studentParentAssociation.setEmergencyContactStatus(emergencyContact.equals("1") ? true : false);
        }

        return studentParentAssociation;
    }

    /**
     * generate address jaxb object from csv record
     *
     * @param addressRecord
     * @return an Address jaxb object
     */
    private Address getAddress(Map<String, String> addressRecord) {
        Address address = new Address();

        String addressType = addressRecord.get("AddressType");
        if (!addressType.isEmpty()) {
            address.setAddressType(AddressType.fromValue(addressType));
        }

        address.setStreetNumberName(addressRecord.get("StreetNumberName"));
        address.setCity(addressRecord.get("City"));

        String state = addressRecord.get("StateAbbreviation");
        if (!state.isEmpty()) {
            address.setStateAbbreviation(StateAbbreviationType.fromValue(state));
        }

        address.setPostalCode(addressRecord.get("PostalCode"));
        address.setNameOfCounty(addressRecord.get("NameOfCounty"));

        return address;
    }

    /**
     * generate name jaxb object from csv record
     *
     * @param nameRecord
     * @return a name jaxb object
     */
    private Name getName(Map<String, String> nameRecord) {
        Name name = new Name();

        String verification = nameRecord.get("Verification");
        if (!verification.isEmpty()) {
            name.setVerification(PersonalInformationVerificationType.fromValue(verification));
        }

        String prefix = nameRecord.get("PersonalTitlePrefix");
        if (!prefix.isEmpty()) {
            name.setPersonalTitlePrefix(PersonalTitlePrefixType.fromValue(prefix));
        }

        name.setFirstName(nameRecord.get("FirstName"));

        String middleName = nameRecord.get("MiddleName");
        if (!middleName.isEmpty()) {
            name.setMiddleName(middleName);
        }
        name.setLastSurname(nameRecord.get("LastSurname"));

        String generation = nameRecord.get("GenerationCodeSuffix");
        if (!generation.isEmpty()) {
            name.setGenerationCodeSuffix(GenerationCodeSuffixType.fromValue(generation));
        }

        return name;
    }

}
