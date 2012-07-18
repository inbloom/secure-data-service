//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class XMLStreamDocumentBuilder {

	/**
	 * Creates a DOM Document from the specified XMLStreamReader
	 * @param reader The XMLStreamReader to read from
	 * @param namespaceAware True if the XMLStreamReader is resolving namespaces
	 * @param rootElementName (Optional) The name of the root Document element to create. If null, the XMLStreamReader
	 * is expected to be positioned on a document element.
	 * @return The DOM document that has been parsed using the XMLStreamReader
	 * @throws XMLStreamException
	 */
	public static Document build(XMLStreamReader reader, boolean namespaceAware, boolean ignoreWhitespace, String rootElementName )
		throws XMLStreamException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try
		{
			dom = dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException pce ){
			// rethrow
			throw new RuntimeException( "Error loading DocumentBuilderFactory: " + pce.getMessage(), pce );
		}
		
		Node rootElement = dom;
		if( rootElementName != null ){
			rootElement = dom.createElement(  rootElementName );
			dom.appendChild( rootElement );
		}
		
		Node currentElement = rootElement;
		
		while( reader.hasNext() ){
			int nodeType = reader.next();
			 Node parsedChild = null;

	            switch (nodeType) {
	            case XMLStreamConstants.SPACE:
	            case XMLStreamConstants.ENTITY_DECLARATION:
	            case XMLStreamConstants.NOTATION_DECLARATION:
	            case XMLStreamConstants.START_DOCUMENT:
	            case XMLStreamConstants.DTD:
	                continue;

	            case XMLStreamConstants.ENTITY_REFERENCE:
	                parsedChild = dom.createEntityReference( reader.getLocalName());
	                break;

	            case XMLStreamConstants.PROCESSING_INSTRUCTION:
	                parsedChild = dom.createProcessingInstruction( reader.getPITarget(), reader.getPIData());
	                break;

	            case XMLStreamConstants.START_ELEMENT:
	                {
	                    String name = reader.getLocalName();
	                    Element newElem;
	                    if ( namespaceAware ) {
	                            newElem = dom.createElementNS(reader.getNamespaceURI(), name);
	                    } else { 
	                        newElem = dom.createElement( name );
	                    }

	                    // Add attributes....
	                   for (int i = 0, len = reader.getAttributeCount(); i < len; ++i) {
	                        String attrName = reader.getAttributeLocalName(i);
	                        if (namespaceAware) {
	                            Attr attr = dom.createAttributeNS(reader.getAttributeNamespace(i), attrName);
	                            attr.setValue(reader.getAttributeValue(i));
	                            newElem.setAttributeNodeNS(attr);
	                        } else {
	                            Attr attr = dom.createAttribute(attrName);
	                            attr.setValue( reader.getAttributeValue(i));
	                            newElem.setAttributeNode(attr);
	                        }
	                    }
	                    // And then 'push' new element...
	                    currentElement.appendChild(newElem);
	                    currentElement = newElem;
	                }
	                break;
	                
	            case XMLStreamConstants.CHARACTERS:
	            	if( ignoreWhitespace && reader.isWhiteSpace() ){
	            		break;
	            	}
	                parsedChild = dom.createTextNode(reader.getText());
	                break;

	            case XMLStreamConstants.COMMENT:
	                parsedChild = dom.createComment(reader.getText());
	                break;
	                
	            case XMLStreamConstants.CDATA:
	                parsedChild = dom.createCDATASection( reader.getText());
	                break;
	                
	            case XMLStreamConstants.END_DOCUMENT:
	            	break;

	            case XMLStreamConstants.END_ELEMENT:
	                currentElement = currentElement.getParentNode();
	                if (currentElement == null) {
	                	currentElement = rootElement;
	                }
	                continue;
	  
	            default:
	                throw new XMLStreamException( "Unable to process event type:  " + reader.getEventType() );
	            }

	            if (parsedChild != null) {
	                currentElement.appendChild(parsedChild);
	            }
		}
		
		return dom;
		
	}

}
