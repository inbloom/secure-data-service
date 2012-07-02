package openadk.library.impl.surrogates;

import java.util.Calendar;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.util.XMLWriter;

public class SIFTimeSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	public SIFTimeSurrogate( ElementDef def )
	{
		super( def );
	}

	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		String elementName = fElementDef.name();
		SIFTime time = (SIFTime)o.getSIFValue();
		if( time.getValue() != null ){
			writeSIFTime( writer, formatter, elementName, time.getValue() );
		}
	}

	public static void writeSIFTime(
			XMLWriter writer, SIFFormatter formatter,
			String elementName, Calendar time )
	{
		String xmlTime = formatter.toTimeString( time );
		writer.tab();
		writer.write( '<' );
		writer.write( elementName );
		// TODO: Write the proper time zone out
		writer.write( " Zone=\"UTC-06:00\"" );
		writer.write( '>' );
		writer.write( xmlTime );
		writer.write( "</" );
		writer.write( elementName );
		writer.write( ">\r\n" );
	}

	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		String elementName = fElementDef.name();
		if( !reader.getLocalName().equals( elementName ) ){
			return false;
		}

		String value = consumeElementTextValue( reader, version );

		if( value != null && value.length() > 0 ){
			Calendar time = formatter.toTime( value );
			SIFTime sifTime = new SIFTime( time );
			parent.setField( sifTime.createField( parent, fElementDef ) );
		}
		return true;
	}

	public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context ) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodePointer createNodePointer(NodePointer parent, Element element, SIFVersion version) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#getPath()
	 */
	public String getPath() {
		return "SIF_Time";
	}



}
