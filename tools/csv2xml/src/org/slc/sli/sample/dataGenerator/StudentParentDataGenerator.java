package org.slc.sli.sample.dataGenerator;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slc.sli.sample.entities.AddressType;
import org.slc.sli.sample.entities.LanguageItemType;
import org.slc.sli.sample.entities.RaceItemType;
import org.slc.sli.sample.entities.SexType;
import org.slc.sli.test.edfi.entities.Address;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.generators.AddressGenerator;
import org.slc.sli.test.generators.NameGenerator;

public class StudentParentDataGenerator {
    private Random r = new Random();
    private NameGenerator ng;
    private AddressGenerator ag;

    private String[] studentColumns = { "StudentUSI", "Verification", "PersonalTitlePrefix", "FirstName", "MiddleName",
            "LastSurname", "GenerationCodeSuffix", "Sex", "BirthDate", "ProfileThumbnail", "HispanicLatinoEthnicity",
            "RacialCategory" };
    private String[] studentAddressColumns = { "StudentUSI", "AddressType", "StreetNumberName", "City",
            "StateAbbreviation", "PostalCode", "NameOfCounty" };
    private String[] studentLanguageColumns = { "StudentUSI", "Language" };

    private String[] parentColumns = { "ParentUSI", "Verification", "PersonalTitlePrefix", "FirstName", "MiddleName",
            "LastSurname", "GenerationCodeSuffix", "MaidenName", "Sex" };

    private String[] spaColumns = { "StudentUSI", "ParentUSI", "Relation", "PrimaryContactStatus", "LivesWith",
            "EmergencyContactStatus" };

    // output file names
    private String studentFile = "data1/Student.csv";
    private String studentAddressFile = "data1/StudentAddress.csv";
    private String studentLanguagesFile = "data1/StudentLanguage.csv";
    private String parentFile = "data1/Parent.csv";
    private String studentParentAssociationFile = "data1/StudentParentAssociation.csv";

    private int studentId = 100000;
    private int parentId = 900000;
    private static int totalStudent = 5000;

    private List<StudentParent> studentParentList = new ArrayList<StudentParent>();

    public StudentParentDataGenerator() throws Exception {
        ng = new NameGenerator();
        ag = new AddressGenerator(StateAbbreviationType.NY);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        StudentParentDataGenerator spdg = new StudentParentDataGenerator();
        spdg.generateData();
        System.out.println("done");
    }

    public void generateData() throws Exception {

        PrintWriter studentWriter = new PrintWriter(new File(studentFile));
        String header = join(Arrays.asList(studentColumns), ",");
        studentWriter.println(header);

        PrintWriter studentAddressWriter = new PrintWriter(new File(this.studentAddressFile));
        header = join(Arrays.asList(this.studentAddressColumns), ",");
        studentAddressWriter.println(header);

        PrintWriter studentLanguageWriter = new PrintWriter(new File(this.studentLanguagesFile));
        header = join(Arrays.asList(this.studentLanguageColumns), ",");
        studentLanguageWriter.println(header);

        PrintWriter parentWriter = new PrintWriter(new File(parentFile));
        header = this.join(Arrays.asList(this.parentColumns), ",");
        parentWriter.println(header);

        PrintWriter spaWriter = new PrintWriter(new File(this.studentParentAssociationFile));
        header = this.join(Arrays.asList(this.spaColumns), ",");
        spaWriter.println(header);

        for (int i = 0; i < totalStudent; i++) {
            // create students
            String[] studentRecord = new String[studentColumns.length];
            studentRecord[0] = Integer.toString(studentId++);

            SexType sex = r.nextBoolean() ? SexType.MALE : SexType.FEMALE;
            studentRecord[7] = sex.value();
            Name studentName = (sex == SexType.MALE) ? ng.getMaleName() : ng.getFemaleName();
            studentRecord[1] = studentName.getVerification().value();
            studentRecord[2] = studentName.getPersonalTitlePrefix().value();
            studentRecord[3] = studentName.getFirstName();
            studentRecord[4] = studentName.getMiddleName();
            studentRecord[5] = studentName.getLastSurname();
            if (studentName.getGenerationCodeSuffix() != null) {
                studentRecord[6] = studentName.getGenerationCodeSuffix().value();
            } else {
                studentRecord[6] = "";
            }

            Calendar birthday = Calendar.getInstance();
            int dayFromNow = r.nextInt(3650) + 2190;
            birthday.add(Calendar.DATE, -dayFromNow);
            studentRecord[8] = this.calendar2String(birthday);
            studentRecord[9] = r.nextBoolean() ? "thumbnail" : "";
            studentRecord[10] = r.nextBoolean() ? "0" : "1";
            studentRecord[11] = RaceItemType.values()[r.nextInt(RaceItemType.values().length)].value();

            studentWriter.println(this.join(Arrays.asList(studentRecord), ","));

            // create address
            this.generateAddresses(studentId, studentAddressWriter);

            this.generateLanguages(studentId, studentLanguageWriter);

            // create parents
            boolean hasParents = r.nextBoolean();
            boolean hasBoth = r.nextBoolean();
            boolean isMother = r.nextBoolean();

            if (hasParents) {
                if (hasBoth) {
                    Name fatherName = ng.getMaleName();
                    this.writeRecord(parentId++, fatherName, SexType.MALE.value(), parentWriter);
                    studentParentList.add(new StudentParent(studentId - 1, parentId - 1, false));
                    Name motherName = ng.getFemaleName();
                    this.writeRecord(parentId++, motherName, SexType.FEMALE.value(), parentWriter);
                    studentParentList.add(new StudentParent(studentId - 1, parentId - 1, true));
                } else {
                    if (isMother) {
                        Name motherName = ng.getFemaleName();
                        this.writeRecord(parentId++, motherName, SexType.FEMALE.value(), parentWriter);
                        studentParentList.add(new StudentParent(studentId - 1, parentId - 1, true));
                    } else {
                        Name fatherName = ng.getMaleName();
                        this.writeRecord(parentId++, fatherName, SexType.MALE.value(), parentWriter);
                        studentParentList.add(new StudentParent(studentId - 1, parentId - 1, false));
                    }
                }
            }

        }
        studentWriter.close();
        studentAddressWriter.close();
        studentLanguageWriter.close();
        parentWriter.close();

        // write studentParentAssociation file
        for (StudentParent sp : studentParentList) {
            String[] spaRecord = new String[spaColumns.length];
            spaRecord[0] = Integer.toString(sp.studentId);
            spaRecord[1] = Integer.toString(sp.parentId);
            spaRecord[2] = sp.isMother ? "Mother" : "Father";
            spaRecord[3] = r.nextBoolean() ? "1" : "0";
            spaRecord[4] = r.nextBoolean() ? "1" : "0";
            spaRecord[5] = r.nextBoolean() ? "1" : "0";
            spaWriter.println(this.join(Arrays.asList(spaRecord), ","));
        }
        spaWriter.close();
    }

    private void generateAddresses(int id, PrintWriter pw) {
        int numberOfAddress = r.nextInt(2) + 1;
        for (int i = 0; i < numberOfAddress; i++) {
            Address address = ag.getRandomAddress();
            String[] addressRecord = new String[this.studentAddressColumns.length];
            addressRecord[0] = Integer.toString(id);
            addressRecord[1] = AddressType.values()[r.nextInt(AddressType.values().length)].value();
            addressRecord[2] = (r.nextInt(1000) + 1) + " " + address.getStreetNumberName();
            addressRecord[3] = address.getCity();
            addressRecord[4] = address.getStateAbbreviation().value();
            addressRecord[5] = address.getPostalCode();
            addressRecord[6] = address.getNameOfCounty();

            pw.println(this.join(Arrays.asList(addressRecord), ","));
        }
    }

    private void generateLanguages(int id, PrintWriter pw) {
        int numberOfLanguage = r.nextInt(2);
        int firstLanguage = r.nextInt(LanguageItemType.values().length);
        for (int i = 0; i < numberOfLanguage; i++) {
            String[] languageRecord = new String[this.studentLanguageColumns.length];
            languageRecord[0] = Integer.toString(id);
            languageRecord[1] = LanguageItemType.values()[(firstLanguage + i)%LanguageItemType.values().length].value();

            if (languageRecord[1].indexOf(',') == -1 ) {
                pw.println(this.join(Arrays.asList(languageRecord), ","));
            }
        }
    }

    private void writeRecord(int id, Name name, String sex, PrintWriter pw) {
        String[] parentRecord = new String[parentColumns.length];
        parentRecord[0] = Integer.toString(id);
        parentRecord[1] = name.getVerification().value();
        parentRecord[2] = name.getPersonalTitlePrefix().value();
        parentRecord[3] = name.getFirstName();
        parentRecord[4] = name.getMiddleName();
        parentRecord[5] = name.getLastSurname();
        if (name.getGenerationCodeSuffix() != null) {
            parentRecord[6] = name.getGenerationCodeSuffix().value();
        } else {
            parentRecord[6] = "";
        }
        if (name.getMaidenName() != null) {
            parentRecord[7] = name.getMaidenName();
        } else {
            parentRecord[7] = "";
        }
        parentRecord[8] = sex;

        pw.println(this.join(Arrays.asList(parentRecord), ","));
    }

    private String calendar2String(Calendar c) {
        String s = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

        if (c != null) {
            s = sdf.format(c.getTime());
        }

        return s;
    }

    private String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    class StudentParent {
        public int studentId;
        public int parentId;
        public boolean isMother;

        public StudentParent(int s, int p, boolean isMather) {
            this.studentId = s;
            this.parentId = p;
            this.isMother = isMather;
        }
    }

}
