//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.util.Map;

public interface IterableFieldAdaptor extends Iterable<FieldAdaptor> {
	/**
	 * Adds a new FieldAdaptor to this collection of FieldAdaptors
	 * @param value
	 */
	public FieldAdaptor addRow();
}
