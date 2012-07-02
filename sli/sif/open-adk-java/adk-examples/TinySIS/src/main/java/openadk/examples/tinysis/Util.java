//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.tinysis;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Util {
	public static void deleteFileOrDirectory(String path) {
		deleteFileOrDirectory(new File(path));
	}
	
	public static void deleteFileOrDirectory(File file) {
		if ( !file.exists() )
			return;
		
		if ( !file.isDirectory() ) {
			file.delete();
			return;
		}
		
		File directory = file;
		
		File[] children = directory.listFiles();
		
		for ( File child : children ) {
			if ( child.isDirectory() )
				deleteFileOrDirectory( child );
			else
				child.delete();
		}
		
		directory.delete();
	}
	
	/**
	 * Create the tables and imports the data necessary for this example to run.  
	 * The data is loaded from the csv files in the data directory.  This method will not succeed if the database already exists.
	 * If the TinySISDB folder exists, then the database exists.  However, you can delete that folder to have it load the data again.
	 * @param connection
	 */
	public static void createDatabase(Connection connection)
	{	
		executeStatement(connection, "CREATE TABLE SIF_StudentSchoolEnrollment (EnrollmentID INT, TermID INT, SchoolID INT, StudentID INT, Status CHAR, EntryDate TIMESTAMP, EntryType CHAR, GradeLevel INT, ExitDate TIMESTAMP, ExitType CHAR, PRIMARY KEY (EnrollmentID))");
		executeStatement(connection, "CREATE TABLE refid (TableName VARCHAR(32), TableKey VARCHAR(32), RefId VARCHAR(32), PRIMARY KEY (RefId))");
		executeStatement(connection, "CREATE TABLE SIF_SchoolInfo (SchoolID INT, DistrictName VARCHAR(64), LocalID VARCHAR(64), StatePrId VARCHAR(64), SchoolName VARCHAR(64), SchoolURL VARCHAR(64), PhoneNumber VARCHAR(64), StreetLine1 VARCHAR(64), City VARCHAR(64), State VARCHAR(64), PostalCode VARCHAR(64), PRIMARY KEY(SchoolID))");
		executeStatement(connection, "CREATE TABLE SIF_StudentPersonal (StudentID INT, StudentNum VARCHAR(64), LastName VARCHAR(64), FirstName VARCHAR(64), MiddleName VARCHAR(64), Email VARCHAR(64), PhoneNumber VARCHAR(64), GradYear INT, Ethnicity CHAR, Gender CHAR, BirthDate TIMESTAMP, PlaceOfBirth VARCHAR(64), CountryOfCitizenship VARCHAR(64), CountryOfResidency VARCHAR(64), Language VARCHAR(2), StreetLine1 VARCHAR(64), StreetLine2 VARCHAR(64), City VARCHAR(64), State VARCHAR(64), PostalCode VARCHAR(10), PRIMARY KEY(StudentID))");
		executeStatement(connection, "CREATE TABLE term (TermID INT, SchoolID INT, Type INT, StartDate TIMESTAMP, EndDate TIMESTAMP, Description VARCHAR(256), SIFSchoolYear INT, PRIMARY KEY(TermID))");
		
		executeStatement(connection, "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'SIF_STUDENTSCHOOLENROLLMENT','data/enrollment.csv',null,null,null,0)");
		executeStatement(connection, "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'REFID','data/refid.csv',null,null,null,0)");
		executeStatement(connection, "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'SIF_SCHOOLINFO','data/school.csv',null,null,null,0)");
		executeStatement(connection, "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'SIF_STUDENTPERSONAL','data/student.csv',null,null,null,0)");
		executeStatement(connection, "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'TERM','data/term.csv',null,null,null,0)");
	}
	
	private static void executeStatement( Connection connection, String query ) {
		try {
			Statement statement = connection.createStatement();
	   		statement.executeUpdate(query);
			statement.close();
		} catch(SQLException sqle) {
			System.err.println("Failed to execute: " + query);
			System.err.println( sqle.getMessage());
		}		
	}
}
