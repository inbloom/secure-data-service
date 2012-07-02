//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import openadk.library.tools.mapping.IterableFieldAdaptor;


public class StringMapAdaptorTable implements IterableFieldAdaptor {

	private ArrayList<FieldAdaptor> fRows = new ArrayList<FieldAdaptor>();

	public Iterator<FieldAdaptor> iterator() {
		return fRows.iterator();
	}

	public FieldAdaptor addRow() {
		return addRow( new HashMap<String, String>() );
	}

	public FieldAdaptor addRow( Map<String, String> dataRow){
		StringMapAdaptor newRow = new ComplexStringMapAdaptor( dataRow );
		fRows.add( newRow );
		return newRow;
	}

}
