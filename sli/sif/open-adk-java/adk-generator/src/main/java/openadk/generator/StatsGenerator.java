//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class StatsGenerator extends Generator
{
	private static boolean DEBUG_OBJECTS = true;
	private static boolean DEBUG_FIELDS = false;
	protected StatsGenerator( String srcDir, String destDir )
	{
		super( srcDir, destDir );
	}

	@Override
	public void generate(DB[] dbs) throws IOException, GeneratorException, MergeException {
		DB db = dbs[ dbs.length - 1 ];
		// Get a list of topics
		Map<String, Stats> statsMap = new HashMap<String, Stats>();
		for( ObjectDef currentObj : db.getObjects() )
		{
			if( currentObj.isTopic() ){
				System.out.println( "OBJECT: " + currentObj.getName() );
				Stats stats = statsMap.get( currentObj.getLocalPackage() );
				if( stats == null ){
					stats = new Stats( currentObj.getLocalPackage() );
					statsMap.put( currentObj.getLocalPackage() , stats );
				}
				incrementStats( db, currentObj, stats );
			}
			
		}
		
		String fn = fDir + File.separator + "SIFStats.txt";
		System.out.println("- Generating: "+fn);
		File outputFile = new File( fn );
		outputFile.getParentFile().mkdirs();

		PrintWriter out = new PrintWriter( new FileWriter( outputFile, true ),true );
		out.println( "Statistics for SIF version: " + db.getVersion().toString() );
		out.println( "--------------------------------------------------------------" );
		out.println();
		Stats combinedStats = new Stats( "All groups (excluding Infrastructure)" );
		for( Stats stats : statsMap.values() ){
			stats.WriteTo( out, "    " );
			out.println();
			if( ! ( stats.Name.equalsIgnoreCase( "infra" ) || stats.Name.equalsIgnoreCase( "common" ) ) ){
				combinedStats.UpdateFrom( stats );
			}
		}
		out.println( "--------------------------------------------------------------" );
		out.println();
		combinedStats.WriteTo( out, "" ); 
		
		out.close();
		
		
	}
	
	private void incrementStats(DB db, ObjectDef def, Stats stats )
	{
		if( def.isTopic() ){
			stats.AllObjects++;
			
			// ADK Metadata doesn't include SIF_ExtendedElements or SIF_Metadata. Add these counts in
			ObjectDef o = db.getObject( "SIF_ExtendedElements" );
			if( o != null ){
				stats.AllStructures++;
				incrementStats( db, o, stats );
			}
			
			o = db.getObject( "SIF_Metadata" );
			if( o != null ){
				stats.AllStructures++;
				incrementStats( db, o, stats );
			}
			
		} 
		

		if( def.getValueType() != null ){
			// This object has a text value (It's an element that has simple content)
			if( DEBUG_FIELDS ){
				System.out.println( "        - Simple Content" );
			}
			stats.AllElements++;
		}
		
		
		FieldDef[] fields = def.getAllFields();
		for( int i = 0; i<fields.length; i++ ){
			FieldDef field = fields[i];
			if( ( field.fFlags & FieldDef.FLAG_ATTRIBUTE ) > 0 ){
				if( DEBUG_FIELDS ){
					System.out.println( "    " + field.fName + " - Attribute" );
				}
				stats.AllAttributes++;
			} else {
				FieldType ft = field.getFieldType();
				if( ft.getDataType() == ADKDataType.COMPLEX ){
					stats.AllStructures++;
					if( DEBUG_FIELDS ){
						System.out.println( field.fName + " - Structure (Complex Elements)" );
					}
					ObjectDef fieldObj = db.getObject( ft.getClassType() );
					if( fieldObj!= null ){
						incrementStats( db, fieldObj, stats );
					} else {
						System.out.println( "ERROR : Field Type : " + ft.getClassType() + 
								" Does not exist for " + def.getName() + "/" + field.getName() );  
					}
				} else {
					if( DEBUG_FIELDS ){
						System.out.println( "    " + field.fName + " - Simple Element" );
					}
					stats.AllElements++;
				}
			}
			
			if( field.isRepeatable() ){
				stats.AllRepeatable++;
			}
			
		}
		
	}

	@Override
	protected void generateObject(ObjectDef o) throws IOException, GeneratorException {
		// TODO Auto-generated method stub
		
	}
	

	private static class Stats{
		
		public String Name;
		public Stats( String name ){
			Name = name;
		}
		public int AllObjects;
		public int AllStructures;
		public int AllElements;
		public int AllRepeatable;
		public int AllAttributes;
		
		public void UpdateFrom( Stats stats ){
			AllObjects += stats.AllObjects;
			AllStructures += stats.AllStructures;
			AllElements += stats.AllElements;
			AllAttributes += stats.AllAttributes;
			AllRepeatable += stats.AllRepeatable;
		}
		
		public void WriteTo( PrintWriter out, String indent ){
			out.println( indent + "Totals for " + Name + ":" );
			out.println( indent + "----------" );
			out.println(indent  + "Objects:             " + AllObjects );
			out.println( indent + "Complex Elements:    " + AllStructures );
			out.println( indent + "Repeatable Elements: " + AllRepeatable );
			out.println( indent + "Simple Elements:     " + AllElements );
			out.println( indent + "Attributes:          " + AllAttributes );
		}
		
	}
	
}
