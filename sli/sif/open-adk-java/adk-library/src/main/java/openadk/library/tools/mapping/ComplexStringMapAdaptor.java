//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import openadk.library.SIFFormatter;



public class ComplexStringMapAdaptor extends StringMapAdaptor implements
		ComplexFieldAdaptor {

	private HashMap<String, StringMapAdaptorTable> fChildRelations
			= new HashMap<String, StringMapAdaptorTable>();

	public ComplexStringMapAdaptor(Map dataMap) {
		super(dataMap);
	}

	public ComplexStringMapAdaptor(Map dataMap, SIFFormatter formatter) {
		super(dataMap, formatter);
	}
	
	/**
	 * Gets the keyset of the underlying Child Relationship
	 * @return the keyset of the underlying Relationship
	 */
	public Set<String> getChildRelationshipKeySet()
	{
		return fChildRelations.keySet();
	}

	/**
	 * Gets the <code>Child Relationship Map</code> being used for SIF data list mapping operations
	 * @return The <code>Map</code> being used for SIF data list mapping operations
	 */
	public Map<String, StringMapAdaptorTable> getChildRelationshipMap()
	{
		return fChildRelations;
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.tools.mapping.ComplexFieldAdaptor#getChildRelationship(java.lang.String, com.edustructures.sifworks.tools.mapping.IterableFieldAdaptor)
	 */
	public IterableFieldAdaptor getChildRelationship( String relationshipName ) {
		return fChildRelations.get( relationshipName );
	}

	public synchronized void addRelatedDataRow( String relationshipName, Map<String, String> dataRow ){

		StringMapAdaptorTable childTable = fChildRelations.get( relationshipName );
		if( childTable == null ){
			childTable = new StringMapAdaptorTable();
			fChildRelations.put( relationshipName, childTable );
		}
		childTable.addRow( dataRow );
	}

	public IterableFieldAdaptor addChildRelationship(String relationshipName)
			throws UnsupportedOperationException, IllegalStateException {
		if( fChildRelations.containsKey( relationshipName )){
			throw new IllegalStateException( "Child relationship already exists: " + relationshipName );
		}
		StringMapAdaptorTable childTable = new StringMapAdaptorTable();
		fChildRelations.put( relationshipName, childTable );
		return childTable;
	}


}
