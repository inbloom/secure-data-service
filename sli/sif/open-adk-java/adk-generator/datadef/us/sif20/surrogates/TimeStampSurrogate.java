package openadk.library.impl.surrogates;

import java.util.Calendar;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.infra.InfraDTD;
import openadk.util.XMLWriter;

/**
 * Surrogate for rendering or reading SIF 1.x Date and Time fields into the
 * new SIF 2.0 DateTime datatype
 * @author Andrew Elmhorst
 *
 */
public class TimeStampSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	private String fDateElement = "Date";
	private String fTimeElement = "Time";

	public TimeStampSurrogate( ElementDef def )
	{
		super( def );
		if( def == InfraDTD.SIF_HEADER_SIF_TIMESTAMP ){
			fDateElement = "SIF_Date";
			fTimeElement = "SIF_Time";
		}
	}

	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		Calendar timeStamp = (Calendar)o.getSIFValue().getValue();
		if( timeStamp != null ){
			String xmlDate = formatter.toDateString( timeStamp );
			writeSimpleElement( writer, fDateElement, xmlDate );
			SIFTimeSurrogate.writeSIFTime( writer, formatter, fTimeElement, timeStamp );
		}
	}

	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		if( !reader.getLocalName().equals( fDateElement ) )
		{
			return false;
		}

		String dateValue = consumeElementTextValue( reader, version );
		Calendar timeStamp = formatter.toDate( dateValue );
		if( timeStamp != null ){
			if( reader.getLocalName().equals( fTimeElement ) )
			{
				String timeValue = consumeElementTextValue( reader, version );
				Calendar time = formatter.toTime( timeValue );
				if( time != null ){
					timeStamp.set( Calendar.HOUR_OF_DAY, time.get( Calendar.HOUR_OF_DAY ) );
					timeStamp.set( Calendar.MINUTE, time.get( Calendar.MINUTE ) );
					timeStamp.set( Calendar.SECOND, time.get( Calendar.SECOND ) );
				}
			}
			SIFDateTime dateTime = new SIFDateTime( timeStamp );
			parent.setField( dateTime.createField( parent, fElementDef ) );
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

	public String getPath() {
		return "SIF_Date";
	}



}
