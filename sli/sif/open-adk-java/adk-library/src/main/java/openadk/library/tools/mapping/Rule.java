//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;


import openadk.library.*;
import openadk.library.tools.xpath.SIFXPathContext;

import org.w3c.dom.*;

/**
 *  The abstract base class for all Mappings rules
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public abstract class Rule
{
	
	/**
	 *  Evaluates this rule against a SIFDataObject and returns the text value
	 *  of the element or attribute that satisfied the query.<p>
	 *
	 * @param xpathContext The SIFXPathContext instance to use for object traversal
	 * @param version The SIF Version that is in effect
	 * @return The SimpleType representing the value of the element or attribute that satisfied the
	 *      query, or null if no match found
	 * @throws ADKSchemaException If the xpath expression cannot be resolved
	 */
	public abstract SIFSimpleType evaluate( SIFXPathContext xpathContext, SIFVersion version )
		throws ADKSchemaException;

	/**
	 *  Produces a duplicate of this Rule object<p>
	 * @param newParent The parent to copy this rule to
	 *
	 *  @return A "deep copy" of this Rule object
	 * @throws ADKMappingException If the rule cannot be copied
	 */
	public abstract Rule copy( FieldMapping newParent )
		throws ADKMappingException;

	/**
	 *  Render this Rule as an DOM Node
	 *  @param parent The parent Node
	 */
	public abstract void toXML( Node parent );

	
}
