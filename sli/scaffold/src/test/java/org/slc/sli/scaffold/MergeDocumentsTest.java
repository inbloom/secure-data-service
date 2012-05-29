package org.slc.sli.scaffold;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.xpath.XPathException;

/**
 * Test MergeDocuments
 * 
 * @author srupasinghe
 */
public class MergeDocumentsTest {
    DocumentManipulator handler = new DocumentManipulator();
    MergeDocuments merge = new MergeDocuments(); // class under test
    
    @Before
    public void setup() {
        handler.init();
    }
    
    @Test
    public void testApplyMerge() throws ScaffoldException, URISyntaxException, DOMException, XPathException {
        URL sampleUrl = this.getClass().getResource("/wadl.xml");
        URL mergeUrl = this.getClass().getResource("/merge-wadl.xml");
        
        merge.merge(new File(sampleUrl.toURI()), new File(mergeUrl.toURI()), "esample.txt");
    }
    
}
