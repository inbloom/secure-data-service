//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.*;

import openadk.library.common.SIF_ExtendedElement;
import openadk.library.common.XMLData;
import openadk.library.impl.SIFIOFormatter;
import openadk.library.impl.surrogates.RenderSurrogate;
import openadk.util.XMLWriter;

/**
 *  Renders a <code>SIFElement</code> to an XML stream in SIF format.<p>
 *
 *  Agents do not typically use the SIFWriter class directly, but may do so to
 *  render a SIF Data Object or a SIF Message to an output stream. The following
 *  code demonstrates how to write a SIFDataObject to System.out:
 *  <p>
 *
 *  <pre>
 *       StudentPersonal sp = ...
 *       SIFWriter out = new SIFWriter( System.out );
 *       out.write( sp );
 *  </pre>
 *  @author Eric Petersen
 *  @version 1.0
 */
public class SIFWriter
{

	private boolean fRootAttributesWritten = false;

	private XMLWriter fWriter;

	/**
	 *  The SIFElement to render
	 */
	protected SIFElement fData;

	/**
	 *  Elements that should not be included in the output
	 */
	protected HashMap<String, ElementDef> fFilter;

	/**
	 *  The version of SIF to use when rendering XML
	 */
	protected SIFVersion fVersion;

	protected SIFFormatter fFormatter;

    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    public static final String NIL = "nil";
    public static final String XSI_NIL = "xsi:" + NIL;

	private SIFWriter()
	{
		setSIFVersion( ADK.getSIFVersion() );
	}

	/**
	 *  Creates an instance of SIFWriter to wrap the given stream
	 *  @param out The OutputStream to write to, using SIF Encoding rules
	 */
    public SIFWriter( OutputStream out ) {
    	this();
    	fWriter = new XMLWriter( SIFIOFormatter.createOutputWriter( out ) );
    }

	/**
	 *  Creates an instance of SIFWriter to wrap the given stream
	 *  @param out The OutputStream to write elements to, using SIF Encoding rules
	 *  @param autoFlush true to flush the stream after each write
	 */
	public SIFWriter( OutputStream out, boolean autoFlush ) {
		this();
		fWriter = new XMLWriter( SIFIOFormatter.createOutputWriter( out ), autoFlush );
	}

	/**
	 * Creates an instance of SIFWriter to wrap the given stream. This
	 * constructor allows agent properties to be passed to the SIFWriter
	 * <p>
	 * The agent property, <code>adk.compatibility.enableXMLEscaping</code> is
	 * checked in this constructor. If the falue is false, XmlElement and
	 * Attribute data will not be escaped
	 * @param	out		The OutputStream to write elements to
	 * @param 	zone	the Zone to read properties from
	 */
	public SIFWriter( OutputStream out, Zone zone ) {
		this();
		fWriter = new XMLWriter( SIFIOFormatter.createOutputWriter( out ) );
	}

	/**
	 *  Constructor
	 *  @param out The Writer to write elements to
	 */
	public SIFWriter( Writer out ) {
		this();
		fWriter = new XMLWriter( out );
	}

	/**
	 * Constructor that allows agent properties to be passed to the SIFWriter
	 * <p>
	 * The agent property, <code>adk.compatibility.enableXMLEscaping</code> is checked in this constructor.
	 * If the falue is false, XmlElement and Attribute data will not be escaped
	 * @param	out		the Writer to write elements to
	 * @param 	zone	the Zone to read properties from
	 */
	public SIFWriter( Writer out, Zone zone ) {
		this();
		fWriter = new XMLWriter( out );
	}

	/**
	 *  Constructor
	 *  @param out The Writer to write elements to
	 *  @param autoFlush true to flush the Writer after each write
	 */
	public SIFWriter( Writer out, boolean autoFlush ) {
		this();
		fWriter = new XMLWriter( out, autoFlush );
	}

	/**
	 *  Places a filter on this SIFWriter such that only elements (and their
	 *  children) identified in the array will be included in the output. Note
	 *  that attributes are always included even if not specified in the filter
	 *  list, as are top-level SIF Data Objects like StudentPersonal.<p>
	 *
	 *  The filter remains in effect until the <code>clearFilter</code> method
	 *  is called or a null array is passed to this method.<p>
	 *
	 *  @param elements An array of ElementDef constants from the SIFDTD class
	 *      that identify elements to include in the output, or <code>null</code>
	 *      to clear the current filter
	 */
	public void setFilter( ElementDef[] elements )
	{
		if( elements == null )
			clearFilter();
		else
		{
			if( fFilter == null )
				fFilter = new HashMap<String,ElementDef>();
			else
				fFilter.clear();

			for( int i = 0; i < elements.length; i++ ) {
				if( elements[i] != null ) {
					fFilter.put( elements[i].name(), elements[i] );
				}
			}
		}
	}



	/**
	 *  Clears the filter previously set with the setFilter method
	 */
	public void clearFilter()
	{
		if( fFilter != null ) {
			fFilter.clear();
			fFilter = null;
		}
	}


	private static final int
		EMPTY = 0,
		OPEN = 1,
		CLOSE = 2;

	private boolean hasContent( SIFElement o, SIFVersion version )
	{
		if( o.getChildCount() > 0 ){
			return true;
		}
		List<SimpleField> fields = o.getFields();
		for( SimpleField f : fields ) {
			// TODO: This is a perfect place to optimize. Version-specific lookups
			// should be optimized
			if( f.fElementDef.isSupported( version ) && !f.fElementDef.isAttribute( version ) ){
				return true;
			}
		}
		return false;
	}

	/**
	 *  Write a SIF Message in the version of SIF in effect for that object.
	 *  To change the version of SIF that is used, call the
	 *  <code>SIFMessagePayload.setSIFVersion</code> method prior to calling
	 *  this function.<p>
	 *
	 *  @param o The SIF Message to write to the output stream
	 */
	public synchronized void write( SIFMessagePayload o )
	{
		setSIFVersion( o.getSIFVersion() );
		fWriter.write("<SIF_Message ");
		writeRootAttributes( true );
		fWriter.println( ">" );

		fWriter.indent( 1 );
		writeElement((SIFElement)o);
		fWriter.println("</SIF_Message>");
	}

	/**
	 *  Write a SIF Data Object in the version of SIF in effect for that object.
	 *  To change the version of SIF that is used, call the
	 *  <code>SIFDataObject.setSIFVersion</code> method prior to calling
	 *  this function.<p>
	 *
	 *  @param o The SIFDataObject instance to write to the output stream
	 */
	public synchronized void write( SIFDataObject o )
	{
		setSIFVersion( o.getSIFVersion() );

		if( o instanceof SIFDataObjectXML ){
			fWriter.write(o.toXML());
		}
		else{
			writeElement((SIFElement)o);
		}
	}

	/**
	 *  Write a SIF Data Object to the output stream using whatever XML content
	 *  is currently defined for that object.<p>
	 *
	 *  @param o The SIFDataObjectXML instance to write to the output stream
	 */
	public synchronized void write( SIFDataObjectXML o )
	{
		setSIFVersion( o.getSIFVersion() );
		fWriter.write(o.toXML());
	}

	/**
	 *  Write a SIF element in the version of SIF specified.<p>
	 *
	 *  @param version The version of SIF to use when rendering the SIF element
	 *  @param o The SIF Element instance to write to the output stream
	 */
	public synchronized void write( SIFVersion version, SIFElement o )
	{
		setSIFVersion( version );
		writeElement(o);
	}

	/**
	 *  Write a SIF element in the version of SIF currently in effect for this
	 *  SIFWriter.
	 *  <p>
	 *
	 *  @param o The SIF Element instance to write to the output stream
	 */
	public void write( SIFElement o )
	{
		writeElement( o );
	}

	private void writeSIFExtendedElement(SIF_ExtendedElement element) {
		fWriter.write(element.getXML());
	}


	/**
	 * @param element
	 * @param isLegacy if true, this method assumes that it needs to do more work,
	 * such as looking for rendering surrogates for the specific version of SIF
	 */
	private void writeElement( SIFElement element, boolean isLegacy )
	{
		ElementDef def = element.getElementDef();
		if( !( include(element) && def.isSupported( fVersion ) ) ){
			return;
		}

		if( isLegacy ){
			RenderSurrogate surrogate = def.getVersionInfo( fVersion ).getSurrogate();
			if( surrogate != null ){
				try
				{
					surrogate.renderRaw( fWriter, fVersion, element, fFormatter );
				} catch( ADKException ex ){
					throw new RuntimeException( ex.getMessage(), ex );
				}
				return;
			}
		}

		if( element.isEmpty() || !hasContent( element, fVersion ) )
			// TODO: We need to review this special case code and determine if there
			// is a more efficient way to deal with XML content
			// Special case: XMLData
			if( element instanceof XMLData ){
				write(element,OPEN, isLegacy);
				fWriter.write((( XMLData )element).getXML() );
				write(element,CLOSE, isLegacy );
			} else if ( element instanceof SIF_ExtendedElement && !isLegacy ) {
				writeSIFExtendedElement((SIF_ExtendedElement )element);
			} else {
				write(element,EMPTY, isLegacy );
			}
		else if ( element instanceof SIF_ExtendedElement && !isLegacy ) {
			writeSIFExtendedElement((SIF_ExtendedElement )element);
		} 
		else
		{
			write(element,OPEN, isLegacy);
			if( !fRootAttributesWritten ){
				writeRootAttributes( false );
			}

			List<Element> elements = fFormatter.getContent( element, fVersion );
			for( Element e : elements ){
				if( e instanceof SIFElement )
					writeElement((SIFElement)e, isLegacy );
				else
					write((SimpleField)e, isLegacy);
			}
			write(element,CLOSE, isLegacy);
		}
	}

	/**
	 * Write a SIF element in the version of SIF currently in effect for this
	 *  SIFWriter.
	 * @param o
	 */
	private void writeElement( SIFElement o )
	{
		writeElement( o, fVersion.compareTo( SIFVersion.SIF20 ) < 0 ) ;
	}

	protected void write( SimpleField f, boolean isLegacy )
	{
		if( !include(f) ){
			return;
		}

		if( isLegacy ){
			RenderSurrogate surrogate = f.getElementDef().getVersionInfo( fVersion ).getSurrogate();
			if( surrogate != null ){
				try
				{
					surrogate.renderRaw( fWriter, fVersion, f, fFormatter );
				} catch( ADKException ex ){
					throw new RuntimeException( ex.getMessage(), ex );
				}
				return;
			}
		}

		//  "<tag [attr...]>[text]" or "<tag [attr...]/>"
		fWriter.tab();
		StringBuilder b = new StringBuilder();
		b.append('<');
		b.append(f.fElementDef.tag(fVersion));


		String fieldValue = null;
		SIFSimpleType simpleValue = f.getSIFValue();
		if( simpleValue != null ){
			fieldValue = simpleValue.toString( fFormatter );
		}

		// Check the value to see if it's null
		if( fieldValue == null ){
			if( !isLegacy ){
				b.append(' ');
				b.append(  XSI_NIL +"=\"true\" />" );
				fWriter.println(b.toString());
				return;
			} else {
				// The specified version of SIF doesn't support
				// the xsi:nil attribute. Set the value to an
				// empty string
				fieldValue = "";
			}
		}

		b.append('>');

		if( f.isDoNotEncode() ) {
			b.append( fieldValue );
		} else {
			b.append( fWriter.xmlEncode( fieldValue ) );
		}
		b.append("</");
		b.append(f.fElementDef.tag(fVersion));
		b.append('>');

		fWriter.println(b.toString());

	}

	protected void write( SIFElement o, int mode, boolean isLegacy )
	{
		String tag = o.getElementDef().tag(fVersion);
		if( mode == CLOSE )
		{
			// "</tag>"
			fWriter.indent(-1);
			fWriter.tab();
			fWriter.println("</"+tag+">");
		}
		else
		{
			//  "<tag [attr...]>[text]" or "<tag [attr...]/>"
			fWriter.tab();
			fWriter.print("<"+tag);
			if( !fRootAttributesWritten ){
				writeRootAttributes( false );
			}
			writeAttributes(o);
			if( mode == EMPTY )
				fWriter.println("/>");
			else {
				SIFSimpleType sst = o.getSIFValue();
				if( sst != null ){
					if( sst.getValue() == null ){
						if( isLegacy ){
							fWriter.print(">");
						} else {
							fWriter.print( " " +  XSI_NIL +"=\"true\">" );
							fWriter.pauseTab();
						}
					} else {
						fWriter.print(">");
						if( o.isDoNotEncode() ) {
							fWriter.print( sst.toString( fFormatter ) );
						} else {
							fWriter.printXmlText( sst.toString( fFormatter ) );
						}
						fWriter.pauseTab();
					}
				}else {
					fWriter.println( ">" );
					fWriter.indent(1);
				}

			}
		}
	}

	/**
	 *  Write the attributes of a SIFElement to the output stream
	 *  @param o The SIFElement whose attributes are to be written
	 */
	protected void writeAttributes( SIFElement o )
	{
		StringBuilder b = new StringBuilder();
		List<SimpleField> fields = fFormatter.getFields( o, fVersion );
		for( SimpleField f : fields )
		{
			ElementVersionInfo evi = f.fElementDef.getVersionInfo( fVersion );
			if( evi != null && evi.isAttribute() )
			{
				// Null attribute values are not supported in SIF, unlike
                // element values, which can be represented with xs:nil
				@SuppressWarnings("unused")
				SIFSimpleType sst = f.getSIFValue();
				if( sst.getValue() != null )
				{
					b.append(' ');
					b.append( evi.getTag() );
					b.append( "=\"" );
					String fieldValue = sst.toString( fFormatter );
					if( f.isDoNotEncode() ) {
						b.append( fieldValue );
					} else {
						b.append( fWriter.xmlEncode( fieldValue ) );
					}
					b.append( '\"' );
				}
			}
		}

		fWriter.print(b.toString());
	}

	/**
	 * Writes the given string without doing any xml encoding
	 * @param value
	 */
	public void writeRaw( String value )
	{
		fWriter.print( value );
	}

	/**
	 * Writes the given string, after xml encoding it.
	 * @param value
	 */
	public void write( String value )
	{
		fWriter.printXmlText( value );
	}

	protected boolean include( Element o )
	{
		if( o.isChanged() )
		{
			if( fFilter == null || o.fElementDef.isObject() ){
				return true;
			}

			//  If the element is in the filter list, include it
			if( fFilter.containsKey( o.fElementDef.name() ) ){
				return true;
			}

			//  If any of the element's parents are in the filter list, include it
			Element cur = o;
			while( cur != null ) {
				if( fFilter.containsKey( cur.fElementDef.name() ) ){
					return true;
				}
				cur = cur.getParent();
			}

				//  At this point the element should not be included *unless* it is
			//  the parent of one of the elements in the filter list. In this
			//  case it has to be included or else that child will not be.
			for( Iterator it = fFilter.values().iterator(); it.hasNext(); )
			{
				ElementDef parentDef = ((ElementDef)it.next()).getParent();
				if( parentDef != null && parentDef.name().equals( o.fElementDef.name() ) )
					return true;
			}
		}

		return false;
	}

	public void write( char[] chars ){
		fWriter.write( chars );
	}

	public void flush()
	{
		fWriter.flush();
	}

	public void close()
	{
		fWriter.close();
	}

	private void setSIFVersion( SIFVersion version )
	{
		fVersion = version;
		fFormatter = ADK.DTD().getFormatter( version );
	}


	/**
	 * By Default, SIFWriter writes an XML Namespace when it starts
	 * writing to an element stream. If this is not desirable, it can
	 * be suppressed with this call
	 * @param suppress
	 */
	public void suppressNamespace( boolean suppress )
	{
		fRootAttributesWritten = suppress;
	}

	private void writeRootAttributes( boolean includeVersion )
	{
		if( !fRootAttributesWritten )
		{
			fWriter.write(" xmlns=\"" + fVersion.getXmlns() + "\"" );
			if( fFormatter.supportsNamespaces() ){
				fWriter.write( " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			}
			if( includeVersion ){
				fWriter.write( " Version=\"" + fVersion.toString() + "\"" );
			}
		}
		fRootAttributesWritten = true;
	}
}
