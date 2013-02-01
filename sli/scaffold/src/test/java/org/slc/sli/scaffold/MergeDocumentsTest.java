/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.scaffold;

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
    
    @Test
    public void testApplyMerge() throws ScaffoldException, URISyntaxException, DOMException, XPathException {
        URL sampleUrl = this.getClass().getResource("/wadl.xml");
        URL mergeUrl = this.getClass().getResource("/merge-wadl.xml");
        
        merge.merge(new File(sampleUrl.toURI()), new File(mergeUrl.toURI()), "esample.txt");
    }
    
}
