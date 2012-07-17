//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Mapping {
	abstract public String getKey();
	abstract public Node getNode();
	abstract public Mapping copy( ObjectMapping newParent ) throws ADKMappingException;
	abstract public MappingsFilter getFilter();
	abstract public void toXML( Element element );
}
