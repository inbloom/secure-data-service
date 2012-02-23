package org.slc.sli.scaffold;

import org.junit.Before;
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

    @Before
    public void setup() {
        handler.init();
    }

    @Test
    public void testXslt() {
        URL wadlUrl = this.getClass().getResource("/eapplication.wadl");
        URL xsltUrl = this.getClass().getResource("/wadl.xsl");

        try {
            xslt.transform(new File(wadlUrl.toURI()), new File(xsltUrl.toURI()), "out.html");
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}
