package org.slc.sli.scaffold;

import org.w3c.dom.Document;

import java.io.File;

/**
 * User: wscott
 */
public class XslTransformDocument {
    private final DocumentManipulator handler = new DocumentManipulator();

    public XslTransformDocument() {
        handler.init();
    }

    public void transform(File wadlFile, File xsltFile, String outputFile) {
        try {
            Document wadlDoc = handler.parseDocument(wadlFile);

            handler.serializeDocumentToHtml(wadlDoc, new File(outputFile), xsltFile);
        } catch (DocumentManipulatorException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param args wadl, xslt, output
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            return;
        }

        XslTransformDocument xslt = new XslTransformDocument();
        xslt.transform(new File(args[0]), new File(args[1]), args[2]);
    }
}
