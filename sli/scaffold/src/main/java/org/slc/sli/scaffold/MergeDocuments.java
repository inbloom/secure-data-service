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
import java.io.IOException;

import javax.xml.xpath.XPathException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Generic xml document
 *
 * @author srupasinghe
 */
public class MergeDocuments {
    private static final Logger LOG = LoggerFactory.getLogger(MergeDocuments.class);

    private final DocumentManipulator handler = new DocumentManipulator();

    private static final String BASE_XPATH_EXPR = "//merges/merge";

    private static final String TYPE_NODE = "node";
    private static final String TYPE_ATTRIBUTE = "attribute";

    private static final String ACTION_ADD = "add";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_SET = "set";

    private static final String ATTR_XPATH = "xpath";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_ACTION = "action";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";

    private static final String NODE_ATTRIBUTE = "attribute";

    public MergeDocuments() {
        // No Op
    }

    public void merge(File baseFile, File mergeFile, String outputFileName) {
        try {
            Document wadlDoc = handler.parseDocument(baseFile);
            Document mergeDoc = handler.parseDocument(mergeFile);

            applyMerge(wadlDoc, mergeDoc);
            addDocumentation(wadlDoc);

            handler.serializeDocumentToXml(wadlDoc, new File(baseFile.getParentFile().getAbsolutePath()
                    + File.separator + outputFileName));
        } catch (DocumentManipulatorException e) {
            LOG.warn(e.getMessage());
        } catch (DOMException e) {
            LOG.warn(e.getMessage());
        } catch (XPathException e) {
            LOG.warn(e.getMessage());
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    private void addDocumentation(final Document wadlDoc) throws IOException, XPathException {
        final ResourceDocumentation resourceDocumentation = new ResourceDocumentation(wadlDoc);
        resourceDocumentation.addDocumentation();
    }


    /**
     * Starts the merge process
     *
     * @param mainDoc  The document to edit.
     * @param mergeDoc The document containing the edit instructions.
     * @throws XPathException A problem parsing the XPath location.
     */
    protected void applyMerge(Document mainDoc, Document mergeDoc) throws XPathException {
        NodeList mergeActions = handler.getNodeList(mergeDoc, BASE_XPATH_EXPR);

        for (int i = 0; i < mergeActions.getLength(); i++) {
            Node node = mergeActions.item(i);

            // get the attribute map and action information
            NamedNodeMap attrMap = node.getAttributes();
            String type = attrMap.getNamedItem(ATTR_TYPE).getNodeValue();
            String action = attrMap.getNamedItem(ATTR_ACTION).getNodeValue();
            String xpath = attrMap.getNamedItem(ATTR_XPATH).getNodeValue();
            NodeList actionArgs = node.getChildNodes();

            // perform the transform
            performTransform(mainDoc, type, action, xpath, actionArgs);
        }
    }

    /**
     * Performs the transform on the given document with the xpath and node list
     *
     * @param doc           Base document to edit.
     * @param type          The type of element to edit (attribute or node).
     * @param action        The action (add, delete, set) to perform.
     * @param xpath         The XPath location to perform the edit.
     * @param mergeNodeList Action arguments. Nodes to add, attributes to set, etc.
     * @throws XPathException A problem parsing the XPath location.
     */
    protected void performTransform(Document doc, String type, String action, String xpath, NodeList mergeNodeList)
            throws XPathException {
        NodeList editNodes = handler.getNodeList(doc, xpath);

        for (int i = 0; i < editNodes.getLength(); i++) {
            Node crntEditNode = editNodes.item(i);

            if (TYPE_ATTRIBUTE.equals(type)) {
                if (ACTION_ADD.equals(action) || ACTION_SET.equals(action)) {
                    attributeSet(crntEditNode, mergeNodeList);
                } else if (ACTION_DELETE.equals(action)) {
                    attributeDelete(crntEditNode, mergeNodeList);
                }
            } else if (TYPE_NODE.equals(type)) {
                if (ACTION_ADD.equals(action)) {
                    nodeAdd(crntEditNode, mergeNodeList);
                } else if (ACTION_DELETE.equals(action)) {
                    nodeDelete(crntEditNode);
                }
            }
        }
    }

    /**
     * Adds the nodes in actionArgs as children nodes to editNode.
     *
     * @param editNode   The node on which children will be added.
     * @param actionArgs The nodes to add.
     */
    private void nodeAdd(Node editNode, NodeList actionArgs) {
        // got through and add each new node to the root
        for (int k = 0; k < actionArgs.getLength(); k++) {
            Node n = actionArgs.item(k);

            if (n.getNodeType() == Document.ELEMENT_NODE) {
                editNode.appendChild(editNode.getOwnerDocument().adoptNode(n.cloneNode(true)));
            }
        }
    }

    /**
     * Deletes a node.
     *
     * @param editNode The node to delete.
     */
    private void nodeDelete(Node editNode) {
        Node parentNode = editNode.getParentNode();
        parentNode.removeChild(editNode);
    }

    /**
     * Deletes an attribute from a node.
     *
     * @param editNode   The node from which to delete the attribute.
     * @param actionArgs An array of Nodes defining attributes to delete.
     */
    private void attributeDelete(Node editNode, NodeList actionArgs) {
        for (int k = 0; k < actionArgs.getLength(); k++) {
            Node attr = actionArgs.item(k);

            // if its an attribute element then get the attributes
            if (attr.getNodeName().equals(NODE_ATTRIBUTE)) {
                NamedNodeMap map = attr.getAttributes();
                String attrName = map.getNamedItem(ATTR_NAME).getNodeValue();

                editNode.getAttributes().removeNamedItem(attrName);

            }
        }
    }

    /**
     * Sets the value of attributes.
     *
     * @param editNode   The node where attributes will be edited.
     * @param actionArgs A List of nodes defining attributes and their values.
     */
    private void attributeSet(Node editNode, NodeList actionArgs) {
        for (int k = 0; k < actionArgs.getLength(); k++) {
            Node attr = actionArgs.item(k);

            // if its an attribute element then get the attributes
            if (attr.getNodeName().equals(NODE_ATTRIBUTE)) {
                NamedNodeMap map = attr.getAttributes();

                // create the new attribute
                Attr attribute = editNode.getOwnerDocument()
                        .createAttribute(map.getNamedItem(ATTR_NAME).getNodeValue());
                attribute.setValue(map.getNamedItem(ATTR_VALUE).getNodeValue());

                // set it in the main document
                editNode.getAttributes().setNamedItem(attribute);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            return;
        }

        MergeDocuments merge = new MergeDocuments();
        merge.merge(new File(args[0]), new File(args[1]), args[2]);
    }
}
