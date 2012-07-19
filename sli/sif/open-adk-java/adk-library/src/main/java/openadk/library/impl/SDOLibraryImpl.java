//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.Map;

import openadk.library.ElementDef;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public abstract class SDOLibraryImpl {

	public abstract void load();
	
	public abstract void addElementMappings( Map<String, ElementDef> dtdMap ); 
	
}
