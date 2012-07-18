//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import openadk.library.*;
import openadk.library.tools.mapping.*;


/**
 *  An implementation of a QueryFormatter that formats SIF_Query conditions as a
 * 	string suitable for inclusion in an SQL <code>WHERE</code> clause.<p>
 *
 *  When preparing a dictionary to be passed to the <code>SQLQueryFormatter.format</code>
 *  method, the caller must map <code>SIFDTD</code> ElementDef constants to instances 
 * 	of the <code>SQLField</code> class. The constructor to that class requires two 
 * 	parameters: the application-defined name of the field, and a type code constant 
 * 	from the <code>java.sql.Types</code> class. The type code is used to properly
 * 	render the field value according to its data type (e.g. strings are quoted with 
 * 	a single quote, numeric fields are rendered as-is, etc.)
 *  <p>
 *
 *  For example,<p>
 *
 *  <code>
 *  Map m = new HashMap();<br/>
 *  m.put( SIFDTD.STUDENTPERSONAL_REFID,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;new SQLField( "Students.Foreign_ID", java.sql.Types.VARCHAR ) );<br/>
 *  m.put( SIFDTD.NAME_LASTNAME,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;new SQLField( "Students.Last_Name", java.sql.Types.VARCHAR ) );<br/>
 *  m.put( SIFDTD.NAME_FIRSTNAME,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;new SQLField( "First_Name", java.sql.Types.VARCHAR ) );<br/>
 *  m.put( SIFDTD.DEMOGRAPHICS_CITIZENSHIPSTATUS,<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;new SQLField( "Students.US_Citizen_Bool{04=1;=0}", java.sql.Types.NUMERIC ) );<br/>
 *  </code>
 *
 *  The above example might result in a string such as "( Students.US_Citizen_Bool = 0 )"
 *  or "( Students.Foreign_ID = '898' ) OR ( Students.Last_Name = 'Cortez' AND First_Name = 'Robert' )"<p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class SQLQueryFormatter extends QueryFormatter
{
	
	private Map<Object, Object> fFields;
	
	/**
	 *  Constructs a SQLQueryFormatter
	 */
    public SQLQueryFormatter()
	{
		super();
    }
    
    /**
     * Adds an SQLField to use for rendering an SQL Where clause using
     * the {@link #format(Query)} method.
     * @param def The ElementDef that is represented by the field
     * @param field The SQL representation of the field
     */
    public void addField( ElementDef def, SQLField field )
    {
    	if( fFields == null ){
    		fFields = new HashMap<Object, Object>();
    	}
    	fFields.put( def, field );
    }
    
    /**
     * Adds an SQLField to use for rendering an SQL Where clause using
     * the {@link #format(Query)} method.
     * @param def The XPath representing the SIF Element being mapped
     * @param field The SQL representation of the field
     */
    public void addField( String xPath, SQLField field )
    {
    	if( fFields == null ){
    		fFields = new HashMap<Object, Object>();
    	}
    	fFields.put( xPath, field );
    }
    
    
    /**
     * Adds a QueryField to use for rendering an SQL Where clause using
     * the {@link #format(Query)} method.
     * @param def The ElementDef that is represented by the field
     * @param field The SQL representation of the field
     */
    public void addField( ElementDef def, QueryField field )
    {
    	if( fFields == null ){
    		fFields = new HashMap<Object, Object>();
    	}
    	fFields.put( def, field );
    }

    /**
     * Adds a QueryField to use for rendering an SQL Where clause using
     * the {@link #format(Query)} method.
     * @param def The xPath representing the SIF Element being mapped
     * @param field The SQL representation of the field
     */
    public void addField( String xPath, QueryField field )
    {
    	if( fFields == null ){
    		fFields = new HashMap<Object, Object>();
    	}
    	fFields.put( xPath, field );
    }
    
    
    /**
     * Adds SQLFields to represent each field rule in the specified MappingsContext.
     * 
     * @param context The MappingsContext containing FieldMappings
     */
    public void addFields( MappingsContext context ){
    	addFields( context, SQLDialect.DEFAULT );
    }
    /**
     * Adds SQLFields to represent each field rule in the specified MappingsContext.
     * 
     * @param context The MappingsContext containing FieldMappings
     */
    public void addFields( MappingsContext context, Dialect dialect ){
    	for( Mapping amapping : context.getFieldMappings() ){
    		if (amapping instanceof FieldMapping) {
    			FieldMapping mapping = (FieldMapping)amapping;
    			
	    		Rule rule = mapping.getRule();
	    		if( rule instanceof XPathRule ) { // JEN
	    			SIFTypeConverter converter = null;
	    			XPathRule xRule = (XPathRule)rule;
	    		   	ElementDef targetDef = xRule.lookupTargetDef( context.getObjectDef() );
	    			if( targetDef != null ){
	    				converter = targetDef.getTypeConverter();
	    			}
	    			if( converter == null ){
	    				converter = SIFTypeConverters.STRING;
	    			}
	    			if( mapping.getValueSetID() != null ){
	    				ValueSet vs = context.getMappings().getValueSet( mapping.getValueSetID(), true );
	    				if( vs != null ){
	//    					 Create the lookup table for generating the SQL lookup
							StringBuilder buffer = new StringBuilder();
							// e.g. CircRecord.PatronType{StudentPersonal=1;StaffPersonal=2}
							//buffer.Append( tablePrefix );
							buffer.append( mapping.getFieldName() );
							buffer.append( '{' );
							ValueSetEntry [] entries = vs.getEntries();
							for ( int a = 0; a < entries.length; a++ )
							{
								if ( a > 0 )
								{
									buffer.append( ';' );
								}
								buffer.append( entries[ a ].value );
								buffer.append( '=' );
								buffer.append( entries[ a ].name );
							}
							buffer.append( '}' );
							SQLField field = new SQLField( buffer.toString(), converter.getSQLType(), dialect );
			    			addField( xRule.getXPath(), field );
	    					continue;
	    				}
	    			}
	    			
	    			SQLField field = new SQLField( mapping.getFieldName(), converter.getSQLType(), dialect );
	    			addField( xRule.getXPath(), field );
	    		}
    		}
    	}
    }
    
    
    /**
     * Returns all of the SQLFields that have been mapped for this instance
     * @return
     */
    public Set<Entry<Object,Object>> getFields()
    {
    	return fFields.entrySet();
    }
    
    
    /**
	 *  Builds a query string given a dictionary of field definitions and a Query
	 *  instance. This method evaluates the conditions of that Query to produce
	 *  a textual query string in the format determined by the implementation.<p>
	 *
	 *  The dictionary should contain application-defined field values that map
	 *  to <code>ElementDef</code> key elements. Whenever a SIF element or attribute
	 *  is found in the Query, the corresponding application-defined field is
	 *  used in its place.<p>
	 *
	 *  A special convention allows agents to define an in-line translation table for
	 * 	replacing SIF element/attribute values with values defined in the table. If 
	 * 	a field is expressed in the form "field-name{value1=cons1;value2=cons2;..}",
	 *  the comma-delimited list of values within the curly braces is applied to the
	 *  value of the SIF_Value element in the SIF_Query, such that "value1" is
	 *	represented as "cons1". For example, the acceptable values for 
	 *	LibraryPatronStatus/@SifRefIdType attribute are "StudentPersonal" and 
	 *	"StaffPersonal". If in your application you represent these values as numeric 
	 *	types - say, 1 and 2, respectively - you could create the following in-line
	 *	translation table to instruct the QueryFormatter to substitute 
	 *	"StudentPersonal" with "1" and "StaffPersonal" with "2":
	 *
	 *      "CircRecord.PatronType{StudentPersonal=1;StaffPersonal=2}"
	 *
	 *  @param query An ADK Query object, usually obtained during the processing
	 *      of a SIF_Request by a <i>Publisher</i> message handler
	 */
    public String format( Query query )
	throws QueryFormatterException
	{
    	return format( query, true );
	}
    
    public String format( Query query, boolean explicit )
    	throws QueryFormatterException
    {
    	if( fFields == null && query.hasConditions() ){
    		throw new QueryFormatterException( "Agent is not configured to respond to query conditions" );
    	}
    	return format( query, fFields, explicit );
    }

	/**
	 *  Return the text that should be inserted for an opening brace
	 */
	public String getOpenBrace()
	{
		return "( ";
	}

	/**
	 *  Return the text that should be inserted for a closing brace
	 */
	public String getCloseBrace()
	{
		return " )";
	}

	
	/**
	 *  Return the text that should be inserted for the particular comparison operator, such as "Equals"
	 */
	public String getOperator( ComparisonOperators op )
	{
		switch( op )
		{
			case EQ:
				return " = ";
			case NE:
				return " != ";
			case GT:
				return " > ";
			case LT:
				return " < ";
			case GE: 
				return " >= ";
			case LE:
				return " <= ";
		}

		return "";
	}

	/**
	 *  Return the text that should be inserted for the particular grouping operator, such as "AND"
	 */
	public String getOperator( GroupOperators op )
	{
		switch( op )
		{
			case NONE:
			case AND:
				return " AND ";
			case OR:
				return " OR ";
		}

		return "";
	}
	
	

	/**
	 *  Return the text for a field name
	 *  @param field The field
	 *  @param def The corresponding field definition from the Map passed to
	 *      the <code>format</code> method
	 *  @return The implementation returns the field name in whatever form is
	 *      appropriate to the implementation, using the supplied <i>def</i>
	 *      Object if necessary to obtain additional field information.
	 */
	public String renderField( ElementDef field, Object def )
		throws QueryFormatterException
	{
		try
		{
			return extractFieldName( ((SQLField)def).Name );
		}
		catch( ClassCastException cce )
		{
			throw new QueryFormatterException( "SQLQueryFormatter requires that the Map passed to the format method consist of SQLField instances (not " + cce.getMessage() + " instances)" );
		}
	}

	/**
	 *  Return the text for a field value
	 *  @param field The field value
	 *  @param def The corresponding field definition from the Map passed to
	 *      the <code>format</code> method
	 *  @return The implementation returns the field value in whatever form is
	 *      appropriate to the implementation, using the supplied <i>def</i>
	 *      Object if necessary to obtain additional field information
	 */
	public String renderValue( String value, Object def )
		throws QueryFormatterException
	{
		try
		{
			//  The elements in the Map are expected to be SQLField instances
	    	SQLField f = (SQLField)def;
			return f.render( doValueSubstitution(value,f.Name) );
		}
		catch( ClassCastException cce )
		{
			throw new QueryFormatterException( "SQLQueryFormatter requires that the Map passed to the format method consist of SQLField instances (not " + cce.getMessage() + " instances)" );
		}
	}
}
