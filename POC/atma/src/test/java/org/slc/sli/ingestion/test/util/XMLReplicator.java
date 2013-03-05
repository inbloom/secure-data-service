package org.slc.sli.ingestion.test.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLReplicator {
    private static final SAXBuilder builder = new SAXBuilder();
    private static final XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

    public static void main(String[] args) throws Exception {
        Document doc = builder.build(new FileReader("src/test/resources/xml/small/InterchangeSection.xml"));

        for(int i=0;i<14;i++) {
            doc.getRootElement().addContent(doc.getRootElement().cloneContent());
        }

        out.output(doc, new FileOutputStream(new File("src/test/resources/xml/small/InterchangeSectionBig.xml")));
        System.out.println(doc.getRootElement().getContentSize());
    }
}
