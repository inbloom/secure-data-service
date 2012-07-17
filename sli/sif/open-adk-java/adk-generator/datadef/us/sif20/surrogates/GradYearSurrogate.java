package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.common.PartialDateType;
import openadk.library.student.StudentDTD;
import openadk.library.tools.xpath.SIFElementPointer;
import openadk.util.XMLWriter;

/**
 * RenderSurrogate that renders and parses the SIF 1.5r1 and SIF 1.1 GradYear
 * elements into their SIF 2.0 format
 * @author Andrew
 *
 */
public class GradYearSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	// TODO: This class would be much more streamlined if we had better support
	// in SIFXPathContext for using SIF 2.0 xpaths while working with a 1.x object

	public GradYearSurrogate( ElementDef def ){
		super( def );
	}

	public void renderRaw(XMLWriter writer, SIFVersion version, Element o,
			SIFFormatter formatter) throws SIFException {
		String type = getTypeAttribute();
		String value = null;
		if( type == "Actual" ){
			PartialDateType gradDate = (PartialDateType)o;
			Integer year = gradDate.getYear();
			if( year == null )
			{
				value = "";
			} else {
				value = String.format( "%04d", year.intValue() );
			}
		} else {
			value = o.getTextValue();
			if( value == null ){
				 // in SIF 1.5, represent null values as ""
				value = "";
			}
		}

		if( type != null && value != null ){
//			try
//			{
				writer.tab();
				writer.write( "<GradYear Type=\"" );
				writer.write( type );
				writer.write( "\">" );
				writer.write( value );
				writer.write( "</GradYear>" );
				writer.write( "\r\n" );
			//}
//			catch( XMLStreamException xse )
//			{
//				throwSIFException( xse, "", version );
//			}
		}

	}

	private String getTypeAttribute()
	{
		if( fElementDef.name().equals( "ProjectedGraduationYear" ) ){
			return "Projected";
		} else if ( fElementDef.name().equals( "OnTimeGraduationYear" ) ){
			return "Original";
		} else if ( fElementDef.name().equals( "PartialDateType" ) ){
			return "Actual";
		}
		return "";
	}

	public boolean readRaw(XMLStreamReader reader, SIFVersion version,
			SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		if( !reader.getLocalName().equals( "GradYear" ) ){
			return false;
		}

		//
		// NOTE:
		// GradYear can appear on both StudentPersonal and StudentSnapshot
		//
		String type = reader.getAttributeValue( null, "Type" );
		String value = consumeElementTextValue( reader, version );

		ElementDef childDef = null;
		if( type != null && value != null )
		{

			SIFInt intValue = (SIFInt)SIFTypeConverters.INT.parse( formatter, value );
			Integer year = intValue.getValue();
			if( "Projected".equals( type ) ){
				childDef = ADK.DTD().lookupElementDef( parent.getElementDef(), "ProjectedGraduationYear" );
				parent.setField( childDef, intValue );
			} else if( "Original".equals( type ) ){
				childDef = ADK.DTD().lookupElementDef( parent.getElementDef(), "OnTimeGraduationYear" );
				parent.setField( childDef, intValue );
			} else {
				childDef = ADK.DTD().lookupElementDef( parent.getElementDef(), "GraduationDate" );
				PartialDateType gd = new PartialDateType( year );
				parent.addChild( childDef, gd );
			}

		}
		return true;
	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#createChild(org.apache.commons.jxpath.ri.model.NodePointer, openadk.library.SIFFormatter, openadk.library.SIFVersion, org.apache.commons.jxpath.JXPathContext)
	 */
	public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context ) {
		return new GradYearNodePointer( parentPointer );
	}

	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#getPath()
	 */
	public String getPath() {
		return "GradYear[@Type='" + getTypeAttribute() + "']";
	}


	/* (non-Javadoc)
	 * @see openadk.library.impl.surrogates.RenderSurrogate#createNodePointer(org.apache.commons.jxpath.ri.model.NodePointer, openadk.library.Element, openadk.library.SIFVersion)
	 */
	public NodePointer createNodePointer(NodePointer parent, Element element, SIFVersion version) {
		ElementDef def = element.getElementDef();
		if( def.name().equals( StudentDTD.STUDENTPERSONAL_PROJECTEDGRADUATIONYEAR.name() ) ){
			return new GradYearNodePointer(parent, element, "Projected" );
		} else 	if( def.name().equals( StudentDTD.STUDENTPERSONAL_ONTIMEGRADUATIONYEAR.name() ) ){
			return new GradYearNodePointer(parent, element, "Original" );
		} else 	if( def.name().equals( StudentDTD.STUDENTPERSONAL_GRADUATIONDATE.name() ) ){
			return new GradYearNodePointer(parent, element, "Actual" );
		}
		return null;
	}


	/**
	 * Implements a node pointer that represents a GradYear element. This node
	 * pointer is created based on a legacy XPath, such as GradYear[@Type='Projected'].
	 * The actual node this class points to, however, would be the field on StudentPersonal
	 * or StudentSnapshot that represents the Projected Grad Year. For StudentPersonal,
	 * that field is the OnTimeGraduationYear element.
	 * @author Andrew Elmhorst
	 *
	 */
	class GradYearNodePointer extends SurrogateElementPointer<Element>
	{
		/**
		 *
		 */
		private static final long serialVersionUID = 8441111097610733968L;
		/**
		 * The string that represents the GradYear/@Type value of this element
		 * in SIF 1.x versions
		 */
		private String fAttrValue;

		protected GradYearNodePointer(NodePointer parentPointer ) {
			super(parentPointer, "GradYear" );
		}

		protected GradYearNodePointer(NodePointer parentPointer, Element field, String attrValue ) {
			super(parentPointer, "GradYear", field );
			fAttrValue = attrValue;
		}

		@Override
		public NodeIterator attributeIterator(QName qname) {
			if( qname.getName().equals( "Type" ) ){
				return new FauxAttribute( this, "Type", fAttrValue  );
			}
			return null;
		}

		@Override
		public NodePointer createAttribute(JXPathContext context, QName name) {
			return new GradYearTypePointer( this );
		}

		@Override
		public boolean isLeaf() {
			return true;
		}


		@Override
		public void setValue(Object value) {

			SIFSimpleType<Integer> sifValue = null;
			if( value instanceof SIFInt ){
				sifValue = (SIFInt)value;
			} else {
				sifValue = SIFTypeConverters.INT.getSIFSimpleType( value );
			}
			Element e = getElement();
			if( e instanceof PartialDateType ){
				((PartialDateType)e).setYear( sifValue.getValue() );
			} else {
				e.setSIFValue( sifValue );
			}
		}

		@SuppressWarnings("unchecked")
		void setGradYearType( String type ){
			fAttrValue = type;
			SIFElementPointer parentPointer = (SIFElementPointer)getParent();
			SIFElement sp = (SIFElement)parentPointer.getBaseValue();
			SIFSimpleType nullValue = SIFTypeConverters.INT.getSIFSimpleType( null );
			Element field = null;

			if( type.equals( "Projected" ) ){
				field = sp.getField( "ProjectedGraduationYear" );
				if( field == null ){
					ElementDef childDef = ADK.DTD().lookupElementDef( sp.getElementDef(), "ProjectedGraduationYear" );
					field = sp.setField( childDef, nullValue );
				}
			} else if( type.equals( "Original" ) ){
				field = sp.getField( "OnTimeGraduationYear" );
				if( field == null ){
					ElementDef childDef = ADK.DTD().lookupElementDef( sp.getElementDef(), "OnTimeGraduationYear" );
					field = sp.setField( childDef, nullValue );
				}
			} else {
				PartialDateType gd = new PartialDateType( (String)null );
				ElementDef childDef = ADK.DTD().lookupElementDef( sp.getElementDef(), "GraduationDate" );
				sp.addChild( childDef, gd );
				field = gd;
			}
			setElement( field );
		}

		/**
		 * Represents a temporary in-memory pointer to the @Type attribute
		 * of the GradYear element. This element does not exist in the ADK,
		 * but a pointer to it is allowed, since the ADK supports SIF 1.x
		 * XPaths.
		 * @author Andy Elmhorst
		 *
		 */
		class GradYearTypePointer extends FauxElementPointer {

			/**
			 *
			 */
			private static final long serialVersionUID = 4550174490811773922L;

			protected GradYearTypePointer(NodePointer parentPointer) {
				super(parentPointer, "Type");
			}

			@Override
			public boolean isLeaf() {
				return true;
			}

			@Override
			public Object getBaseValue() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getImmediateNode() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setValue(Object value) {
				setGradYearType( value.toString() );
			}
		}
	}
}
