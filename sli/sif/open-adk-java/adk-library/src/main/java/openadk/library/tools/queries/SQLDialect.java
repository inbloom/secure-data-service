//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

/**
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class SQLDialect extends Dialect {
	
	
	/**
	 * The default SQL Dialect 
	 */
	public static Dialect DEFAULT = new SQLDialect( "'");
	
	/**
	 * A SQL Dialect with support for Microsoft Access 
	 */
	public static Dialect MS_ACCESS = new SQLDialect( "\"");
	
		protected SQLDialect(String quoteCharacters) {
			super( quoteCharacters );
		}
		
	
}
