//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.tinysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import openadk.examples.tinysis.data.ResultSetAdapter;
import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.DataObjectOutputStream;
import openadk.library.DefaultValueBuilder;
import openadk.library.MessageInfo;
import openadk.library.Publisher;
import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFMessageInfo;
import openadk.library.Zone;
import openadk.library.tools.mapping.ADKMappingException;
import openadk.library.tools.mapping.FieldAdaptor;
import openadk.library.tools.mapping.Mappings;
import openadk.library.tools.mapping.MappingsContext;
import openadk.library.tools.queries.QueryFormatterException;
import openadk.library.tools.queries.SQLDialect;
import openadk.library.tools.queries.SQLQueryFormatter;

/**
 * 
 * @author ADK Examples
 *
 */
public class DataObjectProvider implements Publisher {

	private String fTableName;
	private Mappings fMappings;
	
	public DataObjectProvider( String tableName, Mappings rootMappings ){
		fTableName = tableName;
		fMappings = rootMappings;
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Publisher#onRequest(com.edustructures.sifworks.DataObjectOutputStream, com.edustructures.sifworks.Query, com.edustructures.sifworks.Zone, com.edustructures.sifworks.MessageInfo)
	 */
	public void onRequest(DataObjectOutputStream out, Query query, Zone zone,
			MessageInfo info) throws ADKException {

		SIFMessageInfo smi = (SIFMessageInfo)info;
		System.out.println( "Received a request for " + query.getObjectTag() + " from agent \"" + smi.getSourceId() + "\" in zone " + zone.getZoneId() );
		
		MappingsContext mappingsContext = 
			fMappings.selectOutbound( 
								query.getObjectType(), smi.getLatestSIFRequestVersion(), 
								zone.getZoneId(), smi.getSourceId());
		
		// Configure the output stream to automatically filter out any
		// objects that don't match the query conditions
		// NOTE: This is in addition to the filtering done by the SQL query below
		out.setAutoFilter( query );
		
		//  Read all objects from the database to populate a HashMap of
		//  field/value pairs. The field names can be whatever we choose as long
		//  as they match the field names used in the <mappings> section of the
		//  agent.cfg configuration file. Each time a record is read, convert it
		//  to the specified SIF data object using the Mappings class and stream it to
		//  the supplied output stream.

		String sql = convertQueryToSql( smi, query, mappingsContext );
		Connection conn = getConnection(zone);
		try
		{
			int count = 0;
			ResultSet rs = getData( conn, sql, zone );
			ResultSetAdapter rsa = new ResultSetAdapter( rs );
			mappingsContext.setValueBuilder( new DefaultValueBuilder( rsa ) );
			
			while( rsa.next() )
			{
				count++;
				SIFDataObject sdo = ADK.DTD().createSIFDataObject( query.getObjectType() );
				sdo.setSIFVersion( smi.getLatestSIFRequestVersion() );
				onMappingObject( smi, query, mappingsContext, rsa, sdo);
				out.write( sdo );
			}

			rs.close();
			System.out.println( "- Returned " + count + " records from the database in response" );
		}
		catch( Exception ex )
		{
			System.out.println( "Error returning a SIF_Error response: " + ex.toString() );
			throw new SIFException(
				SIFErrorCategory.REQUEST_RESPONSE,
				SIFErrorCodes.REQRSP_GENERIC_ERROR_1,
				"An error occurred while querying the database for students",
				zone, ex );
		}
		finally
		{
			if( conn != null ) {
				try {
					conn.close();
				} catch( Exception ignored ) {
					System.out.println( ignored.toString() );
				}
			}
		}

		
		
	}

	/**
	 * Perform a mappings operation on the given object. This method allows
	 * subclasses to override behavior and change values before or after
	 * mapping occurs
	 * @param smi
	 * @param q
	 * @param mappingsContext
	 * @param oma
	 * @param sdo
	 * @throws ADKMappingException
	 */
	protected void onMappingObject(SIFMessageInfo smi, Query q, MappingsContext mappingsContext, FieldAdaptor oma, SIFDataObject sdo) throws ADKMappingException {
		mappingsContext.map( sdo, oma );
	}
	
	/**
	 * Adds fields to the SQLQueryFormatter so that it can render the SQL. This method
	 * allows subclasses to override behavior and to add additional SQLFields, representing
	 * calculated values. A good example of this is queries on StudentSchoolEnrollment/TimeFrame,
	 * which can be calculated based on EntryDate and ExitDate
	 * @param smi
	 * @param query
	 * @param context
	 * @param formatter
	 */
	protected void onAddQueryFormatterFields( SIFMessageInfo smi, Query query, MappingsContext context, SQLQueryFormatter formatter )
	{
		formatter.addFields( context, SQLDialect.DEFAULT );
	}

	private String convertQueryToSql( SIFMessageInfo smi, Query query, MappingsContext mappingsContext)
		throws QueryFormatterException
	{
		StringBuilder sql = new StringBuilder();
		sql.append( "Select * FROM " );
		sql.append( fTableName );
		
		// TODO: Commented out for now....
		
		if( query.hasConditions() ){
			SQLQueryFormatter sqlFormatter = new SQLQueryFormatter();
			onAddQueryFormatterFields( smi, query, mappingsContext, sqlFormatter );
			sql.append( " WHERE " );
			sql.append( sqlFormatter.format( query, false ) );
		}
		return sql.toString();
	}
	
	

	/**
	 * Gets a connection from the database. If a connection cannot be obtained,
	 * a SIFException is thrown with the retry flag set to True to signal to the 
	 * ZIS to retry the message later.
	 * @param zone
	 * @return
	 * @throws SIFException
	 */
	private Connection getConnection(Zone zone) throws SIFException {
		Connection conn = null;
		try
		{
			//	Get a Connection
			conn = DriverManager.getConnection( zone.getProperties().getProperty( "jdbc.url" ));
			conn.setAutoCommit( true );
		}
		catch( SQLException sqlEx ){
			SIFException sifEx = new SIFException( SIFErrorCategory.SYSTEM, SIFErrorCodes.SYS_GENERIC_ERROR_1, "Unable to connect to application database", zone, sqlEx );
			// Notify the ZIS to re-send the message later
			sifEx.setRetry( true );
			throw sifEx;
		}
		return conn;
	}
	
	/**
	 * Gets a ResultSet from the database. If an exception occurs, the appropriate
	 * SIFException is thrown
	 * @param conn An open connection to the database
	 * @param sql The SQL to run to get data
	 * @return A ResultSet of data
	 * @throws SIFException
	 */
	private ResultSet getData( Connection conn, String sql, Zone zone )
		throws SIFException
	{
		try
		{
			//  Query the database for all students
			Statement s = conn.createStatement();
			return s.executeQuery( sql );
		}
		catch( SQLException sqlEx ){
			SIFException sifEx = new SIFException( 
					SIFErrorCategory.SYSTEM, 
					SIFErrorCodes.SYS_GENERIC_ERROR_1, "Error executing query", zone, sqlEx );
			throw sifEx;
		}

	}
	
	/**
	 * Gets the current row of data from the ResultSet and stores all fields
	 * in a Map. The Map is used by the ADK to retrieve data for Mapping
	 * operations.
	 * @param resultSet The ResultSet object to get a row of data from
	 * @return The current row of data, contained in a Map
	 */
	private void getDataValues( ResultSet resultSet, Map<String,Object> values )
		throws SQLException
	{

		ResultSetMetaData rsmd = resultSet.getMetaData();
		for( int a = 1; a < ( rsmd.getColumnCount() + 1 ); a++ )
		{
			try{
				String fieldName = rsmd.getColumnName( a );
				Object value = resultSet.getObject( a );
				values.put( fieldName, value );
			} catch( SQLException sqlEx ){
				System.out.println( "Error on field # " + a + sqlEx );
			}
		}
	}

}
