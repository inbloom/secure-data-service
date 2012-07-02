//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.tinysis.data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import openadk.library.SIFSimpleType;
import openadk.library.SIFTypeConverter;
import openadk.library.tools.mapping.FieldAdaptor;
import openadk.library.tools.mapping.FieldMapping;


/**
 * Rudimentary implementation of an implementation of the ADK FieldAdaptor interface
 * that wraps a java.sql.ResultSet
 * 
 */

public class ResultSetAdapter implements FieldAdaptor {

	private ResultSet fSourceData;
	private ResultSetMetaData fMetadata;
	private HashMap<String,Integer> fColumns;
	
	public ResultSetAdapter( ResultSet sourceData ) throws SQLException
	{
		fSourceData = sourceData;
		fMetadata = sourceData.getMetaData();
		fColumns = new HashMap<String,Integer>();
		for( int i=0; i<fMetadata.getColumnCount(); i++ ){
			fColumns.put( fMetadata.getColumnName( i + 1 ), i + 1 );
		}
	}
	
	
	/**
	 * Moves the cursor in the resultSet to the next row
	 * @return 
	 * @throws SQLException 
	 */
	public boolean next() throws SQLException
	{
		return fSourceData.next();
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.tools.mapping.FieldAdaptor#getSIFValue(java.lang.String, com.edustructures.sifworks.SIFTypeConverter, com.edustructures.sifworks.tools.mapping.FieldMapping)
	 */
	public SIFSimpleType getSIFValue(String name, SIFTypeConverter typeConverter,
			FieldMapping fm) {
		
		return typeConverter.getSIFSimpleType( getValue( name ) );

	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.tools.mapping.FieldAdaptor#getValue(java.lang.String)
	 */
	public Object getValue(String name) {
		// If mappings are defined incorrectly, this method could throw lots of exceptions,
		// so we check the cached list of columns to determine if the column being asked for
		// exists before trying to read it
		Integer columnIndex = fColumns.get( name );
		if( columnIndex == null ){
			return null;
		}
		Object data;
		try {
			data = fSourceData.getObject( columnIndex );
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.tools.mapping.FieldAdaptor#hasField(java.lang.String)
	 */
	public boolean hasField(String fieldName) {
		return fColumns.containsKey( fieldName );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.tools.mapping.FieldAdaptor#setSIFValue(java.lang.String, com.edustructures.sifworks.SIFSimpleType, com.edustructures.sifworks.tools.mapping.FieldMapping)
	 */
	public void setSIFValue(String fieldName, SIFSimpleType sifDataElmeent, FieldMapping fm) {
		// TODO Update of ResultSet not yet supported
	}

}
