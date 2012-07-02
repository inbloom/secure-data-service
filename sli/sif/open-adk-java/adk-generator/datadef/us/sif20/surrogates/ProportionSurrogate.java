package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.common.CommonDTD;
import openadk.util.XMLWriter;

public class ProportionSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	public ProportionSurrogate( ElementDef def )
	{
		super( def );
	}

	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		String xmlValue = o.getSIFValue().toString( formatter ) + "%";
		writeSimpleElement(writer, "Proportion", xmlValue);

	}

	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		if( !reader.getLocalName().equals( "Proportion" ) ){
			return false;
		}

		String value = consumeElementTextValue( reader, version );

		if( value != null && value.length() > 0 ){
			// Strip off the trailing percentage
			if( value.endsWith( "%" ) ){
				value = value.substring( 0, value.length() - 1 );
			}
			ElementDef proportionDef = CommonDTD.RACE_PROPORTION;
			SIFSimpleType proportionValue = null;
			proportionValue = proportionDef.getTypeConverter().parse( formatter, value );
			parent.setField( proportionDef, proportionValue );
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
	 * @see com.edustructures.sifworks.impl.surrogates.RenderSurrogate#getPath()
	 */
	public String getPath() {
		return "Proportion";
	}



}
