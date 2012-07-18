//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public abstract class Generator {
	
	/** The merged DB used to generate classes */
	protected DB fDB;
	/** Output directory */
	protected String fDir;
	protected String fSrcDir;
	
	
	protected Generator( String srcDir, String destDir )
	{
		fSrcDir = srcDir;
		if( destDir.endsWith( File.separator ) )
			fDir = destDir;
		else
			fDir = destDir + File.separator;
	}
	
	public abstract void generate( DB[] dbs )throws IOException, GeneratorException, MergeException;
	
	public void sortAndGenerateObjects( DB[] dbs )throws IOException, GeneratorException, MergeException
	{
		//	Sort the DBs in ascending order.
		Arrays.sort( dbs, new Comparator<DB>()
			{
				public int compare( DB o1, DB o2 ) {
					return o1.getVersion().toString().compareTo( o2.getVersion().toString() );
				}
			}
		);

		//  Merge the DBs into one, beginning with the earliest version and
		//  working to the latest. As versions are merged, we only keep
		//  those differences that result in a unique function prototype. Thus,
		//  later versions overwrite earlier versions unless an earlier version
		//  has unique functionality.

		fDB = dbs[0];
		System.out.println("\r\nMerging metadata...");
		for( int i = 1; i < dbs.length; i++ )
			dbs[i].mergeInto( fDB );

//		System.exit(-1);

		//  Now generate Java class files for each Object type defined in the
		//  merged DB.
		System.out.println("\r\nGenerating object classes...");
		ObjectDef[] objects = fDB.getObjects();
		for( int i = 0; i < objects.length; i++ ) {
			generateObject(objects[i]);
		}
	}
	
	protected void generateObject(ObjectDef o) throws IOException, GeneratorException
	{}
}
