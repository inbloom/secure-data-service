package org.slc.sli.ingestion;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/xmlfileexpander-context.xml" })
/*@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })*/
public class XMLFileExpanderTest {

    @Autowired
    XMLFileExpander xmlFileExpander;

    /**
     *
     */
    @SuppressWarnings("unused")
    @Test
    public void testXMLFileExpander() {

        // Test the XML file expander on a large test file.
        try {
            xmlFileExpander.expandXMLFile("session.xml");
        } catch (IOException e) {
            // Report error.
            System.out.println(e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
