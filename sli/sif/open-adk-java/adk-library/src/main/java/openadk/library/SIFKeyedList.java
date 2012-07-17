//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.List;

import openadk.library.impl.ElementDefImpl;


public class SIFKeyedList<T extends SIFKeyedElement> 
	extends SIFList<T>
{

	private static final long serialVersionUID = 2L;

	public SIFKeyedList(ElementDef def ) {
		super(def);
	}
	
	
	/**
	 *  Gets the child object with the matching element name and key<p>
	 *  @param name The version-independent element name. Note the element name
	 *      is not necessarily the same as the element tag, which is version
	 *      dependent.
	 *  @param key The key to match
	 *  @return The SIFElement that has a matching element name and key, or null
	 *      if no matches found
	 */
	public SIFElement getChild( String name, String key )
	{
		List<SIFElement> children = _childList();
		synchronized( children )
		{
			for( SIFElement o : children ) {
				if( ((ElementDefImpl)o.fElementDef).internalName().equals(name) && ( key == null || ( o.getKey().equals(key) ) ) )
			    	return o;
			}
		}

		return null;
	}

	/**
	 *  Gets a child object identified by its ElementDef and composite key<p>
	 *  @param id A ElementDef defined by the SIFDTD class to uniquely identify this field
	 *  @param compKey The key values in sequential order
	 */
	public SIFElement getChild( ElementDef id, String[] compKey )
	{
		StringBuffer b = new StringBuffer(compKey[0]);
		for( int i = 1; i < compKey.length; i++ ) {
			b.append(".");
	    	b.append(compKey[i]);
		}

		return getChild(id,b.toString());
	}
}
