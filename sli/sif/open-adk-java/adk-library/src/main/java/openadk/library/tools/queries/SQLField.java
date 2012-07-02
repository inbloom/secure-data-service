//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

/**
 *  Encapsulates a field name and type from the <code>java.sql.Types</code> class
 *
 *  When preparing a dictionary to be passed to the <code>SQLQueryFormatter.format</code>
 *  method, the caller must map <code>SIFDTD</code> ElementDef constants to instances 
 * 	of SQLField. The <code>java.sql.Types</code> constant is used to render the field 
 * 	value according to its data type (e.g. strings are quoted with a single quote, 
 * 	numeric fields are rendered as-is, etc.)<p>
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
 *  &nbsp;&nbsp;&nbsp;&nbsp;new SQLField( "Students.US_Citizen_Bool{04=1,=0}", java.sql.Types.NUMERIC ) );<br/>
 *  </code>
 *  <p>
 *
 *  The above example might result in a string such as "( Students.US_Citizen_Bool = 0 )"
 *  or "( Students.Foreign_ID = '898' ) OR ( Students.Last_Name = 'Cortez' AND First_Name = 'Robert' )"<p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class SQLField
{
	private final Dialect fDialect;
	public String Name;
	public int Type;

	/**
	 *  Constructor
	 *  @param name The application-defined field name
	 *  @param type A constant from the java.sql.Types class. The type is used
	 *      by SQLQueryBuilder to property format the field value
	 */
	public SQLField( String name, int type )
	{
		Name = name;
		Type = type;
		fDialect = SQLDialect.DEFAULT;
	}
	
	/**
	 *  Constructor
	 *  @param name The application-defined field name
	 *  @param type A constant from the java.sql.Types class. The type is used
	 *      by SQLQueryBuilder to property format the field value
	 *  @param dialect The SQL Dialect to use for rendering fields   
	 */
	public SQLField( String name, int type, Dialect dialect )
	{
		Name = name;
		Type = type;
		fDialect = dialect;
	}

	/**
	 *  Render a field value given the java.sql.Types constant passed to the constructor
	 */
	public String render( String value )
		throws QueryFormatterException
	{
		switch( Type )
		{
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
			case java.sql.Types.LONGVARCHAR:
				return renderString( value );

			case java.sql.Types.DATE:
				return renderDate( value );

			case java.sql.Types.TIME:
				return renderTime( value );

			case java.sql.Types.TIMESTAMP:
				return renderTimeStamp( value );

			default:
				return value;
		}
	}

	/**
	 *  Render a field value as a string
	 */
	public String renderString( String value )
	{
		return fDialect.renderString( value );
	}

	/**
	 *  Render a field value as a number
	 */
	public String renderNumeric( String value )
	{
		return fDialect.renderNumeric( value );
	}

	/**
	 *  Render a field value as a date
	 */
	public String renderDate( String value )
	{
		return fDialect.renderDate( value );
	}

	/**
	 *  Render a field value as a time
	 */
	public String renderTime( String value )
	{
		return fDialect.renderTime( value );
	}
	
	/**
	 *  Render a field value as a time
	 */
	public String renderTimeStamp( String value )
	{
		return fDialect.renderTimeStamp( value );
	}
}
