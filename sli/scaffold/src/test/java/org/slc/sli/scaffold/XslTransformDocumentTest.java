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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * User: wscott
 */
public class XslTransformDocumentTest {
    DocumentManipulator handler = new DocumentManipulator();
    XslTransformDocument xslt = new XslTransformDocument();

    @Test
    public void testXslt() {
        URL wadlUrl = this.getClass().getResource("/eapplication.wadl");
        URL xsltUrl = this.getClass().getResource("/wadl.xsl");

        try {
            xslt.transform(new File(wadlUrl.toURI()), new File(xsltUrl.toURI()), "target/out.html");
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}
