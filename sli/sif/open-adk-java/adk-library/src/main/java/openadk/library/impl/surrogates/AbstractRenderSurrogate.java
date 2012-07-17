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




/**
 * The base class for many of the rendering surrogates used by the ADK to parse elements from
 * prior versions.
 *
 */
public class AbstractRenderSurrogate {

	protected ElementDef fElementDef;


	/**
	 * Creates a new instance of the AbstractRenderSurrogate
	 * @param def The ElementDef that this surrogate is responsible for
	 */
	public AbstractRenderSurrogate( ElementDef def )
	{
		fElementDef = def;
	}

	/**
	 * Positions the cursor after the end of the current XMLElement
	 * @param reader
	 * @param version
	 * @throws SIFException
	 */
	protected void nextTag( XMLStreamReader reader )
		throws ADKParsingException
	{
		try
		{
			// Advance to the end of the current element
			while( reader.getEventType() != XMLStreamReader.END_ELEMENT ){
				reader.nextTag();
			}
			// Advance one step further
			reader.nextTag();
		}
		catch( XMLStreamException xse )
		{
			throwParseException( xse, reader.getLocalName(), SIFVersion.SIF15r1 );
		}
	}

	/**
	 * Reads the value from the current XMLElement using the
	 * XMLStreamReader and completely consumes the element, positioning the
	 * cursor on the next tag
	 * @param reader
	 * @param version
	 * @return The inner text value of the current XML Element
	 * @throws SIFException
	 */
	protected String consumeElementTextValue( XMLStreamReader reader, SIFVersion version )
		throws ADKParsingException
	{
		String value = null;
		try
		{
			value = readElementTextValue( reader );
			// Consume the rest of this element
			reader.nextTag();
		}
		catch( XMLStreamException xse )
		{
			throwParseException( xse, reader.getLocalName(), version );
		}
		return value;
	}

	/**
	 * Consumes the text value of an element and returns with the
	 * XMLStreamReader positioned on the EndElement
	 * @param reader
	 * @return The inner text value of the current XML Element
	 * @throws XMLStreamException
	 */
	protected String readElementTextValue( XMLStreamReader reader )
		throws XMLStreamException
	{
		String value = null;
		int nodeType = reader.getEventType();
		while( nodeType == XMLStreamConstants.START_ELEMENT )
		{
			// If the element exists, return an empty string, rather than null
			value = "";
			nodeType = reader.next();
		}
		if( nodeType == XMLStreamConstants.CHARACTERS )
		{
			StringBuilder buf = new StringBuilder();
			while( nodeType == XMLStreamConstants.CHARACTERS ){
				buf.append( reader.getText() );
				nodeType = reader.next();
			}
			value = buf.toString();
		}
		return value;
	}


	protected void throwParseException( Exception ex, String elementDefName, SIFVersion version )
	throws ADKParsingException
	{
		throw new ADKParsingException
	    (  "Unable to parse element or attribute '" + elementDefName + "' :" +
	      ( ex == null ? "" : ex.getMessage() ) + " (SIF " + version.toString() + ")", null, ex );
	}

	protected void throwParseException( String errorMessage, SIFVersion version )
	throws ADKParsingException
	{
		throw new ADKParsingException
	    (  errorMessage + " (SIF " + version.toString() + ")", null );
	}

	protected void writeSimpleElement(XMLWriter writer, String elementName, String xmlValue) {
		writer.tab();
		writer.write( '<' );
		writer.write( elementName );
		writer.write( '>' );
		writer.write( xmlValue  );
		writer.write( "</" );
		writer.write( elementName );
		writer.write( ">\r\n" );
	}


}
