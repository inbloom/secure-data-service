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

import org.slc.sli.sample.entities.RaceItemType;
import org.slc.sli.sample.entities.SexType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.generators.AddressGenerator;
import org.slc.sli.test.generators.NameGenerator;

public class StudentParentDataGenerator {

    private String[] studentColumns = { "StudentUSI", "Verification", "PersonalTitlePrefix", "FirstName", "MiddleName",
            "LastSurname", "GenerationCodeSuffix", "Sex", "BirthDate", "ProfileThumbnail", "HispanicLatinoEthnicity",
            "RacialCategory" };
    private String[] studentAddressColumns = { "StudentUSI", "AddressType", "StreetNumberName", "City",
            "StateAbbreviation", "PostalCode", "NameOfCounty" };
    private String[] studentLanguageColumns = { "StudentUSI", "Language" };

    private String[] parentColumns = { "ParentUSI", "PersonalTitlePrefix", "Verification", "FirstName", "MiddleName",
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

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
    }

    public void generateData() throws Exception {
        NameGenerator ng = new NameGenerator();
        AddressGenerator ag = new AddressGenerator(StateAbbreviationType.NY);
        Random r = new Random();

        PrintWriter studentWriter = new PrintWriter(new File(studentFile));
        String header = join(Arrays.asList(studentColumns), ",");
        studentWriter.println(header);

        for (int i = 0; i < totalStudent; i++) {
            String[] studentRecord = new String[studentColumns.length];
            studentRecord[0] = Integer.toString(studentId++);

            SexType sex = r.nextBoolean() ? SexType.MALE : SexType.FEMALE;
            studentRecord[7] = sex.name();
            Name studentName = (sex == SexType.MALE) ? ng.getMaleName() : ng.getFemaleName();
            studentRecord[1] = studentName.getVerification().name();
            studentRecord[2] = studentName.getPersonalTitlePrefix().name();
            studentRecord[3] = studentName.getFirstName();
            studentRecord[4] = studentName.getMiddleName();
            studentRecord[5] = studentName.getLastSurname();
            studentRecord[6] = studentName.getGenerationCodeSuffix().name();

            Calendar birthday = Calendar.getInstance();
            int dayFromNow = r.nextInt(3650) + 2190;
            birthday.add(Calendar.DATE, -dayFromNow);
            studentRecord[8] = this.calendar2String(birthday);
            studentRecord[9] = r.nextBoolean() ? "thumbnail" : "";
            studentRecord[10] = r.nextBoolean() ? "0" : "1";
            studentRecord[11] = RaceItemType.values()[r.nextInt(RaceItemType.values().length)].name();

            studentWriter.println(this.join(Arrays.asList(studentRecord), ","));
        }
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

        public StudentParent(int s, int p) {
            this.studentId = s;
            this.parentId = p;
        }
    }

}
