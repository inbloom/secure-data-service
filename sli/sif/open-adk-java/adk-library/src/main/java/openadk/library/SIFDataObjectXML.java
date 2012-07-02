//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  A SIFDataObject implementation that wraps raw XML messages.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class SIFDataObjectXML extends SIFDataObject
{
	protected String fXml;

	/**
	 *  Constructs a SIFDataObject with XML content
	 *  @param def The ElementDef constant from the SIFDTD class identifying
	 *      the data object represented by the XML
	 *  @param xml The XML content
	 */
    public SIFDataObjectXML( ElementDef def, String xml )
	{
		super( ADK.getSIFVersion(),def );

		fXml = xml;
	}

	public String getRefId()
	{
		throw new ADKNotSupportedException( "The getRefId method cannot be called on SIFDataObjectXML instances", null );
	}

	/**
	 *  Constructs a SIFDataObject with XML content
	 *  @param version The version of SIF to associate with this object
	 *  @param def The ElementDef constant from the SIFDTD class identifying
	 *      the data object represented by the XML
	 *  @param xml The XML content
	 */
    public SIFDataObjectXML( SIFVersion version, ElementDef def, String xml )
	{
		super( version,def );

		fXml = xml;
    }

	/**
	 *  Gets the raw XML message content
	 */
	public String toXML() {
		return fXml;
	}

	/**
	 *  Sets the raw XML message content
	 */
	public void setXML( String xml ) {
		fXml=xml;
	}
}
