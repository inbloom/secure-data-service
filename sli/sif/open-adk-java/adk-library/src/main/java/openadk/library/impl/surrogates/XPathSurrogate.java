//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import openadk.library.*;
import openadk.util.XMLWriter;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * Implements a flexible Rendering surrogate based on a proprietary XPath syntax
 * @author Andrew
 *
 */
public class XPathSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	private String fLegacyXpath;
	private String fValueXpath;

	public XPathSurrogate( ElementDef def, String xpathMacro )
	{
		super( def );
		String[] macroParts = xpathMacro.split( "=" );
		fLegacyXpath = macroParts[0];
		fValueXpath = macroParts[1];
	}

	public void renderRaw(
			XMLWriter writer,
			SIFVersion version,
			Element o,
			SIFFormatter formatter)
	{

		// Read the value out of the source object
		Element valueElement = findReferencedElement( o );
		if( valueElement == null ){
			return;
		}
		SIFSimpleType value = valueElement.getSIFValue();
		if( value == null || value.getValue() == null ){
			return;
		}

		String[] xPathParts = fLegacyXpath.split( "/" );
		int currentSegment = 0;
		// Build the path
		while( currentSegment < xPathParts.length - 1 )
		{
			writer.tab();
			writer.write( '<' );
			writer.write( xPathParts[ currentSegment ] );
			if( !xPathParts[currentSegment+1].startsWith( "@" ) ){
				writer.write( '>' );
			}
			currentSegment++;
		}

		String finalSegment = xPathParts[currentSegment];
		if( finalSegment.startsWith( "@" ) ){
			writer.write( ' ' );
			writer.write( finalSegment.substring( 1 ) );
			writer.write( "=\"" );
			writer.printXmlText( value.toString( formatter ) );
			writer.write( "\" />\r\n" );
			currentSegment--;
		} else {
			// Note: finalSegment can be equal to ".", which
			// signals to render the text only
			if( finalSegment.length() > 1 )
			{
				writer.tab();
				writer.write( '<' );
				writer.write( finalSegment );
				writer.write( '>' );
			}
			writer.printXmlText( value.toString( formatter ) );
			if( finalSegment.length() > 1 )
			{
				writer.write( "</" );
				writer.write( finalSegment );
				writer.write( ">\r\n" );
			}
		}

		currentSegment--;
		// unwind the path
		while( currentSegment > -1 )
		{
			writer.write( "</" );
			writer.write( xPathParts[ currentSegment ] );
			writer.write( ">\r\n" );
			currentSegment--;
		}
	}


	public boolean readRaw(
			XMLStreamReader reader,
			SIFVersion version,
			SIFElement parent,
			SIFFormatter formatter )
		throws ADKParsingException
	{


		String value = null;
		//
		// STEP 1
		// Determine if this surrogate can handle the parsing of this node.
		// Retrieve the node value as a string
		//

		String[] xPathParts = fLegacyXpath.split( "/" );
		int eventType = reader.getEventType();
		String localName = reader.getLocalName();
		if ( eventType == XMLStreamConstants.START_ELEMENT &&
				localName.equals( xPathParts[0] ) )
		{
			try
			{
				int currentSegment = 0;
				int lastSegment = xPathParts.length - 1;
				if( xPathParts[lastSegment].startsWith( "@" ) ){
					lastSegment--;
				}
				while( currentSegment < lastSegment )
				{
					reader.nextTag();
					currentSegment++;

					if( !reader.getLocalName().equals( xPathParts[currentSegment] ) ){
						throwParseException("Element {" + reader.getLocalName() + "} is not supported by XPathSurrogate path " + fLegacyXpath, version );
					}
				}

				// New we are at the last segment in the XPath, and the XMLStreamReader
				// should be positioned on the proper node. The last segment is either
				// an attribute or an element, which need to be read differently
				String finalSegment = xPathParts[ xPathParts.length - 1 ];
				if( finalSegment.startsWith( "@" ) ){
					value = reader.getAttributeValue( null, finalSegment.substring( 1 ) );
					// Advance the cursor
					reader.next();
				} else {
					value = readElementTextValue( reader );
				}
				super.nextTag( reader );
			}
			catch( XMLStreamException xse )
			{
				throwParseException( xse, reader.getLocalName(), version );
			}
		} else {
			// No match was found
			return false;
		}

		//
		// STEP 2
		// Find the actual field to set the value to
		//
		ElementDef fieldDef = null;
		SIFElement targetElement = parent;
		if(fValueXpath.equals( "." ) && fElementDef.isField() ){
			fieldDef = fElementDef;
		} else {
			//	This indicates a child SIFElement that needs to be created
			try
			{
				targetElement = SIFElement.create( parent, fElementDef );
			} catch (ADKSchemaException adkse ){
				throwParseException( adkse, reader.getLocalName(), version );
			}

			formatter.addChild( parent, targetElement, version );

			if( fValueXpath.equals( "." ) ){
				fieldDef = fElementDef;
			} else {
				String fieldName = fValueXpath;
				if( fValueXpath.startsWith( "@" ) ){
					fieldName = fValueXpath.substring( 1 );
				}
				fieldDef = ADK.DTD().lookupElementDef( fElementDef, fieldName );
			}
		}

		if( fieldDef == null ) {
			throw new RuntimeException("Support for value path {" + fValueXpath + "} is not supported by XPathSurrogate." );
		}


		//
		// STEP 3
		// Set the value to the field
		//
		SIFTypeConverter converter = fieldDef.getTypeConverter();
		if( converter == null ){
			// TODO: Determine if we should be automatically creating a converter
			// for elementDefs that don't have one, or whether we should just throw the
			// spurious data away.
			converter = SIFTypeConverters.STRING;
		}
		SIFSimpleType data = converter.parse( formatter, value );
		targetElement.setField( fieldDef, data );

		return true;
	}

	@Override
	public String toString() {
		return "XPathSurrogate{" + fLegacyXpath + "=" + fValueXpath + "}";
	}

	/**
	 * Creates the Child as the result of an XPath Expression and returns the
	 * NodePointer wrapping the child
	 */
	public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context ) {
		// 1) Create an instance of the SimpleField with a null value (It's assigned later)

		//
		// STEP 2
		// Find the actual field to set the value to
		//
		SIFElement parent = (SIFElement)parentPointer.getNode();
		SIFElement targetElement = parent;

		if( !fElementDef.isField() ){
			//	This indicates a child SIFElement that needs to be created
			try
			{
				targetElement = SIFElement.create( parent, fElementDef );
			} catch (ADKSchemaException adkse ){
				throw new JXPathException( "Unable to create " + parent.getElementDef().name() + "/" +
						fElementDef.name() );
			}

			formatter.addChild( parent, targetElement, version );
		}

		ElementDef fieldDef = null;
		if( fValueXpath.equals( "." ) ){
			fieldDef = fElementDef;
		} else {
			String fieldName = fValueXpath;
			if( fValueXpath.startsWith( "@" ) ){
				fieldName = fValueXpath.substring( 1 );
			}
			fieldDef = ADK.DTD().lookupElementDef( fElementDef, fieldName );
		}


		if( fieldDef == null ) {
			throw new JXPathException("Support for value path {" + fValueXpath + "} is not supported by XPathSurrogate." );
		}

		SIFSimpleType ssf = fieldDef.getTypeConverter().getSIFSimpleType(
				null);
		SimpleField sf = ssf.createField(targetElement, fieldDef);
		targetElement.setField(sf);



		// 2) built out a fake set of node pointers representing the SIF 1.5r1 path and
		//    return the root pointer from that stack
		return buildLegacyPointers( parentPointer, sf );

	}

	/**
	 * Creates a NodePointer that "wraps" the specified ADK 2.0 element, but appears to JXPath
	 * as if it uses the SIF 1.5 path.
	 *
	 * XPathSurrogates always represent a surrogate around an ADK 2.0 Simple Field
	 *
	 */
	public NodePointer createNodePointer(NodePointer parent, Element sourceElement, SIFVersion version) {
		// 1) Find the field referenced by the XPathSurrogate expression
		//    If it doesn't exist, return null
		Element referencedField = findReferencedElement( sourceElement );
		if( referencedField == null ){
			return null;
		}
		// 2) If it does exist, build out a fake set of node pointers representing the
		//    SIF 1.5r1 path and return the root pointer.
		return buildLegacyPointers( parent, referencedField );

	}

	/**
	 * Builds a set of nodes to re-create a SIF 1.5 legacy path
	 * @param referencedField
	 * @return
	 */
	private NodePointer buildLegacyPointers( NodePointer parent, Element referencedField) {
		String[] xPathParts = fLegacyXpath.split( "/" );
		int currentSegment = 0;
		NodePointer root = null;
		NodePointer currentParent = parent;

		// Build the path
		while( currentSegment < xPathParts.length - 1 )
		{
			FauxSIFElementPointer pointer = new FauxSIFElementPointer( currentParent, xPathParts[currentSegment] );
			if( currentParent != null && currentParent instanceof FauxSIFElementPointer ){
				((FauxSIFElementPointer)currentParent).setChild( pointer, xPathParts[currentSegment], false );
			}
			currentParent = pointer;
			if( root == null ){
				root = pointer;
			}
			currentSegment++;
		}

		String finalSegment = xPathParts[currentSegment];
		boolean isAttribute = false;
		if( finalSegment.startsWith( "@" ) ){
			// This is an attribute
			finalSegment = finalSegment.substring( 1 );
			isAttribute = true;
		}

		SurrogateSimpleFieldPointer fieldPointer = new SurrogateSimpleFieldPointer(currentParent, finalSegment, referencedField );
		if( currentParent != null && currentParent instanceof FauxSIFElementPointer ){
			((FauxSIFElementPointer)currentParent).setChild( fieldPointer, finalSegment, isAttribute );
		}
		if( root == null ){
			root = fieldPointer;
		}

		return root;

	}

	/**
	 * Finds the element referenced by the expression after the '=' sign in
	 * the constructor for XPathSurrogate. This path represents the XPath from
	 * the element being wrapped to the actual field it represents.
	 * @param startOfPath
	 * @return
	 */
	private Element findReferencedElement( Element startOfPath ) {
		//	Read the value out of the source object
		if( fValueXpath.startsWith( "." ) ){
			return startOfPath;
		} else {
			ElementDef valueDef = null;
			if( startOfPath instanceof SIFElement ){
				valueDef = ADK.DTD().lookupElementDefBySQP( startOfPath.getElementDef(), fValueXpath );
			}
			if( valueDef == null ){
				throw new RuntimeException("Support for value path {" + fValueXpath + "} is not supported by XPathSurrogate." );
			}
			SimpleField field = ((SIFElement)startOfPath).getField( valueDef );
			return field;
		}
	}

	public String getPath() {
		return fLegacyXpath;
	}




}
