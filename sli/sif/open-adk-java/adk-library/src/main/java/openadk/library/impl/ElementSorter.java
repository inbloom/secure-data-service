//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.*;

import openadk.library.*;

/**
 *  Sorts SIFElements by sequence number.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ElementSorter implements Comparator<Element>
{
//	/**
//	 *  Static map of sorters keyed by SIFVersion number
//	 */
//	protected static HashMap<SIFVersion, ElementSorter> sSorters = new HashMap<SIFVersion, ElementSorter>();

	/**
	 *  The SIF version this ElementSorter will use when obtaining sequence
	 *  numbers for SIFElements
	 */
	protected SIFVersion fVersion;
	
	/**
	 *  Constructor
	 *  @param version The version of SIF to sort against. The sequence numbers
	 *      of some elements change from one version of SIF to the next as a
	 *      result of new elements or changes in ordering by SIF Working Groups.
	 */
	protected ElementSorter( SIFVersion version )
	{
		fVersion = version;
	}

	/**
	 *  Gets an ElementSorter for a given version of SIF
	 *  @param version The version of SIF to sort against
	 */
	public static ElementSorter getInstance( SIFVersion version )
	{
		if( version.compareTo( SIFVersion.SIF20 )  < 0 ){
			return new SIF1xElementSorter( version );
		}
		return new ElementSorter( version );
	}

	/**
	 *  Determines whether Element <i>o1</i>comes before or after Element <i>o2</i>
	 *  given the ElementDef sequence number of the two objects.
	 */
	public int compare( Element o1, Element o2 )
	{
		int cmp1 = o1.getElementDef().getSequence( fVersion );
		int cmp2 = o2.getElementDef().getSequence( fVersion );

		return compareSequences(cmp1, cmp2);
	}

	protected int compareSequences(int cmp1, int cmp2) {
		if( cmp1 < cmp2 )
			return -1;
		if( cmp1 > cmp2 )
			return 1;

		return 0;
	}
	
}
