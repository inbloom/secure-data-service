//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.Element;
import openadk.library.ElementVersionInfo;
import openadk.library.SIFVersion;

public class SIF1xElementSorter extends ElementSorter {

	protected SIF1xElementSorter( SIFVersion version ){
		super( version );
	}
	
	/**
	 *  Determines whether Element <i>o1</i>comes before or after Element <i>o2</i>
	 *  given the ElementDef sequence number of the two objects.
	 */
	public int compare( Element o1, Element o2 )
	{
		Element parent1 = o1.getParent();
		Element parent2 = o2.getParent();
		if( parent1 == parent2 || parent1 == null || parent2 == null )
		{
			return super.compare( o1, o2 );
		}
		
		// One of these elements has a parent that was collapsed and it is now
		// being compared with it's uncles and aunts, rather than its siblings
		// The logic is simple: Determine which element is the niece or nephew. That
		// element will use it's parent sequence to compare with the relative.
		if( parent1.getParent() == parent2 ){
			int cmp1 = parent1.getElementDef().getSequence( fVersion );
			int cmp2 = o2.getElementDef().getSequence( fVersion );
			return compareSequences( cmp1, cmp2 );
		} else if( parent2.getParent() == parent1 ){
			int cmp1 = o1.getElementDef().getSequence( fVersion );
			int cmp2 = parent2.getElementDef().getSequence( fVersion );
			return compareSequences( cmp1, cmp2 );
		} else if ( parent1.getParent() == parent2.getParent() ){
			int cmp1 = parent1.getElementDef().getSequence( fVersion );
			int cmp2 = parent2.getElementDef().getSequence( fVersion );
			return compareSequences( cmp1, cmp2 );
		}
		// Indeterminate. Do the safe thing and exit gracefully
		return super.compare( o1, o2 );
		
	}

}
