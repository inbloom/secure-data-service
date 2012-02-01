package org.slc.sli.scaffold;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class handles the document manipulation tasks
 * @author srupasinghe
 *
 */
public class DocumentManipulator {
    private DocumentBuilderFactory docFactory;
    private XPathFactory xPathFactory;
 
    protected void init() {
        docFactory = DocumentBuilderFactory.newInstance();
        xPathFactory = XPathFactory.newInstance();
        
        docFactory.setNamespaceAware(true);
    }
    
    /**
     * Parse a xml document
     * @param fileName
     * @throws ScaffoldException 
     */
    protected Document parseDocument(File file) throws DocumentManipulatorException {
        Document doc = null;
        
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            doc = builder.parse(file);
        } catch (SAXException e) {
            //change to better exception handling later
            throw new DocumentManipulatorException(e);
        } catch (IOException e) {
            throw new DocumentManipulatorException(e);
        } catch (ParserConfigurationException e) {
            throw new DocumentManipulatorException(e);
        }
        
        return doc;
    }
    
    /**
     * Evaluates a xpath expression and returns a NodeList 
     * @param doc
     * @param expression
     * @return
     * @throws XPathException
     */
    protected NodeList getNodeList(Document doc, String expression) throws XPathException {
        XPath xpath = xPathFactory.newXPath();
        XPathExpression exp = xpath.compile(expression);
            
        Object result = exp.evaluate(doc, XPathConstants.NODESET);
            
        return (NodeList) result;
    }
    
    /**
     * Serializes a document to html
     * @param document
     * @param outputFile
     * @param xslFile
     * @throws DocumentManipulatorException
     */
    public void serializeDocumentToHtml(Document document, File outputFile, File xslFile) throws DocumentManipulatorException {
        FileOutputStream fileOutput = null;     
        StreamSource xslSource = null;
        Transformer transformer = null;
        
        try {
            Properties oprops = new Properties();
            
            //set the output properties
            oprops.put(OutputKeys.METHOD, "html");
            oprops.put("indent-amount", "4");
            
            fileOutput = new FileOutputStream(outputFile);
            //create the stream source from the xsl stylesheet
            xslSource = new StreamSource(xslFile);
            
            //get a transformer object with xsl styling
            transformer = TransformerFactory.newInstance().newTransformer(xslSource);
            //set the properties
            transformer.setOutputProperties(oprops);
            //perform the transform
            transformer.transform(new DOMSource(document), new StreamResult(fileOutput));
            
        } catch (FileNotFoundException e) {
           throw new DocumentManipulatorException(e);
        } catch (TransformerConfigurationException e) {
            throw new DocumentManipulatorException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new DocumentManipulatorException(e);
        } catch (TransformerException e) {
            throw new DocumentManipulatorException(e);
        } 
        
    }
    
    /**
     * Serializes a document to string
     * @param document
     * @return
     * @throws DocumentManipulatorException
     */
    public String serializeDocumentToString(Document document) throws DocumentManipulatorException {
        Transformer transformer = null;
        StringWriter outText = new StringWriter();
        StreamResult stream = new StreamResult(outText);
        
        try {
            Properties oprops = new Properties();
            
            oprops.put(OutputKeys.METHOD, "xml");
            //oprops.put("indent-amount", "4");
            
            //create the transformer
            transformer = TransformerFactory.newInstance().newTransformer();
            //set the properties
            transformer.setOutputProperties(oprops);
            //perform the transformation
            transformer.transform(new DOMSource(document), stream);
            
        } catch (TransformerConfigurationException e) {
            throw new DocumentManipulatorException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new DocumentManipulatorException(e);
        } catch (TransformerException e) {
            throw new DocumentManipulatorException(e);
        }
        
        return outText.toString();
    }
}
