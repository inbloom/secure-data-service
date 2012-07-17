//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.util.Map;

public interface ComplexFieldAdaptor extends FieldAdaptor {
		/**
		 * Returns a child table of data related to the parent object.
		 * @param relationShipName The name of the related rowset of data.
		 * @return a child rowset with the specified name or <c>NULL</c> if there
		 * is no child relationship with the specified name.
		 */
		public IterableFieldAdaptor getChildRelationship( String relationShipName );

		/**
		 * Adds a new child relationship to the parent object
		 * @param relationShipName The name of the related rowset of data.
		 * @return the newly created childRelationship
		 * @throws UnsupportedOperationException If adding child relationships is not
		 * supported by this adapter
		 * @throws IllegalStateException if the specified relationship already exists
		 */
		public IterableFieldAdaptor addChildRelationship(String string)
			throws UnsupportedOperationException, IllegalStateException;
		}
