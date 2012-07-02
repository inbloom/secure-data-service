//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

import java.util.*;

/**
 * 	Describes a query for Metrics matching the specified conditions
 */
public class MetricQuery
{
	/**
	 * 	Joined queries.
	 */
	protected Vector fJoined;
	
	/**
	 * 	A list of OID constraints
	 */
	protected Vector fOID;
	
	/**
	 * 	If the query is restricted by ObjectType, this is the ObjectType condition 
	 */
	protected String fObjTypeExpr;
	
	/**
	 * 	If the query is restricted by SIF_MsgId, this is the SIF_MsgId condition
	 */
	protected String fMsgIdExpr;
	
	/**
	 * 	Constructor	
	 */
	public MetricQuery()
	{
	}
	
	public MetricQuery( String oid )
	{
		addCondition( oid );
	}
	
	public MetricQuery( int oid )
	{
		addCondition( String.valueOf( oid ) );
	}
	
	public void join( MetricQuery query )
	{
		join( query, null );
	}
	
	public void join( MetricQuery query, String op )
	{
		if( fJoined == null )
			fJoined = new Vector();
		
		fJoined.add( new JoinedQuery( query, op ) );
	}
	
	/**
	 * 	Set the OID condition from an OID string.
	 * 	@param oid The OID. If an element in the OID is to be excluded from the condition, 
	 * 		specify an asterisk or question mark (e.g. "100.5.*.61"). If an element in the
	 * 		OID can be within any range, use "min-max" (e.g. "100.?.1000-1999")
	 */
	public void addCondition( String oid )
	{
		addCondition( oid, "=" );
	}
	
	public void addCondition( String oid, String op )
	{
		int position = 0;
		
		StringTokenizer tok = new StringTokenizer( oid,"." );
		while( tok.hasMoreTokens() ) 
		{
			position++;
			String val = (String)tok.nextElement();
			if( val.equals("*") || val.equals("?") )
				continue;
			int x = val.indexOf("-");
			if( x != -1 ) {
				int ls = Integer.parseInt( val.substring(0,x) );
				int rs = Integer.parseInt( val.substring(x+1) );
				addOIDCondition( "oid" + position + " >= " + ls + " AND oid" + position + " <= " + rs );
			}
			else
				addOIDCondition( "oid" + position + op + val );
		}
	}
	
	/**
	 * 	Add an OID condition
	 * 	@param expr An expression such as "oid4=781"
	 */
	private void addOIDCondition( String expr )
	{
		if( fOID == null )
			fOID = new Vector();
		fOID.addElement( expr );
	}

	/**
	 * 	Add an OID condition
	 * 	@param element The zero-based OID element index
	 * 	@param op The comparison operator (e.g. "=")
	 * 	@param value The OID element value
	 */
	private void addOIDCondition( int element, String op, byte value )
	{
		if( fOID == null )
			fOID = new Vector();
		fOID.addElement( "oid" + element + op + value );
	}
	
	/**
	 * 	Specify an object type condition
	 * 	@param 
	 */
	public void setObjectTypeCondition( short objType, String op )
	{
		fObjTypeExpr = "ObjectType" + op + objType;
	}
		
	
	/**
	 * 	Specify a SIF_MsgId condition
	 */
	public void setMsgIdCondition( String msgId, String op )
	{
		fMsgIdExpr = "MsgId" + op + ( msgId == null ? "null" : ( "'" + msgId + "'" ) );
	}
	
	/**
	 * 	Return an SQL <code>WHERE</code> clause from the query conditions
	 */
	public String toSQL()
	{
		StringBuffer s3 = null;
		if( fOID != null ) {
			s3 = new StringBuffer();
			for( Iterator it = fOID.iterator(); it.hasNext(); ) {
				String c = (String)it.next();
				if( s3.length() != 0 )
					s3.append( " AND " );
				s3.append( c );
			}
		}
		
		StringBuffer where = new StringBuffer();
		if( fMsgIdExpr != null )
			where.append( fMsgIdExpr );
		if( where.length() != 0 ) 
			where.append( " AND " );
		if( fObjTypeExpr != null )
			where.append( fObjTypeExpr );
		if( where.length() != 0 ) 
			where.append( " AND " );
		if( s3 != null )
			where.append( s3 );
		
		//	Add in the joined queries, if any
		if( fJoined != null ) 
		{
			for( Iterator it = fJoined.iterator(); it.hasNext(); ) 
			{
				JoinedQuery jq = (JoinedQuery)it.next();
				if( where.length() > 0 ) {
					where.append( " " );
					where.append( jq.Op );
					where.append( " " );
				}
				
				where.append( " ( " );
				where.append( jq.Q.toSQL() );
				where.append( " ) " );
			}
		}

		return where.length() == 0 ? null : where.toString();
	}
	
	public String toString() {
		return toSQL();
	}
	
	class JoinedQuery {
		public MetricQuery Q;
		public String Op;
		public JoinedQuery( MetricQuery query, String op ) {
			Q = query;
			Op = op;
		}
	}
}
