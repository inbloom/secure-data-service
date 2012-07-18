//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

public class DTDFieldAlias implements Comparable<DTDFieldAlias> {
	
	/**
	 * The SIFVersion for this combination
	 */
	public SIFVersion version;
	/**
	 * The key that will be stored in the SIFDTD lookup table for parsing SIF objects 
	 */
	public String lookupKey;
	
	public String fieldAlias;
	/**
	 * The SIFDTD const field name of this element 
	 */
	public String dtdConstName;
	public int flags;

		
	public int compareTo(DTDFieldAlias arg0) {
		return this.version.compareTo( version );
	}
}
