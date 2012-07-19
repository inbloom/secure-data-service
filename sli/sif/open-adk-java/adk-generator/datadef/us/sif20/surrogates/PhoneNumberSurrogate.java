package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.common.CommonDTD;
import openadk.library.common.PhoneNumber;
import openadk.library.tools.xpath.SIFElementPointer;
import openadk.util.XMLWriter;

/**
 * RenderSurrogate that renders and parses the SIF 1.5r1 and SIF 1.1 PhoneNumber
 * elements into their SIF 2.0 format
 * @author Andrew
 *
 */
public class PhoneNumberSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {


	public PhoneNumberSurrogate( ElementDef def ){
		super( def );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.surrogates.RenderSurrogate#renderRaw(com.edustructures.util.XMLWriter, com.edustructures.sifworks.SIFVersion, com.edustructures.sifworks.Element, com.edustructures.sifworks.SIFFormatter)
	 */
	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		PhoneNumber pn = (PhoneNumber)o;
		if( pn != null  ){
				writer.tab();
				writer.write( "<PhoneNumber" );
				writer.write(" Format=\"NA\"" );
				String type = pn.getType();
				if( type != null ){
					writer.write(" Type=\"" );
					writer.printXmlText( type );
					writer.write( "\">" );
				}
				String number = pn.getNumber();
				if( number != null ){
					writer.printXmlText( number );
				}
				writer.write( "</PhoneNumber>" );
				writer.write( "\r\n" );

		}

	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.surrogates.RenderSurrogate#readRaw(javax.xml.stream.XMLStreamReader, com.edustructures.sifworks.SIFVersion, com.edustructures.sifworks.SIFElement, com.edustructures.sifworks.SIFFormatter)
	 */
	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		if( !reader.getLocalName().equals( "PhoneNumber" ) ){
			return false;
		}

		PhoneNumber phone = new PhoneNumber();
		String type = reader.getAttributeValue( null, "Type" );
		if( type != null ){
			phone.setType( type );
		}

		String format = reader.getAttributeValue( null, "Format" );
		if( format != null ){
			phone.setFormat( format );
		}
		String number = consumeElementTextValue( reader, version );
		if( number != null ){
			phone.setNumber( number );
		}

		formatter.addChild( parent, phone, version );

		return true;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.surrogates.RenderSurrogate#lookupBySQP(java.lang.String)
	 */
	public ElementDef lookupBySQP(String sqp) {
		if( sqp.length() == 0 ){
			// This query pattern points to the PhoneNumber element itself. The
			// resolved ElementDef should be the PhoneNumber/Number element
			return CommonDTD.PHONENUMBER_NUMBER;
		}
		if( sqp.equals( "@Type" ) ){
			return CommonDTD.PHONENUMBER_TYPE;
		}
		return null;
	}

	public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context ) {
		PhoneNumber phone = new PhoneNumber();
		phone.setField( CommonDTD.PHONENUMBER_NUMBER, new SIFString( null ) );
		SIFElement owner = (SIFElement)parentPointer.getBaseValue();
		formatter.addChild(owner, phone, version );
		return new PhoneNumberPointer( parentPointer, phone, version );
	}



	public NodePointer createNodePointer(NodePointer parent, Element element, SIFVersion version) {
		if( !(element instanceof PhoneNumber) ){
			throw new IllegalArgumentException( "Cannot create NodePointer for Elements other than PhoneNumber" );
		}
		return new PhoneNumberPointer( parent, (PhoneNumber)element, version );
	}

	private class PhoneNumberPointer extends SIFElementPointer
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -4097128904146442941L;

		PhoneNumberPointer(NodePointer parentpointer, SIFElement element, SIFVersion version) {
			super(parentpointer, element, version);
		}

		@Override
		public void setValue(Object value) {
			PhoneNumber phone = (PhoneNumber)getBaseValue();
			phone.setNumber( value.toString() );
		}

		@Override
		public Object getValue() {
			PhoneNumber phone = (PhoneNumber)getBaseValue();
			return phone.getField( CommonDTD.PHONENUMBER_NUMBER );
		}


	}

	public String getPath() {
		return "PhoneNumber";
	}





}
