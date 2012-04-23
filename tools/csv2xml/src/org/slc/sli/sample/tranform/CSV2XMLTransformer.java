package org.slc.sli.sample.tranform;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slc.sli.sample.entities.Address;
import org.slc.sli.sample.entities.AddressType;
import org.slc.sli.sample.entities.BirthData;
import org.slc.sli.sample.entities.GenerationCodeSuffixType;
import org.slc.sli.sample.entities.InterchangeStudentParent;
import org.slc.sli.sample.entities.LanguageItemType;
import org.slc.sli.sample.entities.LanguagesType;
import org.slc.sli.sample.entities.Name;
import org.slc.sli.sample.entities.Parent;
import org.slc.sli.sample.entities.ParentReferenceType;
import org.slc.sli.sample.entities.PersonalInformationVerificationType;
import org.slc.sli.sample.entities.PersonalTitlePrefixType;
import org.slc.sli.sample.entities.RaceItemType;
import org.slc.sli.sample.entities.RaceType;
import org.slc.sli.sample.entities.RelationType;
import org.slc.sli.sample.entities.SexType;
import org.slc.sli.sample.entities.StateAbbreviationType;
import org.slc.sli.sample.entities.Student;
import org.slc.sli.sample.entities.StudentParentAssociation;
import org.slc.sli.sample.entities.StudentReferenceType;

/**
 *
 * This class converts CSV files into an Ed-Fi xml file.
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

    // maps to track references
    private Map<String, StudentReferenceType> studentRefs = new HashMap<String, StudentReferenceType>();
    private Map<String, ParentReferenceType> parentRefs = new HashMap<String, ParentReferenceType>();

    // input csv files
    private String studentFile = "data/Student.csv";
    private String studentAddressFile = "data/StudentAddress.csv";
    private String studentLanguagesFile = "data/StudentLanguage.csv";
    private String parentFile = "data/Parent.csv";
    private String studentParentAssociationFile = "data/StudentParentAssociation.csv";

    // output Ed-Fi xml file
    private static String interchangeStudentParentFile = "data/InterchangeStudentParent.xml";

    /**
     * open csv files and create CSV reader for each file
     * @throws IOException
     */
    private void loadData() throws IOException {
        // load student data
        studentReader = new CSVReader(studentFile);
        studentAddressReader = new CSVReader(studentAddressFile);
        studentAddressReader.getNextRecord();
        studentLanguageReader = new CSVReader(studentLanguagesFile);
        studentLanguageReader.getNextRecord();

        // load parent data
        parentReader = new CSVReader(parentFile);

        // load studentParentAssociation data
        studentParentAssociationReader = new CSVReader(studentParentAssociationFile);
    }

    /**
     * Convert csv files into one xml and print it into PrintStream: ps
     * @param ps
     * @throws JAXBException
     */
    private void printInterchangeStudentParent(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentParent.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentParent interchangeStudentParent = new InterchangeStudentParent();
        List<Object> list = interchangeStudentParent.getStudentOrParentOrStudentParentAssociation();

        // process student
        while (studentReader.getNextRecord() != null) {
            list.add(this.getStudent());
        }

        // process parent
        while (parentReader.getNextRecord() != null) {
            list.add(this.getParent());
        }

        // process studentParentAssociation
        while (studentParentAssociationReader.getNextRecord() != null) {
            list.add(this.getStudentParentAssociation());
        }

        marshaller.marshal(interchangeStudentParent, ps);
    }

    /**
     * generate student jaxb object from csv records
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
        while (studentAddressReader.getCurrentRecord() != null) {
            Map<String, String> studentAddressRecord = studentAddressReader.getCurrentRecord();

            String id = studentAddressRecord.get("StudentUSI");
            if (id.compareTo(studentId) > 0) {
                break;
            } else if (id.equals(studentId)) {
                student.getAddress().add(this.getAddress(studentAddressRecord));
            }

            studentAddressReader.getNextRecord();
        }

        // set languages
        LanguagesType languages = new LanguagesType();
        while (studentLanguageReader.getCurrentRecord() != null) {
            Map<String, String> studentLanguageRecord = studentLanguageReader.getCurrentRecord();

            String id = studentLanguageRecord.get("StudentUSI");
            if (id.compareTo(studentId) > 0) {
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
        student.setLanguages(languages);

        // tracking student references
        StudentReferenceType studentReference = new StudentReferenceType();
        studentReference.setRef(student);
        studentRefs.put(studentId, studentReference);

        return student;
    }

    /**
     * generate jaxb parent object from csv record
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

        // tracking parent references
        ParentReferenceType parentReference = new ParentReferenceType();
        parentReference.setRef(parent);
        parentRefs.put(parentId, parentReference);

        return parent;
    }

    /**
     * generate StudentParentAssociation jaxb object from csv record
     * @return a studentParentAssociation jaxb object
     */
    private StudentParentAssociation getStudentParentAssociation() {
        Map<String, String> studentParentAssociationRecord = studentParentAssociationReader.getCurrentRecord();

        StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

        // set student reference
        studentParentAssociation.setStudentReference(studentRefs.get(studentParentAssociationRecord.get("StudentUSI")));

        // set parent reference
        studentParentAssociation.setParentReference(parentRefs.get(studentParentAssociationRecord.get("ParentUSI")));

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

    /**
     * main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CSV2XMLTransformer transformer = new CSV2XMLTransformer();
        transformer.loadData();

        PrintStream ps = new PrintStream(new File(interchangeStudentParentFile));
        transformer.printInterchangeStudentParent(ps);

        SchemaValidater.check("./data/");
    }

}
