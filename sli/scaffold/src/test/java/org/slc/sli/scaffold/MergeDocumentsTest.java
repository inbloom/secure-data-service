package org.slc.sli.scaffold;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.xpath.XPathException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;

/**
 * Test MergeDocuments
 * @author srupasinghe
 *
 */
public class MergeDocumentsTest {
    MergeDocuments merge = new MergeDocuments(); //class under test
    
    @Before
    public void setup() {
    }
    
    @Test
    public void testApplyMerge() throws ScaffoldException, URISyntaxException, DOMException, XPathException {
        URL sampleUrl = this.getClass().getResource("/sample.xml");
        URL mergeUrl = this.getClass().getResource("/merge-test.xml");
        
        merge.merge(new File(sampleUrl.toURI()), new File(mergeUrl.toURI()), "esample.txt");
    }
}
