package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.programs.ServiceSetting;
import openadk.util.XMLWriter;

/**
 * Provides rendering in SIF 1.5r1 for the ServiceLocation element, which was changed
 * to ServiceSetting/Code in SIF 2.0
 * @author Andrew
 *
 */
public class ServiceLocationSurrogate extends AbstractRenderSurrogate implements
		RenderSurrogate {

	public ServiceLocationSurrogate( ElementDef def ){
		super( def );
	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#renderRaw(com.edustructures.util.XMLWriter, openadk.library.SIFVersion, openadk.library.Element, openadk.library.SIFFormatter)
	 */
	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		ServiceSetting setting = (ServiceSetting)o;
		if( setting != null  ){
				writer.tab();
				writer.write( "<ServiceLocation" );
				// CodeType attribute
				String codeType = setting.getCodeType();
				if( codeType == null ){
					codeType = "NCES";
				}
				writer.write(" CodeType=\"" );
				writer.write( codeType );
				writer.write( '"' );


				writer.write( ">" );

				String code = setting.getCode();
				if( code != null ){
					writer.write( code );
				}
				writer.write( "</ServiceLocation>" );
				writer.write( "\r\n" );

		}

	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#readRaw(javax.xml.stream.XMLStreamReader, openadk.library.SIFVersion, openadk.library.SIFElement, openadk.library.SIFFormatter)
	 */
	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		if( !reader.getLocalName().equals( "ServiceLocation" ) ){
			return false;
		}

		ServiceSetting setting = new ServiceSetting();

		String codeType = reader.getAttributeValue( null, "CodeType" );
		if( codeType != null ){
			setting.setCodeType( codeType );
		}


		String codeValue = consumeElementTextValue( reader, version );
		if( codeValue != null ){
			setting.setCode( codeValue );
		}

		formatter.addChild( parent, setting, version );

		return true;
	}

	public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodePointer createNodePointer(NodePointer parentPointer, Element element, SIFVersion version) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() {
		return "ServiceLocation";
	}


}


