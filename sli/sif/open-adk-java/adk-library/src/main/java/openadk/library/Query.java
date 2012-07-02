//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.Collator;
import java.util.*;

import openadk.library.impl.ElementDefImpl;
import openadk.library.impl.SIFPrimitives;
import openadk.library.infra.SIF_Condition;
import openadk.library.infra.SIF_ConditionGroup;
import openadk.library.infra.SIF_Conditions;
import openadk.library.infra.SIF_Element;
import openadk.library.infra.SIF_Query;
import openadk.library.infra.SIF_QueryObject;
import openadk.library.infra.SIF_Request;
import openadk.library.tools.xpath.SIFXPathContext;


/**
 *  Encapsulates a SIF Query.<p>
 *
 *  An instance of this class is passed to the <code>Zone.query</code> and
 *  <code>Topic.query</code> methods when issuing a SIF Request. A Query object
 * 	defines the following parameters to the request:
 *
 * 	<ul>
 * 		<li>The type of SIF Data Object to query for</li>
 * 		<li>Conditions: One or more conditions may be placed on the query to
 * 			select a subset of objects from the responder (when no conditions are
 * 			present the responder returns all objects)</li>
 * 		<li>Field Restrictions: An optional list of elements to include in
 * 			responses to the query (when no field restrictions are present the
 * 			responder returns the full set of elements for each object)</li>
 * 	</ul>
 * 	<p>
 *
 * 	To construct a simple Query to query for all objects with no conditions or
 * 	field restrictions, call the constructor that accepts an ElementDef constant
 * 	from the SIFDTD class:
 * 	<p>
 *
 * 	<blockquote>
 * 	<code>
 * 	Query myQuery = new Query( SIFDTD.STUDENTPERSONAL );<br/>
 * 	</code>
 * 	</blockquote>
 *
 * 	More complex queries can be constructed by specifying conditions and field
 * 	restrictions.<p>
 *
 *  <b>Conditions</b><p>
 *  A Query may optionally specify one or more conditions to restrict the number
 *  of objects returned by the responder. (Refer to the SIF Specification for a
 * 	detailed description of how query conditions may be constructed.) When no
 * 	conditions are specified, the responder interprets the query to mean "all
 * 	objects". Note SIF 1.0r2 and earlier limit queries such that only root-level
 * 	attributes may be included in query conditions, and only the equals ("EQ")
 * 	comparison operator may be used. SIF 1.1 and later allow agents to query for
 * 	elements within an object, but responders may return an error if they do not
 * 	support that functionality.
 *  <p>
 *
 *	Query conditions are encapsulated by the ADK's ConditionGroup class, which is
 *	used to build SIF_ConditionGroup, SIF_Conditions, and SIF_Condition elements
 *	when the class framework sends a SIF_Request message to a zone. Every Query
 *	with conditions has a root ConditionGroup with one or more child ConditionGroups.
 *	Unless you construct these groups manually, the Query class will automatically
 *	establish a root ConditionGroup and a single child when the <code>addCondition</code>
 *	method is called. Use the <code>addCondition</code> method to add conditions
 *	to a Query. Note the form of Query constructor you call determines how the
 *	<code>addCondition</code> method works. If you call the default constructor,
 *	the ADK automatically establishes a root SIF_ConditionGroup with a Type
 *	attribute of "None", and a single SIF_Conditions child with a Type attribute
 *	of "And". ("None" will be used if the query has only one condition.)
 *	SIF_Condition elements are then added to this element whenever the
 *	<code>addCondition</code> method is called.<p>
 *
 *	For example,<p>
 *
 *	<blockquote>
 *	<code>
 *  // Query for a single student by RefId<br/>
 *  Query query = new Query( SIFDTD.STUDENTPERSONAL );<br/>
 *  query.addCondition(<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_REFID, Condition.EQ,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;"4A37969803F0D00322AF0EB969038483" );<br/>
 * 	</code>
 * 	</blockquote>
 *
 *  If you want to specify the "Or" comparision operator instead of the default
 * 	of "And", call the constructor that accepts a constant from the Condition
 * 	class.<p>
 *
 * 	For example,<p>
 *
 * 	<blockquote>
 *	<code>
 *  // Query for student where the RefId is A, B, or C<br/>
 *  Query query = new Query( SIFDTD.STUDENTPERSONAL, Condition.OR );<br/>
 *  <br/>
 *  query.addCondition(<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_REFID, Condition.EQ,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;"4A37969803F0D00322AF0EB969038483" );<br/>
 *  query.addCondition(<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_REFID, Condition.EQ,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;"5A37969803F0D00322AF0EB969038484" );<br/>
 *  query.addCondition(<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_REFID, Condition.EQ,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;"6A37969803F0D00322AF0EB969038485" );<br/>
 * 	</code>
 *  </blockquote>
 *
 * 	The above examples show how to add simple conditions to a Query. To construct
 * 	complex queries with nested groups of conditions, create your own root
 * 	SIF_ConditionGroup object by calling the form of constructor that
 * 	accepts a ConditionGroup instance. You can specify nested ConditionGroup
 * 	children of this root object.
 *  <p>
 *
 * 	For example,<p>
 *
 * 	<blockquote>
 *	<code>
 *  // Query for student where the Last Name is Jones and the First Name is<br/>
 *	// Bob, and the graduation year is 2004, 2005, or 2006<br/>
 *	ConditionGroup root = new ConditionGroup( Condition.AND );<br/>
 *	ConditionGroup grp1 = new ConditionGroup( Condition.AND );<br/>
 *	ConditionGroup grp2 = new ConditionGroup( Condition.OR );<br/>
 *	<br/>
 *	// For nested elements, you cannot reference a SIFDTD constant. Instead, use<br/>
 *	// the lookupElementDefBySQL function to lookup an ElementDef constant<br/>
 *	// given a SIF Query Pattern (SQP)<br/>
 *	ElementDef lname = ADK.DTD().lookupElementDefBySQP(<br/>
 *	&nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL, "Name/LastName" );</br>
 *	ElementDef fname = ADK.DTD().lookupElementDefBySQP(<br/>
 *	&nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL, "Name/FirstName" );</br>
 *  grp1.addCondition( lname, Condition.EQ, "Jones" );<br/>
 * 	grp1.addCondition( fname, Condition.EQ, "Bob" );<br/>
 *	<br/>
 *  grp2.addCondition( SIFDTD.STUDENTPERSONAL_GRADYEAR, Condition.EQ, "2004" );<br/>
 *  grp2.addCondition( SIFDTD.STUDENTPERSONAL_GRADYEAR, Condition.EQ, "2005" );<br/>
 *  grp2.addCondition( SIFDTD.STUDENTPERSONAL_GRADYEAR, Condition.EQ, "2006" );<br/>
 * 	<br/>
 * 	// Add condition groups to the root group<br/>
 * 	root.addGroup( grp1 );<br/>
 * 	root.addGroup( grp2 );<br/>
 * 	<br/>
 *  // Query for student with the conditions prepared above by passing the<br/>
 * 	// root ConditionGroup to the constructor<br/>
 *  Query query = new Query( SIFDTD.STUDENTPERSONAL, root );<br/>
 * 	</code>
 * 	</blockquote>
 *
 *  <b>Field Restrictions</b><p>
 *  If only a subset of elements and attributes are requested, use the
 *  <code>setFieldRestrictions</code> method to indicate which elements and
 *  attributes should be returned to your agent by the responder. For example,
 *  to request the &lt;StudentPersonal&gt; object with RefId "4A37969803F0D00322AF0EB969038483"
 *  but to only include the <code>RefId</code> attribute and <code>Name</code>
 *  and <code>PhoneNumber</code> elements in the response,<p>
 *
 *  <blockquote>
 *  <code>
 *
 *  // Query for a single student by RefId<br/>
 *  Query query = new Query( SIFDTD.STUDENTPERSONAL );<br/>
 *  <br/>
 *  query.addCondition(<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_REFID, Condition.EQ,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;"4A37969803F0D00322AF0EB969038483" );<br/>
 *
 *  query.setFieldRestrictions(<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;new ElementDef[] {<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_REFID,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_NAME,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFDTD.STUDENTPERSONAL_PHONENUMBER<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;}
 *  );
 *  </code>
 *  </blockquote>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class Query implements Serializable
{
	/** The object to query */
	protected transient ElementDef fObjType;

	/** The version of SIF associated with the query */
	protected SIFVersion[] fVersions = new SIFVersion[0];


	/** Root condition groups */
	protected ConditionGroup fRoot;

	/**
	 * The SIF Context that this Query applies to
	 */
	private SIFContext fContext = SIFContext.DEFAULT;

	/**
	 * Fields to include in the result of the query (null = all fields
	 */
	protected List<ElementRef> fFieldRestrictions;

	/**
	 * The locale-specific collator used for string comparisons when evaluating query conditions
	 */
	protected Collator fCollator;

	/**
	 * User State
	 */
	protected Serializable fUserData;


	/**
	 *  Constructs a Query object with no initial conditions or field
	 *  restrictions. If conditions are subsequently added to the Query, they
	 *  will be evaluated as a group with the logical AND operator. To specify
	 *  that the logical OR operator be used, call the form of constructor that
	 *  accepts an alternate operator.
	 *  <p>
	 *
	 *  @param objectType An ElementDef describing the object type to query (e.g.
	 *      <code>ADK.DTD.STUDENTPERSONAL</code>)
	 */
	public Query( ElementDef objectType )
	{
		if( !objectType.isObject() )
			throw new IllegalArgumentException("\""+objectType.name()+"\" is not a root-level SIF Data Object");

		fObjType = objectType;
		fRoot = null;
	}

	/**
	 *  Constructs a Query object with one ConditionGroup where all conditions
	 *  in the group are evaluated using the supplied boolean operator (either
	 *  <code>Condition.AND</code> or <code>Condition.OR</code>). All Conditions
	 *  subsequently added to this Query will be placed into the ConditionGroup
	 *  created by the constructor.<p>
	 *
	 *  This constructor is provided as a convenience so that callers do
	 *  not have to explicitly create a ConditionGroup for simple queries.<p>
	 *
	 *  @param objectType An ElementDef describing the object type to query (e.g.
	 *      <code>ADK.DTD.STUDENTPERSONAL</code>)
	 *  @param logicalOp The logical operator that defines how to compare this group
	 *      with other condition groups that comprise the query (e.g. GroupOperators.OR)
	 */
	public Query( ElementDef objectType, GroupOperators logicalOp )
	{
		if( !objectType.isObject() )
			throw new IllegalArgumentException("\""+objectType.name()+"\" is not a root-level SIF Data Object");

		fObjType = objectType;
		fRoot = new ConditionGroup( logicalOp );
	}

	/**
	 *  Constructs a Query object with a ConditionGroup.
	 *  <p>
	 *
	 *  @param objectType An ElementDef describing the object type to query (e.g.
	 *      <code>ADK.DTD.STUDENTPERSONAL</code>)
	 *  @param conditions A ConditionGroup comprised of one or more query Conditions
	 */
	public Query( ElementDef objectType, ConditionGroup conditions )
	{
		if( !objectType.isObject() )
			throw new IllegalArgumentException("\""+objectType.name()+"\" is not a root-level SIF Data Object");

		fObjType = objectType;
		fRoot = conditions;
	}

	/**
	 *  Constructs a Query object from a SIF_QueryObject.<p>
	 *
	 *  This constructor is not typically called by agents but is used internally
	 *  by the class framework. The other constructors can be used to safely
	 *  create Query instances to request a specific SIF Data Object. Use the
	 *  <code>addCondition</code> and <code>setFieldRestrictions</code> methods
	 *  to further define the conditions and SIF elements specified by the query.<p>
	 *
	 *  @param query A SIF_Query object received in a SIF_Request message
	 * @throws ADKUnknownOperatorException If one of the operators in the SIF_Query is
	 * unrecognized by the ADK
	 * @throws ADKSchemaException If the object or elements defined in the query or
	 * not recognized by the ADK
	 */
	public Query( SIF_Query query )
		throws ADKUnknownOperatorException,
			   ADKSchemaException
	{

		SIF_QueryObject qo = query.getSIF_QueryObject();
		if( qo == null )
			throw new IllegalArgumentException("SIF_Query must have a SIF_QueryObject element");

		fObjType = ADK.DTD().lookupElementDef( qo.getObjectName() );
		if( fObjType == null ) {
			throw new ADKSchemaException( qo.getObjectName() + " is not a recognized SIF Data Object, or the agent is not configured to support this object type" );
		}
		fRoot = null;

		SIF_ConditionGroup cg = query.getSIF_ConditionGroup();
		if( cg != null && cg.getSIF_Conditionses() != null )
		{
			GroupOperators grpOp = GroupOperators.NONE;

			try {
				grpOp = Condition.parseGroupOperator(cg.getType());
			} catch( ADKUnknownOperatorException uoe ) {
				grpOp = GroupOperators.NONE;
			}

			fRoot = new ConditionGroup(grpOp);

			SIF_Conditions[] sifConds = cg.getSIF_Conditionses();

			if( sifConds.length == 1 )
			{
				//  There is one SIF_ConditionGroup with one SIF_Conditions,
				//  so just add all of the conditions (no nested groups)
				String typ = sifConds[0].getType();
				if( typ == null )
					throw new ADKSchemaException( "SIF_Conditions/@Type is a required attribute" );

				fRoot.fOp = Condition.parseGroupOperator( typ );
				SIF_Condition[] clist = sifConds[0].getSIF_Conditions();
				populateConditions( query, clist, fRoot );
			}
			else
			{
				//  There are multiple SIF_Conditions, so add each as a nested
				//  ConditionGroup of the fRoot
				for( int i = 0; i < sifConds.length; i++ )
				{
					ConditionGroup nested = new ConditionGroup( Condition.parseGroupOperator( sifConds[i].getType() ) );
					populateConditions( query, sifConds[i].getSIF_Conditions(), nested );
					fRoot.addGroup( nested );
				}
			}
		}

		SIFVersion[] reqVersions = null;
		// First, try to get the version from the SIF_Request
		Element parent = query.getParent();
		if( parent != null )
		{
			if(  parent instanceof SIF_Request ){
				SIF_Request request = ( SIF_Request )parent;
				//There was some kind of bad character or something in this source here, adding comment to force diff
				SIFVersion[] versions = request.parseRequestVersions( ADK.getLog() );
				if( versions.length > 0 ){
					reqVersions = versions;
				}

			}
		}
		if( reqVersions == null ){
			SIFVersion version = query.effectiveSIFVersion();
			if( version != null ){
				reqVersions = new SIFVersion[] { version };
			}
		}

		if( reqVersions == null || reqVersions.length == 0 ){
			throw new IllegalArgumentException( "SIF_Query is not contained in a SIF_Request that has a SIF_Version element; cannot determine version of SIF to associated with this Query object" );
		} else {
			fVersions = reqVersions;
		}

		SIF_Element[] fields = query.getSIF_QueryObject().getSIF_Elements();
		if( fields != null && fields.length > 0 )
		{
			for( int i = 0; i < fields.length; i++ )
			{
				String xPath = fields[i].getTextValue();
				if( xPath == null || xPath.length() == 0 ){
					continue;
				}
				addFieldRestriction( xPath );
		    }
		}
	}

	private void populateConditions( SIF_Query query, SIF_Condition[] clist, ConditionGroup target )
		throws ADKUnknownOperatorException,
			   ADKSchemaException
	{
		for( int i = 0; i < clist.length; i++ )
		{
			String o = clist[i].getSIF_Operator();
			ComparisonOperators ops = Condition.parseComparisionOperators( o );
			String val = clist[i].getSIF_Value();
			String path = clist[i].getSIF_Element();
			target.addCondition( fObjType, path, ops, val );
		}
	}

	/**
	 * Returns the XML representation of this Query in the format required by SIF
	 * @return a string containing the XML representation as a SIF_Query element. If an error
	 * occurs during the conversion, an empty string ("") is returned.
	 */
	public String toXML()
	{
		return toXML( this.getEffectiveVersion() );
	}

	/**
	 * Returns the XML representation of this Query in the format required by SIF
	 * for the specified version
	 * @param version The SIF Version to render the Query in. The ADK will attempt to render
	 * 	the query path using the proper element or attribute names for the version of SIF
	 * @return a string containing the XML representation as a SIF_Query element. If an error
	 * occurs during the conversion, an empty string ("") is returned.
	 */
	public String toXML( SIFVersion version )
	{
		// Create a SIF_Query object
		SIF_Query sifQ = SIFPrimitives.createSIF_Query( this, version, true );
		StringWriter out = null;
		SIFWriter w = null;
		try
		{
			out = new StringWriter();
			w = new SIFWriter(out,false);
			w.write(sifQ);
			w.flush();

			return out.toString();
		}
		catch( Exception e )
		{
			ADK.getLog().warn( "Error creating XML equivalent of Query: " + e, e );
			return "";
		}
		finally
		{
			try {
				if( out != null )
	    			out.close();
		    	if( w != null )
			    	w.close();
			} catch( Exception ignored ) {
				ADK.getLog().warn( "Error closing writer: " + ignored, ignored );
			}
		}
	}

	/**
	 * Returns the SIF_Query representation of this Query in the format required by SIF
	 * @return A SIF_Query element.
	 */
	public SIF_Query toSIF_Query()
	{
		return toSIF_Query( ADK.getSIFVersion() );
	}

	/**
	 * Returns the SIF_Query representation of this Query in the format required by SIF
	 * for the specified version
	 * @param version The SIF Version to render the Query in. The ADK will attempt to render
	 * 	the query path using the proper element or attribute names for the version of SIF
	 * @return A SIF_Query element.
	 */
	public SIF_Query toSIF_Query( SIFVersion version )
	{
		return SIFPrimitives.createSIF_Query( this, version, true );
	}

	/**
	 *  Gets the object type being queried<p>
	 *  @return The name of the object passed to the constructor
	 */
	public ElementDef getObjectType() {
		return fObjType;
	}

	/**
	 *  Gets the tag name of the object type being queried<p>
	 *  @return The tag name of the object passed to the constructor
	 */
	public String getObjectTag() {
		return fObjType.tag( ADK.getLatestSupportedVersion( fVersions ) );
	}

	/**
	 * Gets the custom state object associated with this query
	 * @return The custom data that was set to {@link #setUserData(Serializable)}
	 */
	public Serializable getUserData()
	{
		return fUserData;
	}

	/**
	 * Allows a custom state object to be associated with a query
	 * @param userData A custom state object that implements the Serializable interface
	 */
	public void setUserData( Serializable userData )
	{
		fUserData = userData;
	}

	/**
	 *  Add a condition to this query.<p>
	 *
	 *  This method of adding conditions is convenient for adding conditions involving
	 *  root attributes or elements to a query. If you need to add conditions on deeply
	 *  nested elements, use {@link #addCondition(String, ComparisonOperators, String)}
	 *
	 *  @param field A constant from the SIFDTD class that identifies an element
	 *      or attribute of the data object (e.g. <code>SIFDTD.STUDENTPERSONAL_REFID</code>).
	 *
	 *  @param ops The comparison operator. Comparison operator constants are
	 *      defined by the ComparisionOperators enum
	 *  @param value The data that is used to compare to the element or attribute
	 *  @throws IllegalArgumentException if the ElementDef does not represent an immediate
	 *  child of the object being queried.
	 */
	public void addCondition( ElementDef field, ComparisonOperators ops, String value )
	{
		// Do some validation to try to prevent invalid query paths from being created
		String relativePath = field.getSQPPath( ADK.getSIFVersion() );
		ElementDef lookedUp = ADK.DTD().lookupElementDefBySQP( fObjType, relativePath );
		if( lookedUp == null ){
			throw new IllegalArgumentException( "Invalid path: " + fObjType.name() + "/" + relativePath + " is unable to be resolved" );
		}

		addCondition( new Condition( fObjType, relativePath, ops, value ) );

	}

	/**
	 * Add a condition to this query.
	 * @param condition The condition to add. This condition is added to the root
	 * condition group.
	 * @see #getRootConditionGroup()
	 * @see #getConditions()
	 */
	public void addCondition( Condition condition )
	{
		if( fRoot == null ){
			fRoot = new ConditionGroup( GroupOperators.AND );
		}
		fRoot.addCondition( condition );
	}



	/**
	 *  Add a condition to this query using a deeply nested path. Using this
	 *  method of adding query condition allows for specifying deeply nested query
	 *  conditions. However, the xpath specified here is specific to the version
	 *  of SIF<p>
	 *
	 *  To ensure your code works with all versions  of SIF, you should use
	 *  {@link #addCondition(ElementDef, ComparisonOperators, String)} whenever possible.
	 *  <p>
	 *
	 *  @param xPath The Simple XPath to use for this query condition. E.g.
	 *  	SIF_ExendedElements/SIF_ExtendedElement[@Name='eyecolor']
	 *
	 *  @param ops The comparison operator. Comparison operator value from the
	 *  ComparisonOperators enum
	 *
	 *  @param value The data that is used to compare to the element or attribute
	 */
	public void addCondition( String xPath, ComparisonOperators ops, String value )
	{
		addCondition( new Condition( fObjType, xPath, ops, value ) );
	}

	/**
	 *  Add a condition to this query. This form of the <code>addCondition</code>
	 *  method is intended to be called internally by the ADK when parsing an
	 *  incoming SIF_Query element. To ensure your code works with all versions
	 *  of SIF, you should use the other form of this method that accepts an
	 *  ElementDef constant for the <i>field</i> parameter whenever possible.
	 *  <p>
	 *
	 *  @param field Identifies an element or attribute of the data object in
	 *      SIF Query Pattern form as described by the SIF 1.0r2 Specification
	 *      (e.g. "@RefId").  With SIF 1.0r2 and earlier, only root-level
	 *      attributes may be specified in a query. Note this string is specific
	 *      to the version of SIF associated with the Query as element and
	 *      attribute names may vary from one version of SIF to the next. The
	 *      version defaults to the version of SIF in effect for the agent or the
	 *      version of SIF associated with the <code>SIF_Query</code> object
	 *      passed to the constructor.
	 *
	 *  @param ops A value from the ComparisonOperators enum
	 *
	 *  @param value The data that is used to compare to the element or attribute
	 */
	public void addCondition( String field, String ops, String value )
	{
		try {
	    	addCondition(
	    			field, Condition.parseComparisionOperators( ops ), value );
		} catch( ADKUnknownOperatorException uoe ) {
			addCondition(field, ComparisonOperators.EQ, value );
		}
	}

	/**
	 *  Restricts the query to a specific field (i.e. element or attribute) of
	 *  the data object being requested. If invoked, the results of the query
	 *  will only contain the elements or attributes specified by the fields for
	 *  which this method is called (call this method repeatedly for each field).
	 *  Otherwise, the results will contain a complete object.
	 *
	 *  @param field A <code>ElementDef</code> object defined by the static
	 *      constants of the <code>SIFDTD</code> class. For example, to restrict
	 *      a query for the StudentPersonal topic to include only the StatePr
	 *      element of the student address, pass <code>SIFDTD.ADDRESS_STATEPR</code>.
	 *      This would cause the query results to include only
	 *      <code>StudentPersonal/Address/StatePr</code> elements.
	 */
	public synchronized void addFieldRestriction( ElementDef field ) {
		if( field == null ){
			throw new IllegalArgumentException( "Field cannot be null" );
		}

		if( fFieldRestrictions == null ) {
			fFieldRestrictions = new ArrayList<ElementRef>();
		}

		fFieldRestrictions.add( new ElementRef( fObjType, field, getEffectiveVersion() ) );
	}

	/**
	 *  Restricts the query to a specific field (i.e. element or attribute) of
	 *  the data object being requested. If invoked, the results of the query
	 *  will only contain the elements or attributes specified by the fields for
	 *  which this method is called (call this method repeatedly for each field).
	 *  Otherwise, the results will contain a complete object.
	 *
	 *  @param xPath The relative XPath to the referenced field
	 */
	public synchronized void addFieldRestriction( String xPath ) {
		if( xPath == null || xPath.length() == 0 ){
			throw new IllegalArgumentException( "Field cannot be null or zero-length : " + xPath );
		}

		if( fFieldRestrictions == null ) {
			fFieldRestrictions = new ArrayList<ElementRef>();
		}

		fFieldRestrictions.add( new ElementRef( fObjType, xPath, getEffectiveVersion() ) );
	}


	/**
	 *  Restricts the query to the specified elements and attributes
	 *  @param fields An array of ElementDef objects identifying one or more
	 *      elements and/or attributes
	 */
	public void setFieldRestrictions( ElementDef[] fields ) {
		if( fFieldRestrictions != null ){
			fFieldRestrictions.clear();
		}
		for( ElementDef def : fields ){
			addFieldRestriction( def );
		}
	}

	/**
	 *  Gets the fields to include in the result of the query.<p>
	 *  @return An array of fields that should be included in the results of
	 *      this query, or null if all fields are to be included
	 */
	public ElementDef[] getFieldRestrictions() {
		if( fFieldRestrictions == null ){
			return null;
		}

		ElementDef[] returnValue = new ElementDef[ fFieldRestrictions.size() ];
		for( int i = 0; i < returnValue.length; i++ ){
			returnValue[i] = fFieldRestrictions.get( i ).getField();
		}

		return returnValue;
	}

	/**
	 *  Gets the fields to include in the result of the query.<p>
	 *  @return An array of field references that should be included in the results of
	 *      this query, or null if all fields are to be included
	 */
	public List<ElementRef> getFieldRestrictionRefs() {
		return fFieldRestrictions;
	}

	/**
	 *  Gets the conditions placed on this query.<p>
	 *  @return An array of ConditionGroup objects in evaluation order. The
	 * 		children of the root ConditionGroup are returned. If no conditions
	 * 		have been specified, an empty array is returned.
	 */
	public ConditionGroup[] getConditions()
	{
		if( fRoot == null )
			return new ConditionGroup[0];

		ConditionGroup[] groups = fRoot.getGroups();
		if( groups != null && groups.length > 0 )
			return groups;

		//	There is a fRoot group -- which means the user must have called
		//	the default constructor and then called addCondition() to add one
		//	or more conditions -- but the root group does not itself have any
		//	nested groups. So, just return the root group...

		return new ConditionGroup[] { fRoot };
	}

	/**
	 * 	Gets the root ConditionGroup.<p>
	 * 	@return The root ConditionGroup that was established by the constructor.
	 * 		If this query has no conditions, null is returned.
	 */
	public ConditionGroup getRootConditionGroup()
	{
		return fRoot;
	}

	/**
	 *  Determines if this Query has any conditions
	 *  @return true if the query has one or more conditions
	 *  @see #getConditions
	 *  @see #addCondition(ElementDef, ComparisonOperators, String)
	 */
	public boolean hasConditions()
	{
		return fRoot != null && fRoot.hasConditions();
	}

	/**
	 *  Determines if this Query has any field restrictions
	 *  @return true if the query specifies a subset of fields to be returned;
	 *      false if the query returns all elements and attributes of each object
	 *      matching the query conditions
	 *  @see #getFieldRestrictions
	 *  @see #addFieldRestriction
	 *  @see #setFieldRestrictions
	 */
	public boolean hasFieldRestrictions() {
		return fFieldRestrictions != null && fFieldRestrictions.size() > 0;
	}

	/**
	 *  Tests if this Query has a specific element or attribute condition<p>
	 *  @param elementOrAttr The ElementDef constant from the SIFDTD class that
	 *      identifies the specific attribute or element to search for
	 *  @return The Condition object representing the condition. If no
	 *      Condition exists for the element or attribute, null is returned
	 */
	public Condition hasCondition( ElementDef elementOrAttr )
	{
		ConditionGroup[] grps = getConditions();

		for( int i = 0; i < grps.length; i++ ) {
			Condition c = grps[i].hasCondition( elementOrAttr );
			if( c != null )
				return c;
		}

		return null;
	}

	/**
	 *  Tests if this Query has a condition referencing a specific xPath<p>
	 *  @param xPath The Xpath which identifies the specific attribute or element to search for
	 *  @return The Condition object representing the condition. If no
	 *      Condition exists for the element or attribute, null is returned
	 */
	public Condition hasCondition( String xPath )
	{
		ConditionGroup[] grps = getConditions();

		for( int i = 0; i < grps.length; i++ ) {
			Condition c = grps[i].hasCondition( xPath );
			if( c != null )
				return c;
		}

		return null;
	}

	/**
	 *  Sets the value of the SIF_Request/SIF_Version element. By default,
	 *  this value is set to the version of SIF declared for the agent when the
	 *  ADK was initialized.<p>
	 *
	 *  @param versions The version(s) of SIF the responding agent should use when
	 *      returning SIF_Response messages for this query
	 */
	public void setSIFVersions( SIFVersion... versions ) {
		fVersions = versions;
	}

	/**
	 *  Sets the value of the SIF_Request/SIF_Version element. By default,
	 *  this value is set to the version of SIF declared for the agent when the
	 *  ADK was initialized.<p>
	 *
	 *  @return The version of SIF the responding agent should use when
	 *      returning SIF_Response messages for this query
	 */
	public SIFVersion[] getSIFVersions() {
		return fVersions;
	}


	/**
	 *  From the list of SIFVersions associated with this Query, returns the latest SIFVersion
	 *  supported by the current ADK instance.
	 *  @see ADK#getLatestSupportedVersion(SIFVersion[])
	 *  @return The latest SIFVersion supported by the ADK for this Query
	 */
	public SIFVersion getEffectiveVersion()
    {
        return ADK.getLatestSupportedVersion( fVersions );
    }


	/**
	 * 	Sets the root ConditionGroup.<p>
	 *
	 * 	By default a Query is constructed with a ConditionGroup to which
	 * 	individual conditions will be added by the <code>addCondition</code>
	 * 	methods. You can call this method to prepare a ConditionGroup ahead of
	 * 	time and replace the default with your own.<p>
	 *
	 *  Note calling this method after <code>addCondition</code> will replace
	 * 	any conditions previously added to the Query with the conditions in the
	 * 	supplied ConditionGroup.<p>
	 * @param root The root ConditionGroup to use for this query
	 */
	public void setConditionGroup( ConditionGroup root ) {
		fRoot = root;
	}

	/**
	 * Evaluate the given the SIFDataObject against the conditions provided in the
	 * Query. All conditions are evaluated using standard string comparisons using
	 * the system's locale-specific collator
	 * @param obj The SIFDataObject to evalaute against this query
	 * @return TRUE if the SIFDataObject satisfies the conditions in the Query, otherwise FALSE
	 * @throws ADKSchemaException If the condition contains references to invalid elements
	 */
	public boolean evaluate( SIFDataObject obj ) throws ADKSchemaException
	{
		return evaluate( obj, getCollator() );
	}

	/**
	 * Evaluate the given the SIFDataObject against the conditions provided in the
	 * Query. All conditions are evaluated using the provided comparer
	 * @param obj The SIFDataObject to evalaute against this query
	 * @param comparer The comparer used to do comparisons
	 * @return TRUE if the SIFDataObject satisfies the conditions in the Query, otherwise FALSE
	 * @throws ADKSchemaException If the condition contains references to invalid elements
	 */
	public boolean evaluate( SIFDataObject obj, Comparator comparer ) throws ADKSchemaException
	{
		if ( !( obj.getElementDef() == fObjType ) ){
			return false;
		}
		if( fRoot != null ){
			SIFXPathContext context = SIFXPathContext.newSIFContext( obj, this.getEffectiveVersion() );
			return evaluateConditionGroup( context, fRoot, comparer );
		}
		return true;
	}


	/**
	 * Evaluates a condition group against a SifDataObject to determine if
	 * they are a match or not
	 * @param grp
	 * @param obj
	 * @return True if the result of evaluating the condition groups is true
	 * @throws ADKSchemaException If the condition contains references to invalid elements
	 */
	private boolean evaluateConditionGroup(
			SIFXPathContext context,
			ConditionGroup grp,
			Comparator comparer
			 ) throws ADKSchemaException
	{
		Condition[] conds = grp.getConditions();

		if (conds.length > 0) {
			boolean returnOnFirstMatch = grp.getOperator() == GroupOperators.OR ? true
					: false;

			for (Condition c : conds) {
				if ((evaluateCondition(context, c, comparer)) == returnOnFirstMatch) {
					// If this is an OR group, return true on the first match
					// If this is an AND Group, return false on the first
					// failure
					return returnOnFirstMatch;
				}
			}
			// None of the conditions matched the returnOnFirstMathValue.
			// Therefore,
			// return the opposite value
			return !returnOnFirstMatch;
		} else {
			return evaluateConditionGroups(context, grp.getOperator(), grp
					.getGroups(), comparer);
		}

	}

	/**
	 * Evaluates the condition groups and returns True if the Operator is OR and
	 * at least one of the groups evaluates to TRUE. If the Operator is AND, all
	 * of the condition groups have to evaluate to TRUE
	 *
	 * @param operation
	 * @param grps
	 * @param context
	 *            the SIFXPathContext to use for evaluating conditions
	 * @return true if the object meets all of the query conditions
	 * @throws ADKSchemaException
	 *             If the condition contains references to invalid elements
	 */
	private boolean evaluateConditionGroups(
			SIFXPathContext context,
			GroupOperators operation,
			ConditionGroup[] grps,
			 Comparator comparer ) throws ADKSchemaException
	{
		boolean isMatch = true;
		for ( int c = 0; c < grps.length; c++ )
		{
			boolean singleMatch = evaluateConditionGroup( context, grps[ c ], comparer );
			if ( operation == GroupOperators.OR )
			{
				if ( singleMatch )
				{
					// In OR mode, return as soon as we evaluate to True
					return true;
				}
				isMatch |= singleMatch;

			}
			else
			{
				isMatch &= singleMatch;
			}
			// As soon as the evaluation fails, return
			if ( !isMatch )
			{
				return false;
			}
		}
		return isMatch;
	}

	/**
	 * Evaluates a single SIF_Condition against an object and returns whether it matches or not
	 * @param cond
	 * @param obj
	 * @return True if the specified condition is true of this object
	 * @throws ADKSchemaException If the condition contains references to invalid elements
	 */
	private boolean evaluateCondition(
			SIFXPathContext context,
			Condition cond,
			Comparator comparer ) throws ADKSchemaException
	{
		// TODO: Add support for comparison using the SIF Data Types
		Element def = context.getElementOrAttribute( cond.getXPath() );
		String conditionValue = cond.getValue();


		String elementValue = null;
		if( def != null ){
			SIFSimpleType value = def.getSIFValue();
			if( value != null ){
				// Format the value to string, based on the query version
				elementValue = value.toString( this.getEffectiveVersion() );
			} else {
				// TODO: Not sure if this would ever return a value if the above does not
				elementValue = def.getTextValue();
			}
		}

		if ( elementValue == null || conditionValue == null )
		{
			// Don't use standard comparision because it will fail. If
			// one or the other value is null, it cannot be compared, except for
			// if the operator is EQ or NOT
			boolean bothAreNull = ( elementValue == null && conditionValue == null );
			switch( cond.getOperator() )
			{
				case EQ:
				case GE:
				case LE:
					return bothAreNull;
				case NE:
					return !bothAreNull;
				default:
					// For any other operator, the results are indeterminate with
					// null values. Return false in this case.
					return false;
			}
		}

		int compareLevel = comparer.compare( elementValue, conditionValue );

		switch ( cond.getOperator() )
		{
			case EQ:
				return compareLevel == 0;
			case NE:
				return compareLevel != 0;
			case GT:
				return compareLevel > 0;
			case LT:
				return compareLevel < 0;
			case GE:
				return compareLevel >= 0;
			case LE:
				return compareLevel <= 0;
		}
		return false;
	}

	/**
	 * Returns the collator that is appropriate for the current locale
	 * @return The default collator
	 */
	private Collator getCollator()
	{
		if( fCollator == null ){
			fCollator = Collator.getInstance();
		}
		return fCollator;
	}



	/**
	 * Sets the SIFContext that this query should apply to
	 * @param context The SIF Context that this query applies to
	 */
	public void setSIFContext(SIFContext context) {
		this.fContext = context;
	}

	/**
	 * Gets the SIF Context that this Query applies to
	 * @return The SIF Context that this query applies to
	 */
	public SIFContext getSIFContext() {
		return fContext;
	}


	/**
	 * If SIFElement restrictions are placed on this query, this method
	 * will take the SIFDataObject and call setChanged(false). It will then
	 * go through each of the SIFElement restrictions, resolve them, and
	 * call setChanged(true) on those elements only. This will cause the
	 * object to be rendered properly using SIFWriter.
	 * @param sdo
	 */
	public void setRenderingRestrictionsTo( SIFDataObject sdo ) {

		if( sdo == null || fFieldRestrictions == null ){
			return;
		}

		sdo.setChanged( false );

		// Go through and only set the filtered items to true
		SIFXPathContext context = SIFXPathContext.newSIFContext( sdo );
		for( ElementRef ref : fFieldRestrictions ){
			String xPath = ref.getXPath();
			Element e = context.getElementOrAttribute( xPath );
			if( e != null ){
				e.setChanged();
			}
		}
		sdo.ensureRootElementRendered();
	}

	private void writeObject( java.io.ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();

		ElementDefImpl.writeObject(fObjType,out);
	}

	private void readObject( java.io.ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		fObjType=ElementDefImpl.readObject(in);
	}
}
