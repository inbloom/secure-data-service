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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * This class handles the document manipulation tasks
 * 
 * @author srupasinghe
 */
public class DocumentManipulator {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentManipulator.class);

    private DocumentBuilderFactory docFactory;
    private XPathFactory xPathFactory;

    public DocumentManipulator() {
        docFactory = DocumentBuilderFactory.newInstance();
        xPathFactory = XPathFactory.newInstance();

        docFactory.setNamespaceAware(false);
    }
    
    /**
     * Parse a xml document
     * 
     * @param file
     * @throws ScaffoldException
     */
    protected Document parseDocument(File file) throws DocumentManipulatorException {
        Document doc = null;
        
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            doc = builder.parse(file);
        } catch (SAXException e) {
            // change to better exception handling later
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
     * 
     * @param doc
     * @param expression
     * @return
     * @throws XPathException
     */
    public NodeList getNodeList(Document doc, String expression) throws XPathException {
        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext(new DocumentNamespaceResolver(doc));
        
        XPathExpression exp = xpath.compile(expression);
        
        Object result = exp.evaluate(doc, XPathConstants.NODESET);
        
        return (NodeList) result;
    }
    
    /**
     * Serializes a document to html
     * 
     * @param document
     * @param outputFile
     * @param xslFile
     * @throws DocumentManipulatorException
     */
    public void serializeDocumentToHtml(Document document, File outputFile, File xslFile)
            throws DocumentManipulatorException {
        FileOutputStream fileOutput = null;
        StreamSource xslSource = null;
        
        Properties props = new Properties();
        
        // set the output properties
        props.put(OutputKeys.METHOD, "html");
        
        try {
            fileOutput = new FileOutputStream(outputFile);
            // create the stream source from the xsl stylesheet
            xslSource = new StreamSource(xslFile);
            
            serialize(document, new StreamResult(fileOutput), xslSource, props);
        } catch (FileNotFoundException e) {
            throw new DocumentManipulatorException(e);
        }
    }
    
    /**
     * Serialize a document in XML format to a file
     * 
     * @param document
     * @param outputFile
     * @throws DocumentManipulatorException
     */
    public void serializeDocumentToXml(Document document, File outputFile) throws DocumentManipulatorException {
        PrintWriter out = null;
        
        try {
            out = new PrintWriter(new FileOutputStream(outputFile, false));
            
            out.print(serializeDocumentToString(document));
            out.flush();
        } catch (FileNotFoundException e) {
            throw new DocumentManipulatorException(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     * Serializes a document to string
     * 
     * @param document
     * @return
     * @throws DocumentManipulatorException
     */
    public String serializeDocumentToString(Document document) throws DocumentManipulatorException {
        StringWriter outText = new StringWriter();
        
        Properties props = new Properties();
        props.put(OutputKeys.METHOD, "xml");
        
        serialize(document, new StreamResult(outText), null, props);
        
        return outText.toString();
    }
    
    /**
     * Serializes a given document to a stream
     * 
     * @param document
     * @param stream
     * @param source
     * @param props
     * @throws DocumentManipulatorException
     */
    private void serialize(Document document, StreamResult stream, Source source, Properties props)
            throws DocumentManipulatorException {
        
        TransformerFactory factory = null;
        Transformer transformer = null;
        
        try {
            // create the factory
            factory = TransformerFactory.newInstance();
            
            // create the transformer
            if (source != null) {
                transformer = factory.newTransformer(source);
            } else {
                transformer = factory.newTransformer();
            }
            
            // set the properties
            transformer.setOutputProperties(props);
            // perform the transformation
            transformer.transform(new DOMSource(addDefaultDocs(document)), stream);
            
        } catch (TransformerConfigurationException e) {
            throw new DocumentManipulatorException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new DocumentManipulatorException(e);
        } catch (TransformerException e) {
            throw new DocumentManipulatorException(e);
        } catch (XPathExpressionException e) {
            throw new DocumentManipulatorException(e);
        }
    }
    
    private Document addDefaultDocs(Document doc) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new MyNameSpaceContext());
        XPathExpression exp = xPath.compile("//method[not(doc)]");
        NodeList nl = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            String id = item.getAttributes().getNamedItem("id").getNodeValue();
            Node docElem = doc.createElement("doc");
            String defaultDoc = null;
            if ("readAll".equals(id)) {
                defaultDoc = "Returns the requested collection of resource representations.";
            } else if ("read".equals(id)) {
                defaultDoc = "Returns the specified resource representation(s).";
            } else if ("create".equals(id)) {
                defaultDoc = "Creates a new resource using the given resource data.";
            } else if ("delete".equals(id)) {
                defaultDoc = "Deletes the specified resource.";
            } else if ("update".equals(id)) {
                defaultDoc = "Updates the specified resource using the given resource data.";
            }
            if (defaultDoc != null) {
                Text text = doc.createTextNode(defaultDoc);
                docElem.appendChild(text);
                item.appendChild(docElem);
            }
        }

        // "//ns2:param[@name='id']"
        XPathExpression idDelExp = xPath.compile("//param[contains(@name, 'id') or contains(@name, 'Id')][@style='template']/doc");
        NodeList idDelNl = (NodeList) idDelExp.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < idDelNl.getLength(); i++) {
            Node item = idDelNl.item(i);
            item.getParentNode().removeChild(item);
        }

        // "//ns2:param[@name='id']"
        XPathExpression idExp = xPath.compile("//param[contains(@name, 'id') or contains(@name, 'Id')][@style='template'][not(doc)]");
        NodeList idNl = (NodeList) idExp.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < idNl.getLength(); i++) {
            Node item = idNl.item(i);
            Node docElem = doc.createElement("doc");
            String defaultDoc = "A comma-separated list of resource IDs.";
            if (defaultDoc != null) {
                Text text = doc.createTextNode(defaultDoc);
                docElem.appendChild(text);
                item.appendChild(docElem);
            }
        }

        return doc;
    }

    private static final class MyNameSpaceContext implements NamespaceContext {
        @Override
        public String getNamespaceURI(String s) {
            return null;
        }

        @Override
        public String getPrefix(String s) {
            return null;
        }

        @Override
        public Iterator getPrefixes(String s) {
            return null;
        }
    }
}
