//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (c)2001-2007 Edustructures LLC
//  All rights reserved.
//
//  This software is the confidential and proprietary information of
//  Edustructures LLC ("Confidential Information").  You shall not disclose
//  such Confidential Information and shall use it only in accordance with the
//  terms of the license agreement you entered into with Edustructures.
//

import openadk.library.*;

/**
 *	Encapsulates optional filtering attributes for field mapping rules.<p>
 *
 * 	A field mapping rule can define any of the following filters:<p>
 * 
 * 	<ul>
 * 		<li>
 * 			<b>SIF Version</b>. The rule will only be applied if the 
 * 			SIFVersion instance passed to the <code>Mappings.map</code> 
 * 			method matches this value. The version filter is a string
 * 			comprised of a comparison operator ("=" for equals, "-"
 * 			for Less Than or Equal To, or "+" for Greater Than or
 * 			Equal To) followed by a SIF Version identifier. For example, 
 * 			"=1.5" matches SIF 1.5; "+1.1" matches all versions of SIF 
 * 			equal to and greater than 1.1; and "-1.1" matches all versions
 * 			of SIF less than or equal to SIF 1.1. Filter on SIF Version 
 * 			to map values to version-specific elements or attributes 
 * 			of SIF Data Objects.
 * 		</li>
 * 		<li>
 * 			<b>Direction</b>. The rule will only be applied if the
 * 			Direction flag (<code>MappingsDirection.INBOUND</code>
 * 			or <code>MappingsDirection.OUTBOUND</code>) passed to 
 * 			the <code>Mappings.map</code> method matches this value.
 * 			Filter on Direction to create field mapping rules that
 * 			differ for inbound messages like SIF_Request than for
 * 			outbound messages like SIF_Event and SIF_Response.
 * 		</li>
 * 	</ul>
 */
public class MappingsFilter
{
	/**
	 * 	SIFVersion filter
	 */
	protected String fVersion = null;
	
	/**
	 * 	Direction filter
	 */
	protected MappingsDirection fDirection = MappingsDirection.UNSPECIFIED;
	
	/**
	 * 	Sets the SIF Version filter.<p>
	 * 
	 * 	@param version A SIF Version string prefixed by a comparision 
	 * 		operator "=", "+" or "-";. For example, specify "=1.5"
	 * 		to match SIF 1.5; "+1.1" to match all versions of SIF equal
	 * 		to or greater than 1.1, "-1.1" to match all versions of
	 * 		SIF less than or equal to SIF 1.1, etc. If no comparision 
	 * 		operator is specified Equal To ("=") is assumed.
	 */
	public void setSIFVersion( String version )
	{
		if( version == null )
			fVersion = null;
		else
		if( !version.startsWith( "=" ) && 
			!version.startsWith( "+" ) &&
			!version.startsWith( "-" ) ) 
		{
			fVersion = "=" + version;
		}
		else
			fVersion = version;
	}	
	
	/**
	 * 	Gets the SIF Version filter.<p>
	 * 
	 * 	@return A SIF Version string prefixed by a comparision 
	 * 		operator "=", "&lt;" or "&gt";. For example, specify "=1.5"
	 * 		to match SIF 1.5; ">1.1" to match all versions of SIF equal
	 * 		to or greater than 1.1, etc. If no comparision operator 
	 * 		is specified Equal To ("=") is assumed.
	 */
	public String getSIFVersion()
	{
		return fVersion;
	}
	
	/**
	 * 	Determines if the SIF Version filter is specified
	 * @return True if this filter contains a SIF Version filter.
	 */
	public boolean hasVersionFilter()
	{
		return fVersion != null;
	}	
	
	/**
	 * 	Sets the Message Direction filter.<p>
	 * 
	 * 	@param dir Any <code>MappingsDirection.</code> constant
	 */
	public void setDirection( MappingsDirection dir )
	{
		fDirection = dir;
	}
	
	/**
	 * 	Gets the Message Direction filter.<p>
	 * 
	 * 	@return Any <code>MappingsDirection.</code> constant
	 */
	public MappingsDirection getDirection()
	{
		return fDirection;
	}
	
	/**
	 * 	Determines if the Message Direction filter is specified
	 * @return True if this filter specifies a direction filter
	 */
	public boolean hasDirectionFilter()
	{
		return fDirection != MappingsDirection.UNSPECIFIED;
	}	
	
	/**
	 * 	Evaluates the Message Direction filter against a direction flag.<p>
	 * 	@param flag Any <code>MappingsDirection</code> value
	 * 	@return true If the message direction filter is undefined or 
	 * 		matches the specified flag <code>flag</code>
	 */			
	public boolean evalDirection( MappingsDirection flag )
	{
		return fDirection == MappingsDirection.UNSPECIFIED ||
			   fDirection == flag;
	}
	
	/**
	 * 	Evaluates the SIF Version filter against a SIFVersion instance.<p>
	 * 	@param version A SIFVersion instance
	 * 	@return true If the SIF Version filter is undefined, or if the
	 * 		<code>version</code> parameter evaluates true given the 
	 * 		comparision operator and version of SIF specified by the
	 * 		current filter
	 */	
	public boolean evalVersion( SIFVersion version )
	{
		if( fVersion == null || version == null )
			return true;
			
		SIFVersion flt = SIFVersion.parse( fVersion.substring(1) );
		if( flt != null )
		{
			if( fVersion.charAt(0) == '=' )
				return flt.compareTo( version ) == 0; // Prefix of = means SIF_Version == filter version
			if( fVersion.charAt(0) == '+' )
				return version.compareTo(flt) >= 0; // Prefix of + means filter applies to messages with SIF_Version 
			if( fVersion.charAt(0) == '-' )
				return version.compareTo(flt) <= 0; // Prefix of - means SIF_Version <= filter version
		}
			
		return false;
	}
}
