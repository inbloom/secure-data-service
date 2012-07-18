//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MappingsGenerator extends Generator {

	protected MappingsGenerator( String srcDir, String destDir )
	{
		super( srcDir, destDir );
	}
	
	@Override
	public void generate(DB[] dbs) throws IOException, GeneratorException,
			MergeException {
		

		
		// Use the latest database for retrieving package names
		DB db = dbs[ dbs.length - 1 ];
		
		
		//
		//  Create SDOLibrary.java classes...
		//
		for( ObjectDef currentObj : db.getObjects() )
		{
			if( currentObj.isTopic() ){
				//  Generate the package-specific SDOLibrary classes...
				String dir =  fDir+"mappings" + File.separator + 
				currentObj.fPackage;
				
				File md = new File(dir);
				md.mkdirs();
				
				String fName = dir + File.separator + currentObj.getName() + ".xml";
				Map<String, String> enums = new HashMap<String, String>();
				System.out.println("- Generating: "+ fName );
				PrintWriter out = new PrintWriter( new FileWriter(fName),true );
				out.println( "<mappings id=\"Default\">" );
				out.println( "<!-- ============================== -->" );
				out.println( "<!-- Mappings for " + currentObj.getName() + " -->" );
				out.println( "<!-- ============================== -->" );
				out.println( "<object object=\"" + currentObj.getName() + "\">" );
				printMappings( db, currentObj, out, null, null, enums );
				out.println( "</object>" );
				out.println();
				
				for( String key : enums.keySet() ){
					EnumDef def = db.getEnum( key );
					if( def != null ){
						writeValueSet( def, out, enums.get( key ) );
					}
				}
				
				out.println( "</mappings>" );
				out.close();
			}
		}
		
	}

	private void writeValueSet(EnumDef def, PrintWriter out, String name ) throws IOException {

		out.println( "<!-- ============================== -->" );
		out.println( "<!-- ValueSet for " + name + " -->" );
		out.println( "<!-- ============================== -->" );
		out.println( "<valueset id=\"" + name +  "\">" );
		out.println( "<!--   Fill in the name=\"x\" values with the appropriate codes from the application -->" );
		Hashtable t = def.getValues();
		for( Enumeration e = t.elements(); e.hasMoreElements();  ) {
			ValueDef val = (ValueDef)e.nextElement();
			out.println( "   <value name=\"x\" title=\"" + StringUtils.encodeXML( val.fDesc ) + "\">"+ val.fValue +"</value>" );
		}
		out.println( "</valueset>" );
	}
	
	private void printMappings(DB db, ObjectDef def, PrintWriter out, String xPath, String parentTag, Map<String, String> enums )
	{
	
		if( xPath == null ){
			xPath = "";
		}
		
		if( def.getValueType() != null ){
			// This object has a text value (It's an element that has simple content)
			System.out.println( "        - Simple Content" );
		}
		
		
		FieldDef[] fields = def.getAllFields();
		for( int i = 0; i<fields.length; i++ ){
			FieldDef field = fields[i];
			if( ( field.fFlags & FieldDef.FLAG_ATTRIBUTE ) > 0 ){
				System.out.println( "    " + field.fName + " - Attribute" );
				printFieldDesc( field, out );
				out.print( "   <field name=\"" + 
					( parentTag == null ? "" : parentTag.toUpperCase() + "_" ) +  field.getTag().toUpperCase() + "\"" );
				
				if( field.getFieldType().isEnum() ){
					String enumName = field.getFieldType().getEnum();
					String valueSetName = ( parentTag == null ? "" : parentTag + "_" ) +  field.getTag();
					out.print( " valueset=\"" + valueSetName + "\"" );
					enums.put( enumName, valueSetName );
				} 
				out.println( ">" +xPath + "@" + field.getTag() + "</field>" );
			} else {
				FieldType ft = field.getFieldType();
				if( ft.getDataType() == ADKDataType.COMPLEX ){
					
					System.out.println( field.fName + " - Structure (Complex Elements)" );
					ObjectDef fieldObj = db.getObject( ft.getClassType() );
					if( fieldObj != null ){
						if( field.isRepeatable() )
						{
							FieldDef[] key = fieldObj.getKey( null );
							if( key != null && key.length == 1 && ( key[0].fFlags & FieldDef.FLAG_ATTRIBUTE ) > 0  ){
								String newPath = xPath + field.getTag() + "[@" + key[0].getTag() + "='ZZZZ']/";
								if( fieldObj.getValueType() == null ){
									printMappings( db, fieldObj, out, newPath , field.getTag() , enums );
								} else
								{
									printOutFieldMapping( fieldObj.getValueDef( null ), out, newPath, field.getTag(), enums);
								}
								continue;
							}
						}
						printMappings( db, fieldObj, out, xPath + field.getTag() +"/" , field.getTag(), enums );
					}
				} else {
					printOutFieldMapping(field, out, xPath, parentTag, enums);
				}
				
			}
			
			if( field.isRepeatable() ){
			}

		}
		
		if( def.isTopic() )
		{
			// ADK Metadata doesn't include SIF_ExtendedElements or SIF_Metadata. Add these counts in
			ObjectDef o = db.getObject( "SIF_ExtendedElements" );
			if( o != null ){
				printMappings( db, o, out, "SIF_ExtendedElements", "SIF_ExtendedElements", enums );
			}
			
//			o = db.getObject( "SIF_Metadata" );
//			if( o != null ){
//				printMappings( db, o, out, "SIF_Metadata/" );
//			}
		}
		
	}

	private void printOutFieldMapping(FieldDef field, PrintWriter out, String xPath, String parentTag, Map<String, String> enums) {
		System.out.println( "    " + field.fName + " - Simple Element" );
		printFieldDesc( field, out );
		out.print( "   <field name=\"" + 
				( parentTag == null ? "" : parentTag.toUpperCase() + "_" ) +  
				field.getTag().toUpperCase() + "\"" );
		
		if( field.getFieldType().isEnum() ){
			String enumName = field.getFieldType().getEnum();
			String valueSetName = ( parentTag == null ? "" : parentTag + "_" ) +  field.getTag();
			out.print( " valueset=\"" + valueSetName + "\"" );
			enums.put( enumName, valueSetName  );
		} 
		
		boolean isAttribute = ( field.fFlags & FieldDef.FLAG_ATTRIBUTE ) > 0;
		if( ( field.fFlags & FieldDef.FLAG_TEXT_VALUE ) > 0 ){
			out.println( ">" + 	xPath + "</field>" );
		} else
		{
			out.println( ">" + 	xPath + ( isAttribute ? "@" : "" ) +  field.getTag() + "</field>" );
		}
	}
	
	private void printFieldDesc( FieldDef field, PrintWriter out )
	{
		String fieldDesc = field.getDesc();	
		
		if( fieldDesc != null && fieldDesc.length() > 0 ){
			out.println();
			out.print( "   <!-- " );
			if( !field.getName().equals( "Value" ) ){
				out.print( field.getName() );
				out.print( ":  " );
			}
			fieldDesc = fieldDesc.replace( "&lt;", "<" );
			fieldDesc = fieldDesc.replace( "&gt;", ">" );
			out.print( fieldDesc  );
			out.println( " --> " );
		}
	}
	
	


	@Override
	protected void generateObject(ObjectDef o) throws IOException,
			GeneratorException {
		// TODO Auto-generated method stub

	}

}
