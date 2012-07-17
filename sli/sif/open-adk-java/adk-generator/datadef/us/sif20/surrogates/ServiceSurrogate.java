package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.common.Service;
import openadk.util.XMLWriter;

/**
 * Provides a RenderSurrogate for the Programs/Service element, which changed shape in SIF 2.0
 * @author Andrew
 *
 */
public class ServiceSurrogate extends AbstractRenderSurrogate implements
		RenderSurrogate {

	public ServiceSurrogate( ElementDef def ){
		super( def );
	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#renderRaw(com.edustructures.util.XMLWriter, openadk.library.SIFVersion, openadk.library.Element, openadk.library.SIFFormatter)
	 */
	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {

		Service service = (Service)o;
		if( service != null  ){
				writer.tab();
				writer.write( "<Service" );
				// CodeType attribute
				String codeType = service.getCodeType();
				if( codeType == null ){
					codeType = "NCES";
				}
				writer.write(" CodeType=\"" );
				writer.write( codeType );
				writer.write( '"' );

				// Type attribute
				String type = service.getType();
				if( type == null ){
					type = "Other";
				}
				writer.write(" Type=\"" );
				writer.write( type );
				writer.write( '"' );

				writer.write( ">" );

				String code = service.getCode();
				if( code != null ){
					writer.write( code );
				}
				writer.write( "</Service>" );
				writer.write( "\r\n" );

		}

	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#readRaw(javax.xml.stream.XMLStreamReader, openadk.library.SIFVersion, openadk.library.SIFElement, openadk.library.SIFFormatter)
	 */
	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		if( !reader.getLocalName().equals( "Service" ) ){
			return false;
		}

		Service service = new Service();

		String codeType = reader.getAttributeValue( null, "CodeType" );
		if( codeType != null ){
			service.setCodeType( codeType );
		}

		String type = reader.getAttributeValue( null, "Type" );
		if( type != null ){
			service.setType( type );
		}


		String codeValue = consumeElementTextValue( reader, version );
		if( codeValue != null ){
			service.setCode( codeValue );
		}

		formatter.addChild( parent, service, version );

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
		return "Service";
	}


}
