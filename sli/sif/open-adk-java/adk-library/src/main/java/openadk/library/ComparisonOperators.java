//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.infra.Operators;

/**
 * Operators used to control how an element value is compared in a SIF_Query
 * @author Andrew Elmhorst
 *
 */
public enum ComparisonOperators {
	/**
	 * the "equals" operator 
	 */
	EQ ( Operators.EQ ),
	/**
	 * the "less than" operator 
	 */
	LT ( Operators.LT ),
	/**
	 * the "greater than" operator 
	 */
	GT ( Operators.GT ),
	/**
	 * the Negation ("not equal") operator 
	 */
	NE ( Operators.NE ),
	/**
	 *  the "less than or equal to" operator
	 */
	LE ( Operators.LE ),
	/**
	 * the "greater than or equal to" operator
	 */
	GE ( Operators.GE );
	
	private Operators fSIFValue;
	
	private ComparisonOperators( Operators sifValue ){
		fSIFValue = sifValue;
	}
	
	public Operators getSIFValue()
	{
		return fSIFValue;
	}
	

}
