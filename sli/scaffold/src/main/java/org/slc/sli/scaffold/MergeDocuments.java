package org.slc.sli.scaffold;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Generic xml document
 * @author srupasinghe
 *
 */
public class MergeDocuments {
            
    public MergeDocuments() {                
    }
    
    public static void main(String[] args) {
        
        if (args.length < 1) return;
        
        MergeDocuments merge = new MergeDocuments();
        merge.merge(args[0]);
    }
    
    public void merge(String fileName) {
        try {
            parseDocument(new File(fileName));
        } catch (ScaffoldException e) {
            //need to do something better
            e.printStackTrace();
        }
    }
    
    /**
     * Parse a xml document
     * @param fileName
     * @throws ScaffoldException 
     */
    protected Document parseDocument(File file) throws ScaffoldException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(file);
        } catch (SAXException e) {
            //change to better exception handling later
            throw new ScaffoldException(e);
        } catch (IOException e) {
            throw new ScaffoldException(e);
        } catch (ParserConfigurationException e) {
            throw new ScaffoldException(e);
        }
        
        return doc;
    }

}
