package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.common.SIF_RefId;
import openadk.library.reporting.ReportingDTD;
import openadk.util.XMLWriter;

/**
 * Reads or writes the SIF 1.5r1 SifEntity complex element into or from the SIF 2.x
 * SIF_RefId element. This is only used in the ReportSubmitterInfo object
 * @author Andrew Elmhorst
 *
 */
public class SifEntitySurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	public SifEntitySurrogate( ElementDef def ){
		super( def );
	}

	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		SIF_RefId refIdElement = (SIF_RefId) o;
		writer.tab();
		writer.write( "<SifEntity ObjectName=\"" );
		writer.write( refIdElement.getSIF_RefObject() );
		writer.write( "\" RefId=\"" );
		writer.write( refIdElement.getValue() );
		writer.write( "\" />\r\n" );

	}

	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {
		if( !reader.getLocalName().equals( "SifEntity" ) ){
			return false;
		}
		SIF_RefId refIdElement = new SIF_RefId();
		refIdElement.setSIF_RefObject( reader.getAttributeValue( null, "ObjectName" ) );
		refIdElement.setValue( reader.getAttributeValue( null, "RefId" ) );
		parent.addChild( ReportingDTD.REPORTSUBMITTERINFO_SIF_REFID, refIdElement );
		super.nextTag( reader );
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
		return "SifEntity";
	}



}
