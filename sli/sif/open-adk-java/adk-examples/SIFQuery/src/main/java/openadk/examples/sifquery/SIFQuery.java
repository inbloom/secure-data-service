//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.sifquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import openadk.library.*;
import openadk.library.infra.SIF_Error;
import openadk.library.tools.xpath.SIFXPathContext;

public class SIFQuery extends Agent implements QueryResults {
	private static Map<String, ComparisonOperators> supportedComparisons = new HashMap<String, ComparisonOperators>();
	
	public SIFQuery() {
		super( "SIFQuery" );
	}

	private synchronized static void initializeComparisonList()
    {
		if (supportedComparisons.size() == 0) {
			supportedComparisons.put("=", ComparisonOperators.EQ);
            supportedComparisons.put(">", ComparisonOperators.GT);
            supportedComparisons.put("<", ComparisonOperators.LT);
            supportedComparisons.put(">=", ComparisonOperators.GE);
            supportedComparisons.put("<=", ComparisonOperators.LE);
            supportedComparisons.put("!=", ComparisonOperators.NE);
        }
        
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final SIFQuery _agent = new SIFQuery();;
		try
		{
			if( args.length < 2 ) {
				System.out.println("Usage: SIFQuery /zone zone /url url [/events] [options]");
				System.out.println("    /zone zone     The name of the zone");
				System.out.println("    /url url       The zone URL");
				ADKExamples.printHelp();
				return;
			}
			
			//	Pre-parse the command-line before initializing the ADK
			ADK.debug = ADK.DBG_MODERATE;
			ADKExamples.parseCL( null, args );

	    	//  Initialize the ADK with the specified version, loading only the Student SDO package
			ADK.initialize();

			// Call StartAgent. 
			_agent.startAgent(args);
			
			// Turn down debugging
			ADK.debug = ADK.DBG_NONE;
			
			// Call runConsole() This method does not return until the agent shuts down
			_agent.runConsole();
			
			//	Wait for Ctrl-C to be pressed
			Object semaphore = new Object();
			synchronized( semaphore ) {
				semaphore.wait();
			}
			System.out.println("Agent is running (Press Ctrl-C to stop)");
			
		}
		catch( Throwable e )
		{
			System.out.println(e);
		}
		finally
		{
			if( _agent != null && _agent.isInitialized() ){
				//  Always shutdown the agent on exit
				try {
					_agent.shutdown( ADKExamples.Unreg ?  ADKFlags.PROV_UNREGISTER : ADKFlags.PROV_NONE );
				}
				catch( ADKException adkEx ){
					System.out.println( adkEx );
					adkEx.printStackTrace();
				}
			}
		}

	}

	private void startAgent(String[] args)
		throws Exception
	{
		this.initialize();
		Map params = ADKExamples.parseCL( this, args );
		
		String zoneId = (String)params.get("zone");
		String url = (String)params.get("url");
		
		if( zoneId == null || url == null ) {
			System.out.println("The /zone and /url parameters are required");
			System.exit(0);
		}
		
		
		// 1) Get an instance of the zone to connect to
		Zone zone = getZoneFactory().getInstance( zoneId, url );
		zone.getProperties().setCompressionThreshold(0);
		
		zone.setQueryResults( this );
	
		// 3) Connect to zones
		zone.connect( ADKExamples.Reg ? ADKFlags.PROV_REGISTER : ADKFlags.PROV_NONE );
		zone.getZoneStatus();
		
	}
	
	private void runConsole()
		throws IOException, ADKException
	{
		System.out.println( "SIFQuery Command Line" );
		System.out.println( "Version @PRODUCT_VERSION@" );
		
		printSQLHelp();
		BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
		Pattern sqlPattern = Pattern.compile( "(?:select)(.*)(?:from)(.*)(?:where)(.*)$", Pattern.CASE_INSENSITIVE );
		boolean finished = false;
		while( !finished ){
			printPrompt();
			String query = input.readLine().trim();
			if( query.length() == 0 ){
				continue;
			}
			String lcaseQuery = query.toLowerCase();
			if( lcaseQuery.charAt( 0 ) == 'q' ){
				finished = true;
				continue;
			}
			
			if( lcaseQuery.indexOf( "where" ) == -1 ){
				// The regular expression requires a where clause
				query = query + " where ";
			}
			
			Matcher results = null;
			try
			{
				results = sqlPattern.matcher( query );
			}
			catch( Exception ex ){
				System.out.println( "ERROR evaluating expression: " + ex );
				continue;
			}
			if( !results.matches() ){
				System.out.println( "Unknown error evaluating expression."  );
				continue;
			}
			if( results.groupCount() == 3 ){
				Query q = createQuery( results.group( 2 ) );
				if( q != null &&
					addConditions( q, results.group( 3 ) ) &&
					addSelectFields( q, results.group( 1 ) ) )
				{
						System.out.println( "Sending Query to zone.... " );
						String queryXML = q.toXML();
						System.out.println( queryXML );
						// Store the original source query in the userData property
						q.setUserData( queryXML );
						this.getZoneFactory().getAllZones()[0].query( q );
				}
			} else {
				System.out.println( "ERROR: Unrecognized query syntax..." );
				printSQLHelp();
			}
		}
		
	}
	
	private void printPrompt(){
		System.out.print( "SIF:  " );
	}
	
	private void printSQLHelp(){
		System.out.println( "Syntax: Select {fields} From {SIF Object} [Where {field}={value}] " );
		System.out.println( "  {fields} one or more field names, seperated by a comma" );
		System.out.println( "           (may by empty or * )" );
		System.out.println( "  {SIF Object} the name of a SIF Object that is provided in the zone" );
		System.out.println( "  {field} a field name" );
		System.out.println( "  {value} a value" );
		System.out.println( "Examples:" );
		System.out.println( "SIF: Select * from StudentPersonal" );
		System.out.println( "SIF: Select * from StudentPersonal where RefId=43203167CFF14D08BB9C8E3FD0F9EC3C" );
		System.out.println( "SIF: Select * from StudentPersonal where Name/FirstName=Amber" );
		System.out.println( "SIF: Select Name/FirstName, Name/LastName from StudentPersonal where Demographics/Gender=F" );
		System.out.println( "SIF: Select * from StudentSchoolEnrollment where RefId=43203167CFF14D08BB9C8E3FD0F9EC3C" );
		System.out.println();
	}
	
	private Query createQuery( String fromClause ){
		ElementDef queryDef = ADK.DTD().lookupElementDef( fromClause.trim() );
		if( queryDef == null ){
			System.out.println( "ERROR: Unrecognized FROM statement: " + fromClause );
			printSQLHelp();
			return null;
		} else{
			return new Query( queryDef );
		}
	}
	
	private boolean addSelectFields(Query q, String selectClause )
	{
		if( selectClause.length() == 0 || selectClause.indexOf( "*" ) > -1 ){
			return true;
		}
		String[] fields = selectClause.split( "," );
		for( String field : fields ){
			field = field.trim();
			if( field.length() > 0 ){
				ElementDef restriction =ADK.DTD().lookupElementDefBySQP( q.getObjectType(), field );
				if( restriction == null ){
					System.out.println( "ERROR: Unrecognized SELECT field: " + field );
					printSQLHelp();
					return false;
				} else {
					q.addFieldRestriction( restriction );
				}
			}
		}
		return true;
	}

	private boolean addConditions(Query q, String whereClause )
	{
        initializeComparisonList();
		boolean added = true;
		whereClause = whereClause.trim();
		if( whereClause.length() == 0 ){
			return added;
		}
		
		String[] whereConditions = whereClause.split(" [aA][nN][dD] ");
		ComparisonOperators cmpOperator = ComparisonOperators.EQ;
		String[] fields = null;

		if (whereConditions.length > 0) {
			for (String condition : whereConditions) {
				fields = null;
				for (Map.Entry<String, ComparisonOperators> kvp : supportedComparisons.entrySet()) {
					String cmpString = kvp.getKey();
					cmpOperator = kvp.getValue();
					if (cmpOperator == ComparisonOperators.EQ) {
						int index = condition.lastIndexOf(cmpString);
						fields = new String[2];
						if (index > 0) {
							fields[0] = condition.substring(0, index);
							fields[1] = condition.substring((index + 1));
						} else {
							fields[0] = condition;
						}
					}
					if (fields == null) {
						fields = condition.split(cmpString);
					}   
					
	                if (fields[0] == condition) { 
	                    //Means no match found using that current comparison operator
	                    //so skip this condition
	                    fields = null;
	                    continue;
	                }
	
	                if (fields.length != 2) {
	                    System.out.println("ERROR: Unsupported where clause: " + whereClause);
	                    printSQLHelp();
	                    added = false;
	                    break;
	                }
	
	                String fieldExpr = fields[0].trim();
	                ElementDef sdo = ADK.DTD().lookupElementDefBySQP(q.getObjectType(), fields[0].trim());
	                
	                if (sdo == null) {
	                    System.out.println("ERROR: Unrecognized field in where clause: " + fields[0].trim());
	                    printSQLHelp();
	                    added = false;
	                    break;
	                } else {
	                	if (fieldExpr.indexOf('[') > 0)
                        {
                            // If there is a square bracket in the field syntax, use the raw XPath,
                            // rather then the ElementDef because using ElementDef restrictions
                            // does not work for XPath expressions that contain predicates
                            // Note that using raw XPath expressions works fine, but the ADK is no longer
                            // going to be able to use version-independent rendering of the query
                            q.addCondition(fieldExpr, cmpOperator, fields[1].trim());
                        }
                        else
                        {
                            q.addCondition( sdo, cmpOperator, fields[1].trim() );
                            
                        }
	                	//our condition has been found, no need to check the other comparison
	                    //operators for a match so move to the next condition
	                    break; 
	                }   
				}
			}//end for each
		}
		
		return added;
	}
	
	public void onQueryPending(MessageInfo info, Zone zone) throws ADKException {
		SIFMessageInfo smi = (SIFMessageInfo)info;
		System.out.println( "Sending SIF Request with MsgId " + smi.getMsgId() + " to zone " + zone.getZoneId() );
	}

	public void onQueryResults(DataObjectInputStream data, SIF_Error error, Zone zone, MessageInfo info) throws ADKException {
		
		SIFMessageInfo smi = (SIFMessageInfo)info;
		System.out.println();
		System.out.println( "********************************************* " );
		System.out.println( "Received SIF_Response packet from zone" + zone.getZoneId() );
		System.out.println( "Details... " );
		System.out.println( "Request MsgId: " + smi.getSIFRequestMsgId() );
		System.out.println( "Packet Number: " + smi.getPacketNumber() );
		System.out.println();
		
		if( error != null ){
		
			System.out.println( "The publisher returned an error: " ); 
			System.out.println( "Category: " + error.getSIF_Category() + " Code: " + error.getSIF_Code()  );
			System.out.println( "Description " + error.getSIF_Desc() );
			if( error.getSIF_ExtendedDesc() != null )
			{
				System.out.println( "Details: " + error.getSIF_ExtendedDesc() );
			}
			return;
		}
		
		try
		{
			int objectCount = 0;
			while( data.available() ){
				SIFDataObject next = data.readDataObject();
				objectCount++;
				System.out.println();
				System.out.println( "Text Values for " + next.getElementDef().name() + " " + objectCount + " {" + next.getKey() + "}" );
				
				SIFXPathContext context = SIFXPathContext.newSIFContext( next );
				//	Print out all attributes 
				Iterator textNodes = context.iterate("//@*");
				while( textNodes.hasNext() ) {
					Element value = (Element)textNodes.next();
					ElementDef valueDef = value.getElementDef();
					System.out.print( valueDef.getParent().tag( SIFVersion.LATEST ) + "/@" + valueDef.tag( SIFVersion.LATEST ) + "=" + value.getTextValue() + ", " );
				}
				System.out.println();
				// Print out all  elements that have a text value
				textNodes = context.iterate("//*");
				while( textNodes.hasNext() ) {
					Element value = (Element)textNodes.next();
					String textValue = value.getTextValue();
					if( textValue != null ){
						ElementDef valueDef = value.getElementDef();
						System.out.print( valueDef.tag( SIFVersion.LATEST ) + "=" + textValue + ", " );
					}
				}

			}
			System.out.println();
			System.out.println( "Total Objects in Packet: " + objectCount );
			
			
			
		} catch( Exception ex ){
			System.out.println( ex.getMessage() );
			ex.printStackTrace();
		}
		
		if( !smi.getMorePackets() ){
			// This is the final packet. Print stats
			System.out.println( "Final Packet has been received." );
			RequestInfo ri = smi.getSIFRequestInfo();
			if( ri != null ){
				System.out.println( "Source Query: " );	
				System.out.println( ri.getUserData() );
				long difference = smi.getTimestamp().getTime().getTime() - ri.getRequestTime().getTime();
				System.out.println( "Query execution time: " + difference + " ms" );
			}
			
		} else {
			System.out.println( "This is not the final packet for this SIF_Response" );	
		}
		
		System.out.println( "********************************************* " );
		System.out.println( );
		printPrompt();
	}

}
