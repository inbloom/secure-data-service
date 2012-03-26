package org.slc.sli.ingestion;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/xmlfileexpander-context.xml" })
public class XMLFileExpanderTest {

    /**
     *
     */
    @SuppressWarnings("unused")
    @Test
    public void testXMLFileExpander() {
        XMLFileExpander xmlFileExpander = new XMLFileExpander();

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
