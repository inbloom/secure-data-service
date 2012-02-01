package org.slc.sli.scaffold;

import java.io.File;

import javax.xml.xpath.XPathException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Generic xml document
 * @author srupasinghe
 *
 */
public class MergeDocuments {
    private DocumentManipulator handler = new DocumentManipulator();
    private static final String BASE_XPATH_EXPR = "//merges/merge";
    private static final String ACTION_ATTR = "action";
    private static final String XPATH_ATTR = "xpath";
    private static final String TEXT_ELEM = "text";
    private static final String ACTION_ADD = "ADD";
    private static final String ACTION_UPDATETXT = "UPDATETXT";
            
    public MergeDocuments() {                
    }
    
    public static void main(String[] args) {
        
        if (args.length < 2) return;
        
        MergeDocuments merge = new MergeDocuments();
        merge.merge(new File(args[0]), new File(args[1]));
    }
    
    public void merge(File baseFile, File mergeFile) {
        try {
            handler.init();
            
            Document wadlDoc = handler.parseDocument(baseFile);
            Document mergeDoc = handler.parseDocument(mergeFile);
            
            applyMerge(wadlDoc, mergeDoc);
            
            //System.out.println(handler.serializeDocumentToString(wadlDoc));
        } catch (DocumentManipulatorException e) {
            //need to do something better
            e.printStackTrace();
        } catch (DOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XPathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Starts the merge process
     * @param mainDoc
     * @param mergeDoc
     * @throws DOMException
     * @throws XPathException
     */
    protected void applyMerge(Document mainDoc, Document mergeDoc) throws DOMException, XPathException {
        NodeList children = handler.getNodeList(mergeDoc, BASE_XPATH_EXPR);
        
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            
            //get the attribute map and the child nodes
            NamedNodeMap attrMap = node.getAttributes();
            NodeList list = node.getChildNodes();
            
            //perform the transform
            performTransform(mainDoc, attrMap.getNamedItem(ACTION_ATTR).getNodeValue(), 
                    attrMap.getNamedItem(XPATH_ATTR).getNodeValue(), list);
        }
    }
    
    /**
     * Performs the transform on the given document with the xpath and node list
     * @param doc
     * @param action
     * @param xpath
     * @param mergeNodeList
     * @throws XPathException
     */
    protected void performTransform(Document doc, String action, String xpath, NodeList mergeNodeList) throws XPathException {
        //get the node list that matches the xpath expr
        NodeList baseList = handler.getNodeList(doc, xpath);
        
        if (action.equals(ACTION_ADD)) {
            for (int i = 0; i < baseList.getLength(); i++) {
                Node root = baseList.item(i);
                
                //got through and add each new node to the root
                for (int k = 0; k < mergeNodeList.getLength(); k++) {
                    Node n = mergeNodeList.item(k);
                    
                    if (n.getNodeType() != 3) {
                        root.appendChild(root.getOwnerDocument().adoptNode(n));                        
                    }
                }
            }
        } else if (action.equals(ACTION_UPDATETXT)) {
            for (int i = 0; i < baseList.getLength(); i++) {
                Node n = baseList.item(i);
                
                for (int k = 0; k < mergeNodeList.getLength(); k++) {
                    Node txt = mergeNodeList.item(k);
                    
                    //if its a text element then get the text
                    if (txt.getNodeName().equals(TEXT_ELEM)) {
                        n.setTextContent(txt.getTextContent());
                    }
                }
            }
        } else if (action.equals("ADDATTR")) {
            for (int i = 0; i < baseList.getLength(); i++) {
                Node n = baseList.item(i);
                
                for (int k = 0; k < mergeNodeList.getLength(); k++) {
                    Node attr = mergeNodeList.item(k);
                    
                    //if its an attribute element then get the attributes
                    if (attr.getNodeName().equals("attribute")) {
                        NamedNodeMap map = attr.getAttributes();

                        //create the new attribute
                        Attr attribute = n.getOwnerDocument().createAttribute(map.getNamedItem("name").getNodeValue());
                        attribute.setValue(map.getNamedItem("value").getNodeValue());
                        
                        //set it in the main document
                        n.getAttributes().setNamedItem(attribute);
                    }
                }
            }

        }
    }

    

}
