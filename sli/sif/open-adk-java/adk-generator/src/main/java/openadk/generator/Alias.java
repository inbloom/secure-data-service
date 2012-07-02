//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

public class Alias implements Comparable {
	
	private SIFVersion fVersion;
	private String fTag;
	private String fSurrogate;
	private int fSequence;
	private boolean deprecated;
	private int flags;
	
	/**
	 * Creates a new Alias with no values set 
	 * @param version The earliest SIFVersion for which this alias is valid 
	 */
	public Alias( SIFVersion version )
	{
		fVersion = version;
	}
	
	/**
	 * Creates a new Alias using the specified values
	 * @param version The earliest SIFVersion for which this alias is valid
	 * @param tag
	 * @param surrogate
	 * @param sequence
	 */
	public Alias( SIFVersion version, String tag, String surrogate, int sequence ){
		fVersion = version;
		fTag = tag;
		fSurrogate = surrogate;
		fSequence = sequence;
	}
	
	
	public String getElementDefExpression(){
		if( getSurrogate() != null ){
			return "~" + getSurrogate() + ( getTag() == null ? "" : getTag() );
		}
		return getTag();
	}
	
	/**
	 * The earliest version of SIF for which this alias is valid
	 * @return
	 */
	public SIFVersion getVersion(){
		return fVersion;
	}
	

	/**
	 * @param fTag the fTag to set
	 */
	void setTag(String tag) {
		this.fTag = tag;
	}

	/**
	 * @return the fTag
	 */
	String getTag() {
		return fTag;
	}

	/**
	 * @param fSurrogate the fSurrogate to set
	 */
	void setSurrogate(String surrogate) {
		this.fSurrogate = surrogate;
	}

	/**
	 * @return the fSurrogate
	 */
	String getSurrogate() {
		return fSurrogate;
	}

	/**
	 * @param fSequence the fSequence to set
	 */
	void setSequence(int sequence) {
		this.fSequence = sequence;
	}

	/**
	 * @return the fSequence
	 */
	int getSequence() {
		return fSequence;
	}

	/**
	 * @param deprecated the deprecated to set
	 */
	void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * @return the deprecated
	 */
	boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	/**
	 * @return the flags
	 */
	public int getFlags() {
		return flags;
	}

	public int compareTo(Object o) {
		if( o == null ){
			return 1;
		}
		if( o instanceof Alias ){
			return fVersion.compareTo( ((Alias)o).getVersion() );
		}
		
		throw new RuntimeException( "Cannot compare an Alias to " + o.getClass().getCanonicalName() );
	}
	
}
