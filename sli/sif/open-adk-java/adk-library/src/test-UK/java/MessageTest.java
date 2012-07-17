import junit.framework.TestCase;
import openadk.library.ADK;
import openadk.library.SIFDTD;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.learner.LearnerPersonal;
import org.junit.Assert;

import java.io.StringWriter;


public class MessageTest extends TestCase {

    public void setUp() throws Exception {
        ADK.initialize(SIFVersion.LATEST, SIFDTD.SDO_ALL);
    }

    public LearnerPersonal testStudentLearner() {
        LearnerPersonal learnerPersonal = new LearnerPersonal();

        return learnerPersonal;
    }

    public void testMessageNamespace() {

        LearnerPersonal learnerPersonal = testStudentLearner();

        StringWriter stringWriter = new StringWriter();
        SIFWriter sifWriter = new SIFWriter(stringWriter, true);
        sifWriter.write(learnerPersonal);
        sifWriter.close();

        String xml = stringWriter.getBuffer().toString();
        Assert.assertTrue("SIF Message contains UK namespace", xml.contains("xmlns=\"http://www.sifinfo.org/uk/infrastructure/2.x\""));
    }

}
