//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *  A group of query conditions.<p>
 *
 *  A ConditionGroup is a container for Condition objects that are evaluated
 *  together as a group using the Boolean operator passed to the constructor.
 *  ConditionGroups may be nested such that each ConditionGroup is evaluated
 *  using the Boolean operator passed to the constructor.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class ConditionGroup implements Serializable
{
	/** The Conditions and nested ConditionGroups that comprise this group */
	protected List<Serializable> fConditions;

	/** The Boolean operator to use when joining conditions in this group */
	protected GroupOperators fOp;

	/**
	 *  Constructs a ConditionGroup
	 *  @param ops 	The Boolean operator to use when joining conditions in this
	 *      group; either <code>Condition.AND</code> or <code>Condition.OR</code>
	 */
    public ConditionGroup( GroupOperators ops )
	{
		fOp = ops;
    }

	/**
	 *  Gets the Boolean operator for joining all conditions in this group
	 *  @return A constant from the Condition class (e.g. <code>Condition.AND</code>)
	 */
	public GroupOperators getOperator()
	{
		return fOp;
	}

	/**
	 *  Gets the conditions in this group. If the group consists of only nested
	 *  ConditionGroups, an empty array is returned; use the getConditionGroups
	 *  method to retrieve the nested groups.<p>
	 *
	 *  @return An array of Conditions added to this group, or an empty array
	 *      if the group consists of only nested ConditionGroups
	 */
	public Condition[] getConditions()
	{
		List<Object> v = new ArrayList<Object>();

		if( fConditions != null ) {
			for( int i = 0; i < fConditions.size(); i++ ) {
				Object o = fConditions.get( i );
				if( o instanceof Condition ){
					v.add( o );
				}
			}
		}

		Condition[] arr = new Condition[ v.size() ];
		v.toArray( arr );
		return arr;
	}

	/**
	 *  Gets the nested ConditionGroups in this group. If the group does not
	 *  contain any nested ConditionGroups and is comprised of only Condition
	 *  elements, an empty array is returned
	 * @return An array of ConditionGroup
	 */
	public ConditionGroup[] getGroups()
	{
		List<Object> v = new ArrayList<Object>();

		if( fConditions != null ) {
			for( int i = 0; i < fConditions.size(); i++ ) {
				Object o = fConditions.get( i );
				if( o instanceof ConditionGroup ){
					v.add( o );
				}
			}
		}

		ConditionGroup[] arr = new ConditionGroup[ v.size() ];
		v.toArray( arr );
		return arr;
	}

	/**
	 *  Adds a Condition to this group
	 * @param cond The condition to add to this group of conditions
	 */
	public void addCondition( Condition cond )
	{
		if( fConditions == null ){
			fConditions = new ArrayList<Serializable>();
		}

		fConditions.add( cond );
	}

	/**
	 *  Adds a condition to this group.<p>
	 *  
	 *  This method of adding conditions is convenient for adding conditions involving 
	 *  root attributes or elements to a query. If you need to add conditions on deeply
	 *  nested elements, use {@link #addCondition(String, ComparisonOperators, String)}
	 * @param field The metadata element that represents the field, such as 
	 * 			<code>StudentDTD.STUDENTPERSONAL_REFID</code>
	 * @param ops The ComparisonOperator to use for this query condition
	 * @param value The value to compare to this field using the comparison operator
	 */
	public void addCondition( ElementDef field, ComparisonOperators ops, String value )
	{
		addCondition( new Condition( field, ops,value ) );
	}
	
	/**
	 * Internal only. Adds a query condition to this condition group by evaluating the XPath.
	 * If possible, the ElementDef representing the field will be evaluated
	 * @param objectDef The metadata definition of the parent object
	 * @param xPath The xpath to the field
	 * @param ops The ComparisonOperator to apply
	 * @param value The value to compare the field to
	 */
	void addCondition( ElementDef objectDef, String xPath, ComparisonOperators ops, String value )
	{
		addCondition( new Condition( objectDef, xPath, ops,value ) );
	}
	
	/**
	 * 	Add a condition to this group using a deeply nested path. Using this
	 *  method of adding query condition allows for specifying deeply nested query
	 *  conditions. However, the xpath specified here is specific to the version 
	 *  of SIF<p> 
	 *  
	 *  To ensure your code works with all versions  of SIF, you should use 
	 *  {@link #addCondition(ElementDef, ComparisonOperators, String)} whenever possible.
	 *  <p>
	 *
	 * @param xPath The XPath representation of this field
	 * @param ops The ComparisonOperator to apply
	 * @param value The value to compare the field to
	 */
	public void addCondition( String xPath, ComparisonOperators ops, String value )
	{
		addCondition( new Condition( xPath, ops, value ) );
	}

	/**
	 *  Adds a nested ConditionGroup to this group
	 * @param group The ConditionGropu to add to this group
	 */
	public void addGroup( ConditionGroup group )
	{
		if( fConditions == null ){
			fConditions = new Vector<Serializable>();
		}
		fConditions.add( group );
	}

	/**
	 *  Determines if there are any conditions in this group, including any
	 *  nested ConditionGroups
	 * @return True if this group contains any conditions
	 */
	public boolean hasConditions()
	{
		if( fConditions != null )
		{
			for( int i = 0; i < fConditions.size(); i++ ) {
				Object o = fConditions.get( i );
				if( o instanceof Condition )
					return true;
				if( o instanceof ConditionGroup && ((ConditionGroup)o).hasConditions() )
					return true;
			}
		}

		return false;
	}

	/**
	 *  Tests if this ConditionGroup has a Condition for a specific element or
	 *  attribute. Nested ConditionGroups are not included in the search.
	 *  <p>
	 *
	 *  @param elementOrAttr The ElementDef constant from the SIFDTD class that
	 *      identifies the specific attribute or element to search for
	 *  @return The matching Condition object or <code>null</code> if the group
	 *      does not contain a Condition for the specified element or attribute
	 *  
	 *  @deprecated Please use {@link #hasCondition(String)} to support deeply nested 
	 *  		query conditions
	 */
	public Condition hasCondition( ElementDef elementOrAttr )
	{
		if( fConditions != null ) {
			for( Object o : fConditions ) {
				if( o instanceof Condition ){
					Condition cond = (Condition)o;
					if( cond.fField != null && cond.fField.name().equals( elementOrAttr.name() ) ){
						return cond;
					}
				}
			}
		}

		return null;
	}

	/**
	 *  Tests if this ConditionGroup has a Condition for a specific XPath. 
	 *  Nested ConditionGroups are not included in the search.
	 *  <p>
	 *
	 *  @param xPath The xPath representation of the query field. e.g. "Name/FirstName"
	 *  @return The matching Condition object or <code>null</code> if the root 
	 *  	condition group does not contain a Condition for the specified path.
	 */
	public Condition hasCondition( String xPath )
	{
		if( fConditions != null ) {
			for( int i = 0; i < fConditions.size(); i++ ) {
				Object o = fConditions.get(i);
				if( o instanceof Condition && ((Condition)o).fXPath.equals( xPath ) )
					return (Condition)o;
			}
		}

		return null;
	}
	
	
	/**
	 *  Gets the number of conditions in this group.<p>
	 *
	 *  @return The number of conditions that will be returned by the
	 *      getConditions method, or 0 if there are no conditions in the group
	 *      or if the group is comprised of only nested ConditionGroups
	 */
	public int size()
	{
		int cnt = 0;
		if( fConditions != null ) {
			for( int i = 0; i < fConditions.size(); i++ ) {
				Object o = fConditions.get(i);
				if( o instanceof Condition )
					cnt++;
			}
		}

		return cnt;
	}
}
