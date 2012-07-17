//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ZISDefinitionGenerator extends Generator {

	protected ZISDefinitionGenerator( String srcDir, String destDir )
	{
		super( srcDir, destDir );
	}
	
	@Override
	public void generate(DB[] dbs) throws IOException, GeneratorException,
			MergeException {
	
		DB db = dbs[ dbs.length - 1 ];
	
		// Get a list of topics
		List<ObjectDef> oDefs = new ArrayList<ObjectDef>();
		for( ObjectDef o : db.getObjects() )
		{
			if( o.isTopic() ){
				oDefs.add( o );
			}
		}
		// Sort the ObjectDefs by package and then by object name
		Collections.sort( oDefs,  new Comparator<ObjectDef>()
				{
					public int compare( ObjectDef o1, ObjectDef o2 ) {
						int compare = o1.getLocalPackage().compareTo( o2.getLocalPackage() );
						if( compare == 0 ){
							return o1.getDTDSymbol().compareTo( o2.getDTDSymbol() );
						} else {
							return compare;
						}
					}
				}	);
			
		
		//
		// Generate the SIF.xml file
		//
		
		String fn = fDir + File.separator + "sif.xml";
		System.out.println("- Generating: "+fn);

		PrintWriter out = new PrintWriter( new FileWriter(fn),true );
		out.println( "<acl>" );
		DefinitionFile currentPackage = db.getDefinitionFile( oDefs.get( 0 ).getLocalPackage() );
		out.println( "   <!-- Objects in the " + currentPackage.getFriendlyName() + " group -->" );
		out.println( "   <group name=\"" + currentPackage.getFriendlyName() + "\">" );
		for( int a = 0; a < oDefs.size(); a++ ){
			ObjectDef currentObj = oDefs.get( a );
			if( !currentObj.getLocalPackage().equals( currentPackage.getLocalPackage() ) )
			{
				currentPackage = db.getDefinitionFile( currentObj.getLocalPackage() );
				out.println( "   </group>" );
				out.println( "   <!-- Objects in the " + currentPackage.getFriendlyName() + " group -->" );
				out.println( "   <group name=\"" + currentPackage.getFriendlyName() + "\">" );
			}
			writeRulesForObject( dbs, currentObj, out );
		}
	
		out.println( "   </group>" );
		out.println( "</acl>" );
		
		out.close();
		
		
		//
		// Generate the acl_0_unrestricted.xml file
		//
		
//		 Sort the ObjectDefs just by object name
		Collections.sort( oDefs,  new Comparator<ObjectDef>()
				{
					public int compare( ObjectDef o1, ObjectDef o2 ) {
								return o1.getDTDSymbol().compareTo( o2.getDTDSymbol() );
					}
				}	);
		
		fn = fDir + File.separator + "acl_0_unrestricted.xml";
		System.out.println("- Generating: "+fn);

		out = new PrintWriter( new FileWriter(fn),true );
		out.println( "<acl name=\"Unrestricted\">" );
		for( int a = 0; a < oDefs.size(); a++ ){
			ObjectDef currentObj = oDefs.get( a );
			writeRulesForObject( dbs, currentObj, out );
		}
		out.println( "</acl>" );
		
		out.close();
		
		
	}
	
	private void writeRulesForObject( DB[] dbs, ObjectDef def, PrintWriter out ){
		
		System.out.println("- Generating: "+def.getName());
		// For each name this object is known by, get a list of
		// versions it appears in
		String currentObjectName = def.getTag();
		List<SIFVersion> versions = new ArrayList<SIFVersion>();
		
		for( int a = 0; a < dbs.length; a++ ){
			ObjectDef candidate = dbs[a].getObject( def.getName() );
			if( candidate == null ){
				continue;
			}
			String candidateName = candidate.getTag();
			if( !currentObjectName.equals( candidateName ) ){
				// Render a rule for the currentObjectName
				if( versions.size() > 0 ){
					out.println( "      <!-- " + def.getName()  + " is known by a different name in previous versions of SIF -->" );
					writeRulesForObject( currentObjectName, versions, out );
				}
				versions.clear();
				currentObjectName = candidateName;
			}
			versions.add( dbs[a].getVersion() );
		}
		writeRulesForObject( currentObjectName, versions, out );
	}
	
	private void writeRulesForObject( String objectName, List<SIFVersion> versions, PrintWriter out )
	{
		StringBuilder sb = new StringBuilder();
		for( SIFVersion version : versions ){
			sb.append( version.toString() );
			sb.append( ',' );
		}
		sb.deleteCharAt( sb.length() - 1 );
		out.println( "      <rule name=\"" + objectName + "\" versions=\"" + sb.toString() + "\" permissions=\"All\" />" );
	}
	

	@Override
	protected void generateObject(ObjectDef o) throws IOException,
			GeneratorException {
		// TODO Auto-generated method stub

	}
}
