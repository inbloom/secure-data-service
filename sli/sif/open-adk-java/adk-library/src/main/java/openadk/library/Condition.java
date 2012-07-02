//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.io.Serializable;

import openadk.library.impl.ElementDefImpl;


/**
 *  A query condition.
 */
public class Condition implements Serializable
{
	/** The field being referenced, or null if it is a deeply-nested query **/
	protected transient ElementDef fField;
	
	/** The XPath  */
	protected String fXPath;

	/** The operator */
	protected ComparisonOperators fOps;

	/** The value to evaluate */
	protected String fValue;


	/**
	 *  Constructs a query condition
	 *
	 *  @param field A static constant defined by the ADK to identify the field 
	 *  (i.e. element or attribute) to evaluate. 
	 *  @param ops The comparison operator from the ComparisonOperators enum
	 *  @param value The value to evaluate
	 */
	public Condition( ElementDef field, ComparisonOperators ops, String value )
	{
		fField = field;
		fOps = ops;
		fValue = value;
		fXPath = field.getSQPPath( ADK.getSIFVersion() );
		
    }
	
	/**
	 *  Constructs a query condition using an xpath query string
	 *
	 *  @param xPath The path to the field. e.g. "Name/FirstName"
	 *  @param ops The comparison operator from the ComparisonOperators enum
	 *  @param value The value to evaluate
	 */
	public Condition( String xPath, ComparisonOperators ops, String value )
	{
		fOps = ops;
		fValue = value;
		fXPath = xPath;
		
    }

	/**
	 * Internal only. Creates a query condition by evaluating the XPath.
	 * If possible, the ElementDef representing the field will be evaluated
	 * @param objectDef The metadata definition of the parent object
	 * @param xPath The xpath to the field
	 * @param ops The ComparisonOperator to apply
	 * @param value The value to compare the field to
	 */
	Condition(ElementDef objectDef, String xPath, ComparisonOperators ops, String value) {
		
		fOps = ops;
		fValue =value;
		fXPath = xPath;
		ElementDef target = ADK.DTD().lookupElementDefBySQP( objectDef, xPath );
		fField = target;
		
	}

	/**
	 *  Gets the comparision operator
	 *  @return A combination of: <code>EQ</code>, <code>LT</code>,
	 *      <code>GT</code>, and <code>NOT</code>
	 */
	public ComparisonOperators getOperator() {
		return fOps;
	}

	/**
	 *  Gets the metadata for the field to evaluate.
	 * @return The ElementDef representing the Query condition
	 * 		or <code>null</code> if the Query condition represents a
	 * 		deeply nested XPath.
	 * @see #getXPath()
	 *  
	 */
	public ElementDef getField() {
		return fField;
	}
	
	/**
	 * Gets the XPath representation of the Query condition. 
	 * 	e.g. <code>"Name/LastName"</code> or <code>"SIF_ExtendedElements/SIF_ExtendedElement[@Name='eyecolor']"</code>
	 * @return The XPath representation of the query
	 */
	public String getXPath()
	{
		return fXPath;
	}
	
	/**
	 * Gets the XPath representation of this query for the specific version of SIF
	 * @param q The Query that this condition is associated with
	 * @param version The version of SIF to use when rendering element names in the path
	 * @return The XPath representation of this query path in the specified version of SIF
	 */
	public String getXPath( Query q,  SIFVersion version )
	{
		return ADK.DTD().translateSQP( q.getObjectType(), fXPath, version );
	}
	

	/**
	 *  Gets the comparison value. The actual type of this value can be a string or a more
	 *  strongly-typed value
	 * @return The
	 */
	public String getValue() {
		return fValue;
	}
	
	
	/**
	 * Parses a grouping operator represented as a string
	 * @param op An operator value such as "And", "Or", "None"
	 * @return the parsed enum value
	 * @throws ADKUnknownOperatorException if the operator is not equal to "And", "Or", or "None"
	 */
	public static GroupOperators parseGroupOperator( String op )
		throws ADKUnknownOperatorException
	{
		if( op.equalsIgnoreCase("And") ){
			return GroupOperators.AND;
		} else if( op.equalsIgnoreCase("Or") ) {
			return GroupOperators.OR; 
		} else if( op.equalsIgnoreCase("None") ) {
			return GroupOperators.NONE;
		}

		throw new ADKUnknownOperatorException( op );
	}
	
	/**
	 * Parses a comparison operator represented as a string
	 * @param op An operator value such as "EQ", "NEQ", "LT", "GT", "GE", "LE"
	 * @return the parsed enum value
	 * @throws ADKUnknownOperatorException If the comparision operator cannot be parsed
	 */
	public static ComparisonOperators parseComparisionOperators( String op )
		throws ADKUnknownOperatorException
	{
		if( op.equalsIgnoreCase("EQ") ){
			return ComparisonOperators.EQ;
		} else if( op.equalsIgnoreCase("LT") ){
			return ComparisonOperators.LT;
		} else if( op.equalsIgnoreCase("LE") ){
			return ComparisonOperators.LE;
		} else if( op.equalsIgnoreCase("GT") ){
			return ComparisonOperators.GT;
		} else if( op.equalsIgnoreCase("GE") ){
			return ComparisonOperators.GE;
		} else if( op.equalsIgnoreCase("NE") ){
			return ComparisonOperators.NE;
		}
		
		throw new ADKUnknownOperatorException( op );
		
	}
	
	private void writeObject( java.io.ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();
	
		ElementDefImpl.writeObject(fField,out);
	}
	
	private void readObject( java.io.ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		fField=ElementDefImpl.readObject(in);
	}	
}
