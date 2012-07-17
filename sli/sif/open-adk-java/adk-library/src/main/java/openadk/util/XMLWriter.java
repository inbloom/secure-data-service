//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import java.io.InputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.w3c.dom.traversal.NodeFilter;

/**
 * A PrintWriter for rendering xml streams
 * 
 * @author Eric Petersen
 * @version 1.0
 */
public class XMLWriter extends PrintWriter {
	int fIndent = 0;

	int fPauseTab = 0;

	public XMLWriter(OutputStream out) {
		super( out );
	}

	public XMLWriter(OutputStream out, boolean autoFlush) {
		super( out, autoFlush );
	}

	public XMLWriter(Writer out) {
		super( out );
	}

	public XMLWriter(Writer out, boolean autoFlush) {
		super( out, autoFlush );
	}

	/**
	 * Increases the indentation by the specified number of tabs. The next call
	 * the <code>tab</code> will write the proper number of intentation spaces
	 * to the output stream unless tabbing has been paused.
	 */
	public void indent(int tabs) {
		fIndent += tabs;
		if ( fIndent < 0 )
			fIndent = 0;
	}

	/**
	 * Increases the number of pauses to indentation. For each call to this
	 * function, the <code>tab</code> will do nothing.
	 */
	public void pauseTab() {
		fPauseTab++;
	}

	/**
	 * Writes the proper number of intentation spaces to the output stream
	 * unless tabbing has been paused.
	 */
	public void tab() {
		if ( fPauseTab > 0 )
			fPauseTab--;
		else
			for ( int i = 0; i < fIndent * 2; i++ )
				print( ' ' );
	}

	public String getAttrString(Dictionary attrs) {
		if ( attrs != null ) {
			StringBuilder a = new StringBuilder();
			String key = null;
			for ( Enumeration e = attrs.keys(); e.hasMoreElements(); ) {
				key = (String) e.nextElement();
				a.append( key );
				a.append( "=\"" );
				a.append( attrs.get( key ) );
				a.append( '\"' );
			}

			return a.toString();
		}

		return "";
	}

	/**
	 * Encodes the given string for XML and then prints it
	 * 
	 * @param xmlText
	 *            the string that will be placed in an element or attribute text
	 */
	public void printXmlText(String xmlText) {
		print( xmlEncode( xmlText ) );
	}

	/**
	 * Writes an XML DOM Element to the underlying Stream
	 * @param element
	 */
	public void write(org.w3c.dom.Element element) {

//		try
//		{
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			Transformer transformer = transformerFactory.newTransformer();
//			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
//			transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
//			transformer.transform( new DOMSource( element ), new StreamResult( this ) );
//		}
//		catch( Exception ex ){
//			throw new RuntimeException( "Unable to write XML Element: " + ex.getMessage(), ex );
//		}
		
		// For the fun of it, try writing it out
		try {
			DOMImplementation implementation = DOMImplementationRegistry.newInstance().getDOMImplementation( "XML 3.0" );
			DOMImplementationLS feature = (DOMImplementationLS) implementation.getFeature( "LS", "3.0" );
			LSSerializer serializer = feature.createLSSerializer();
			serializer.getDomConfig().setParameter("xml-declaration", false);
			String serializedXml = serializer.writeToString(element);
			// This is ugly but I can't see a better way to control this output here without writing our own serializer
			// The point is to remove redundant namespace declarations, but leave any others intact.
			if ("SIF_ExtendedElement".equals(element.getLocalName())) {
				serializedXml = serializedXml.replaceFirst(" xmlns=['\"]" + element.getNamespaceURI() + "['\"]", "");
				serializedXml = serializedXml.replaceFirst(" xmlns:xsi=['\"]http://www.w3.org/2001/XMLSchema-instance['\"]", "");
			}
			println(serializedXml);
		} catch ( Exception e ) {
			System.err.println( e );
		}
	}
	
	/**
	 * Writes an XML DOM Document to the underlying stream
	 * @param document
	 */
	public void write( org.w3c.dom.Document document ){
		if( document != null ){
			write( document.getDocumentElement() );
		}
	}

	/**
	 * Encodes the given string for XML
	 * 
	 * @param text
	 *            the string that will be placed in an element or attribute text
	 * @return An xml encoded string or "" if null
	 */
	public String xmlEncode(String text) {
		if ( text == null ) {
			return "";
		} else {
			return ADKStringUtils.encodeXML( text );
		}
	}

}
