package org.slc.sli.scaffold;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test MergeDocuments
 * @author srupasinghe
 *
 */
public class MergeDocumentsTest {
    MergeDocuments merge = new MergeDocuments();
    
    @Test (expected=ScaffoldException.class)
    public void testParseDocumentEmptyFile() throws ScaffoldException {
        merge.parseDocument(new File(""));
    }
    
    @Test
    public void testParseDocument() throws ScaffoldException, URISyntaxException {
        URL url = this.getClass().getResource("/sample.xml");
        
        Document doc = merge.parseDocument(new File(url.toURI()));
        
        assertNotNull("Document should not be null", doc);
    }
}
