//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

public abstract class AbstractDef
{
	public static final int
		//FLAG_REQUIRED = 0x00000001,
		FLAG_REPEATABLE = 0x00000002,
		FLAG_OPTIONAL = 0x00000004,
		FLAG_MANDATORY = 0x00000008,
		FLAG_CONDITIONAL = 0x00000010,
		FLAG_DEPRECATED = 0x00000020,
		FLAG_DRAFTOBJECT = 0x00000040,
		FLAG_NO_SIFDTD = 0X00000080;

	/** A description of this object/field */
	protected String fDesc = "";

	/** The name of this object/field */
	protected String fName;

	/** Flags describing characteristics of this object/field */
	protected int fFlags;

	/** The earliest version of SIF this definition applies to */
	protected SIFVersion fMinVersion;

	/** The latest version of SIF this definition applies to */
	protected SIFVersion fMaxVersion;
	
	/** 
	 * 	If the sequence # of this object or element has been overridden, this is
	 * 	the sequence # that will be assigned to the ADK's ElementDef instance for
	 * 	this object or element. Otherwise, -1.
	 */
	protected int fSeqOverride = -1;
	  

	/**
	 *  Constructor
	 */
    public AbstractDef()
	{
    }

	public void setDesc( String d ) {
		if( d == null ){
			fDesc = "";
		} else {
			fDesc = d;
		}
	}
	public String getDesc() {
		return fDesc;
	}
	public void setName( String n ) {
		fName = n;
	}
	public String getName() {
		return fName;
	}

	public void setSequenceOverride( int sequence )
	{
		fSeqOverride = sequence;
	}
	
	public int getSequenceOverride()
	{
		return fSeqOverride;
	}

	public boolean isDeprecated()
	{
		return ( fFlags & FLAG_DEPRECATED ) != 0;
	}

	public void setFlags( String f ) 
	{
		String ff = f.toUpperCase();

		if( ff.equalsIgnoreCase("O") )
			fFlags |= FLAG_OPTIONAL; else
		if( ff.equalsIgnoreCase("R") )
			fFlags |= FLAG_MANDATORY; else
		if( ff.equalsIgnoreCase("M") )
			fFlags |= FLAG_MANDATORY; else
		if( ff.equalsIgnoreCase("C") )
			fFlags |= FLAG_CONDITIONAL; else
		if( ff.equalsIgnoreCase("MR") )
			fFlags |= (FLAG_MANDATORY|FLAG_REPEATABLE); else
		if( ff.equalsIgnoreCase("OR") )
			fFlags |= (FLAG_OPTIONAL|FLAG_REPEATABLE); else
		if( ff.equalsIgnoreCase("CR") )
			fFlags |= (FLAG_CONDITIONAL|FLAG_REPEATABLE);
	}

	public int getFlags()
	{
		return fFlags;
	}

	public void setFlags( int flags )
	{
		fFlags = flags;
	}

	public boolean isDraft()
	{
		return ( fFlags & FLAG_DRAFTOBJECT ) != 0 ;
	}

	public void setDraft()
	{
		fFlags |= FLAG_DRAFTOBJECT;
	}
	

	/**
	 *  Gets the earliest version of SIF this definition applies to.
	 */
	public SIFVersion getEarliestVersion()
	{
		return fMinVersion;
	}

	/**
	 *  Gets the latest version of SIF this definition applies to.
	 */
	public SIFVersion getLatestVersion()
	{
		return fMaxVersion;
	}

	/**
	 *  Sets the latest version of SIF this definition applies to.
	 */
	public void setLatestVersion( SIFVersion version )
	{
		if( fMaxVersion == null || version.compareTo(fMaxVersion) > 0 ){
			fMaxVersion = version;
		}
		if( fMinVersion == null ){
			fMinVersion = version;
		}
	}

	/**
	 *  Sets the earliest version of SIF this definition applies to.
	 *
	 *  If the version is earlier than the current earliest version, it becomes
	 *  the earliest version and will be returned by the getEarliestVersion
	 *  method. Otherwise no action is taken.
	 */
	public void setEarliestVersion( SIFVersion version )
	{
		if( fMinVersion == null || version.compareTo(fMinVersion) < 0 )
			fMinVersion = version;
	}
	
	/**
	 * Allows the def to validate itself after all values are set
	 * @return
	 */
	public void validate() throws ParseException
	{}

}
