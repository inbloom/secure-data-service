//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * 
 * Operators used to logically group condition groups
 * @author Andrew Elmhorst
 *
 */
public enum GroupOperators {
	/**
	 * Signifies no grouping. This value is used when there is only a single SIF_ConditionGroup
	 * present in the SIF Query
	 */
	NONE,
	/**
	 * Logical OR 
	 */
	OR,
	/**
	 * Logical AND
	 */
	AND
}
