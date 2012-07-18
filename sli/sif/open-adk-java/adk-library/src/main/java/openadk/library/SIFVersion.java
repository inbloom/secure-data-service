//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.Serializable;
import java.util.*;

/**
 *	Encapsulates a SIF version number.<p>
 *
 *  The ADK uses instances of SIFVersion rather than strings to identify
 *  versions of SIF. Typically you do not need to obtain a SIFVersion instance
 *  directly except for when initializing the class framework with the
 *  <code>ADK.initialize</code> method. Rather, classes for which SIF version is
 *  a property, such as SIFDataObject and Query, provide a <code>getSIFVersion</code>
 *  method to obtain the version associated with an object.<p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class SIFVersion implements Serializable, Comparable<SIFVersion>
{
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	private static Hashtable<String, SIFVersion> sVersions;

	/** The major version number */
	private final int fMajor;

	/** The minor version number */
	private final int fMinor;

	/** The revision number */
	private final int fRevision;

//// WARNING: MAKE SURE TO UPDATE THE GETINSTANCE METHOD WHEN ADDING NEW VERSIONS ////

	/** Identifies the SIF 1.1 Specification */
	public static final SIFVersion SIF11 = new SIFVersion( 1, 1, 0 );

	/** Identifies the SIF 1.5r1 Specification */
	public static final SIFVersion SIF15r1 = new SIFVersion( 1, 5, 1 );

	/** Identifies the SIF 2.0 Specification */
	public static final SIFVersion SIF20 = new SIFVersion( 2, 0, 0 );

	/** Identifies the SIF 2.0r1 Specification */
	public static final SIFVersion SIF20r1 = new SIFVersion( 2, 0, 1 );

	/** Identifies the SIF 2.1 Specification */
	public static final SIFVersion SIF21 = new SIFVersion( 2, 1, 0 );

	/** Identifies the SIF 2.2 Specification */
	public static final SIFVersion SIF22 = new SIFVersion( 2, 2, 0 );

	/** Identifies the SIF 2.3 Specification */
	public static final SIFVersion SIF23 = new SIFVersion( 2, 3, 0 );

	/** Identifies the SIF 2.4 Specification */
	public static final SIFVersion SIF24 = new SIFVersion( 2, 4, 0 );

	/** Identifies the SIF 2.5 Specification */
	public static final SIFVersion SIF25 = new SIFVersion( 2, 5, 0 );
	
//// WARNING: MAKE SURE TO UPDATE THE GETINSTANCE METHOD WHEN ADDING NEW VERSIONS ////

	/** Identifies the latest SIF Specification supported by the SIFWorks ADK */
	public static final SIFVersion LATEST = SIF25;

	/**
	 *	Constructs a version object
	 *
	 *  @param major The major version number
	 *  @param minor The minor version number
	 *  @param revision The revision number
	 */
	private SIFVersion( int major, int minor, int revision )
	{
		fMajor = major;
		fMinor = minor;
		fRevision = revision;
	}

	/**
	 *  Gets a SIFVersion instance. This method always returns the same version
	 *  instance for the given version numbers. If the version numbers match an official
	 *  version supported by the ADK, that version is returned. Otherwise, new version
	 *  object is created and returned. The same SIFVersion instance will always be
	 *  returned for the same parameters

	 *  @return A SIFVersion instance to encapsulate the version information
	 *      provided to this method. If the <i>major</i>, <i>minor</i>, and
	 *      <i>revision</i> numbers match one of the versions supported by the
	 *      ADK (e.g. SIFVersion.SIF10r1, SIFVersion.SIF10r2, etc.), that object
	 *      is returned. Otherwise, a new instance is created. Thus, you are
	 *      guaranteed to always receive the same SIFVersion instance for a given
	 *      version number supported by the ADK.
	 */
	private static SIFVersion getInstance( int major, int minor, int revision )
	{
		// Check for versions explicitly supported by the ADK first
		if( major == 2 ){
			if( minor == 0 ){
				if( revision == 0 ){
					return SIF20;
				} else if( revision == 1 ){
					return SIF20r1;
				}
			}
			if( revision == 0 ){
				if( minor == 1 ){
					return SIF21;
				} else if( minor == 2 ){
					return SIF22;
				} else if( minor == 3 ){
					return SIF23;
				} else if( minor == 4 ) {
					return SIF24;
				} else if( minor == 5 ) {
					return SIF25;
				}
			}
		} else if( major == 1 ) {
			if( minor == 5 && revision == 1 ){
				return SIF15r1;
			}
			else if( minor == 1 && revision == 0 ){
				return SIF11;
			}
		}

		// No explicit support found. Return a newly-fabricated instance
		// to support this version of SIF
		String tag = toString( major, minor, revision );

		// An official SIFVersion matching the parameters was not found.
		// check for an existing parsed instance, or create a new one.
		if( sVersions == null ){
			sVersions = new Hashtable<String,SIFVersion>();
		}
		SIFVersion ver = sVersions.get( tag );
		if( ver == null ) {
			ver = new SIFVersion( major, minor, revision );
			sVersions.put( tag, ver );
		}
		return ver;
	}


	/**
	 *  Gets the major version number
	 * @return the major version number of this SIFVersion
	 */
	public int getMajor() {
		return fMajor;
	}

	/**
	 *  Gets the minor version number
	 * @return the minor version number of this SIFVersion
	 */
	public int getMinor() {
		return fMinor;
	}

	/**
	 *  Gets the revision number
	 * @return The revision number of this SIFVersion
	 */
	public int getRevision() {
		return fRevision;
	}

	/**
	 *  Parse a <code>SIFVersion</code> from a string<p>
	 *
	 *  @param versionStr A version string in the format "1.0r1"
	 *  @return A SIFVersion instance encapsulating the version string
	 *  @exception IllegalArgumentException is thrown if the version string is invalid
	 */
	public static SIFVersion parse( String versionStr )
	{
		try
		{
			String v = versionStr.toLowerCase();

			int i = v.indexOf('.');
			int major = Integer.parseInt( v.substring(0,i) );
			int minor = 0;
			int revision = 0;

			int r = v.indexOf('r');
			if( r == -1 ) {
				String minorStr = v.substring(i+1);
				if( minorStr.equals( "*" ) ){
					// NOTE: This may change to getLatest(major). However, the Test harness does not
					// support that at the moment.

					// For now, return 1.5r1 for 1.*, 2.0r1 or higher (based on ADK.getSIFVersion) for 2.*
					if( major == 1 ){
						return SIFVersion.SIF15r1;
					} else if( major == 2 ){

						if( ADK.getSIFVersion().compareTo( SIFVersion.SIF20r1 ) > 0 ){
							return ADK.getSIFVersion();
						} else {
							return SIFVersion.SIF20r1;
						}
					} else {
						throw new IllegalArgumentException( "SIFVersion " + versionStr + " is not supported." );
					}
				} else {
					minor = Integer.parseInt( minorStr );
				}
			} else {
				minor = Integer.parseInt( v.substring(i+1,r) );
	    		revision = Integer.parseInt( v.substring(r+1) );
			}

			return getInstance( major, minor, revision );
		}
		catch( NumberFormatException nfe )
		{
			throw new IllegalArgumentException( versionStr + " is an invalid version string", nfe );
		}
	}

	/**
	 * Returns the latest SIFVersion supported by the ADK for the major version
	 * number of SIF specified. For example, passing the value 1 returns
	 * <code>SIFVersion.15r1</code>
	 * @param major
	 * @return The latest version of SIF that the ADK supports for the specified
	 * major version.
	 */
	public static SIFVersion getLatest( int major ){
		switch( major ){
			case 1:
				return SIFVersion.SIF15r1;
			case 2:
				return SIFVersion.LATEST;
		}
		return null;
	}

	/**
	 * Returns the earliest SIFVersion supported by the ADK for the major version
	 * number of SIF specified. For example, passing the value 1 returns
	 * <code>SIFVersion.SIF11</code>
	 * @param major
	 * @return The latest version of SIF that the ADK supports for the specified
	 * major version.
	 */
	public static SIFVersion getEarliest( int major ){
		switch( major ){
			case 1:
				return SIFVersion.SIF11;
			case 2:
				// NOTE SIFVersion.SIF20 is supported by the ADK, but will be phased
				// out, and is no longer an official SIF Version
				return SIFVersion.SIF20r1;
		}
		return null;
	}

	/**
	 *  Parse a <code>SIFVersion</code> from a <i>xmlns</i> attribute value<p>
	 *
	 *  If the xmlns attribute is in the form "http://www.sifinfo.org/v1.0r1/messages",
	 *  the version identified by the namespace is returned (e.g. "1.0r1"). If the
	 *  xmlns attribute is in the form "http://www.sifinfo.org/infrastructure/1.x",
	 *  the latest version of SIF identified by the major version number is
	 *  returned.<p>
	 *
	 *  @param xmlns A SIF xmlns attribute value (e.g. "http://www.sifinfo.org/v1.0r1/messages",
	 *      "http://www.sifinfo.org/infrastructure/1x.", etc)
	 *
	 *  @return A SIFVersion object encapsulating the version of SIF identified
	 *      by the xmlns value, or null if the xmlns is invalid
	 */
	public static SIFVersion parseXmlns( String xmlns )
	{
		//
		//  Determine the SIFVersion:
		//
		if( xmlns != null )
		{
			if(  xmlns.endsWith( ".x" ) ){

				//  http://www.sifinfo.org/infrastructure/1.x
				//  NOTE: This works until SIF 10.x
				int location = xmlns.lastIndexOf( '/' );
				char majorCh = xmlns.charAt( location + 1 );
				if( Character.isDigit( majorCh ) )
				{
					int major = majorCh-48;
					return getLatest( major );
				}
			}
		}
		return null;
	}

	/**
	 *  Get the SIF namespace for this version of the specification.<p>
	 *
	 *  @return If the SIFVersion is less than SIF 1.1, a namespace in the form
	 *      "http://www.sifinfo.org/v1.0r2/messages" is returned, where the full
	 *      SIF Version number is included in the namespace. For SIF 1.x and
	 *      later, a namespace in the form "http://www.sifinfo.org/infrastructure/1.x"
	 *      is returned, where only the major version number is included in the
	 *      namespace.
	 */
	public String getXmlns()
	{
		if( compareTo( SIFVersion.SIF11 ) < 0 )
			return "http://www.sifinfo.org/v" + toString() + "/messages";

		return SIFDTD.XMLNS_BASE + "/" + fMajor + ".x";
	}

	/**
	 *  Compare this version to another
	 *  @param version The version to compare
	 *  @return -1 if this version is earlier than <code>version</code>, 0 if
	 *      the versions are equal, or 1 if this version is greater than <code>
	 *      version</code> or <code>version</code> is null.
	 */
	public int compareTo( SIFVersion version )
	{
		if( version == null ){
			return 1;
		}
		int compare = compare( fMajor, version.fMajor );
		if( compare != 0 ){
			return compare;
		}
		compare = compare( fMinor, version.fMinor );
		if( compare != 0 ){
			return compare;
		}
		return compare( fRevision, version.fRevision );
	}

	private int compare( int thisValue, int compareValue )
	{
		if( thisValue == compareValue ){
			return 0;
		}
		if( thisValue > compareValue ){
			return 1;
		}
		return -1;
	}


	/**
	 *  Gets the string representation of the version
	 *  @return The tag passed to the constructor. If null, a version in the
	 *      form <i>major</i>.<i>minor</i>r<i>revision</i> is returned with no
	 *      padding.
	 */
	public String toString()
	{
		return toString( fMajor, fMinor, fRevision );
	}

	private static String toString( int major, int minor, int revision ){
		if( revision < 1 )
			return String.format( "%s.%s",  major, minor );
		else
			return String.format( "%s.%sr%s",  major, minor, revision );
	}

	/**
	 *  Gets the string representation of the version using an underscore instead
	 *  of a period as the delimiter
	 * @return This SIFVersion in symbol format (e.g. 1_5r1)
	 */
	public String toSymbol()
	{
		if( fRevision < 1 )
			return fMajor+"_"+fMinor;
		else
			return fMajor+"_"+fMinor+"r"+fRevision;
	}

	/**
	 * Evaluates the native wrapped value of this object to see if
	 * it equals the value of the compared object
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
	    if( this == o )
	    {
	        return true;
	    }
	    if((o != null) && (o instanceof SIFVersion ))
	    {
	        SIFVersion compared = (SIFVersion)o;
	        return fMajor == compared.fMajor &&
	        		fMinor == compared.fMinor &&
	        		fRevision == compared.fRevision;
	    }
	    return false;
	 }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
	    return fMajor * 100000 + fMinor * 100 + fRevision;
	 }


}
