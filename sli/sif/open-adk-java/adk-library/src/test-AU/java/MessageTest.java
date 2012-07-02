import junit.framework.TestCase;
import openadk.library.ADK;
import openadk.library.SIFDTD;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.common.Name;
import openadk.library.student.StudentPersonal;
import org.junit.Assert;

import java.io.StringWriter;


public class MessageTest extends TestCase {

    public void setUp() throws Exception {
        ADK.initialize(SIFVersion.LATEST, SIFDTD.SDO_ALL);
    }

    public StudentPersonal testStudentPersonal() {
        StudentPersonal studentPersonal = new StudentPersonal();
        Name name = new Name();
        name.setGivenName("Joe");
        name.setFamilyName("Student");
        studentPersonal.setPersonInfo(name);
        return studentPersonal;
    }

    public void testMessageNamespace() {

        StudentPersonal studentPersonal = testStudentPersonal();

        StringWriter stringWriter = new StringWriter();
        SIFWriter sifWriter = new SIFWriter(stringWriter, true);
        sifWriter.write(studentPersonal);
        sifWriter.close();

        String xml = stringWriter.getBuffer().toString();
        Assert.assertTrue("SIF Message contains AU namespace", xml.contains("xmlns=\"http://www.sifinfo.org/au/infrastructure/2.x\""));
    }

}
