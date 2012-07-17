//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * GeneratorBase is the base class for code generation classes in AdkGen. It handles the 
 * reusable elements of code generation of the ADK classes, while subclasses implement the
 * language-specific elements
 *
 *
 *  @author Andy Elmhorst
 *  @version 1.0
 */
public abstract class CodeGenerator extends Generator {

	protected CodeGenerator( String srcDir, String destDir )
	{
		super( srcDir, destDir );
	}
	
	/**
	 * Writes a single ElementDef const line to the specified SDO Library class
	 * @param out
	 * @param o
	 */
	protected abstract void writeElementDefConst(PrintWriter out, String commentName, String constName );
	protected abstract void writeToStringOverride(PrintWriter out, FieldDef def);
	protected abstract void writeAliasDefinition(PrintWriter out, String elementDefName, String flags, String aliasVer, String renderAs, String sequence );
	protected abstract void writeEnumClass(PrintWriter out, EnumDef enumDef);
	protected abstract void writeSDOLibraryHeader(PrintWriter out, String className);
	protected abstract void writeClassHeader( PrintWriter writer, ObjectDef def);
	protected abstract void writeClassComment( PrintWriter writer, ObjectDef def);
	protected abstract void writeClassCtor( PrintWriter writer, ObjectDef def );
	protected abstract void writeAbstractMethods( PrintWriter writer, ObjectDef def);
	protected abstract void writeSIFDTDClass(DB database, String dir, DB packageDB) throws IOException;
	protected abstract void writeElementDefCreationLine( 
			PrintWriter out, String dtdSymbol, String parentDtdSymbol,
			boolean useElementDefAlias,String fieldName, String renderName, int sequenceNumber, 
			FieldType classType, String localPackage, SIFVersion earliestVersion,
			SIFVersion latestVersion, String flags, String typeConverter );
	
	protected abstract String getTypeConverterName( FieldType fieldType );
	protected abstract String getADKSimpleType( ADKDataType dataType );
		
	protected abstract void writeComplexField( PrintWriter writer, ObjectDef objectDef, FieldDef field, ObjectDef def) throws GeneratorException;
	protected abstract void writeSimpleField( PrintWriter writer, ObjectDef objectDef, FieldDef field );
	protected abstract void writeDTDHeader( PrintWriter out, DB db, String pkg );
	protected abstract void writeDtdLoad( PrintWriter out );
	protected abstract void writeDTDTableUpdates( PrintWriter out, DB db, String pkg );
	protected abstract void writeEnumClassComment( PrintWriter out, EnumDef enumDef );
	protected abstract void writeEnumHeader( PrintWriter out, EnumDef enumDef );
	protected abstract void writeDTDClassComment( PrintWriter out, DB db ) throws IOException;
	protected abstract void writeDTDAbstractMethods( PrintWriter out );
	protected abstract String symbol(String s);
	protected abstract String getSuperClassSeperatorAndName( ObjectDef superClass );
	
	
	
	/**
	 *  Generate Java classes based on the information in the DB
	 */
	public void generate(DB[] dbs) throws IOException, GeneratorException, MergeException {
			
			super.sortAndGenerateObjects( dbs );
	
			//  Now generate the DTD classes
			System.out.println("\r\nGenerating DTD classes...");
			generateDTDClasses(dbs);
		}

	protected static String[] InfraMessages = {
			"SIF_Ack",
			"SIF_Event",
			"SIF_Provide",
			"SIF_Register",
			"SIF_Request",
			"SIF_Response",
			"SIF_Subscribe",
			"SIF_SystemControl",
			"SIF_Unprovide",
			"SIF_Unregister",
			"SIF_Unsubscribe",
			"SIF_ZoneStatus",
			"SIF_Provision"
		};

	/**
	 *  Generates the SIFDTD class, which extends com.edustructures.sifworks.DTD
	 *  to provide information about the core SIF data type definition. In
	 *  addition, generates a derived class for each version of SIF, where
	 *  the name is the version (e.g."SIF10r1").
	 *
	 *  @param dbs An array of DB objects sorted by SIF version number
	 */
	protected void generateDTDClasses(DB[] databases) throws IOException, GeneratorException {
	
		String dir = fDir+"openadk/library";
		
		// Use the latest database for retrieving package names
		DB packageDB = databases[ databases.length - 1 ];
		
		PrintWriter out;
		writeSIFDTDClass(databases[0], dir,  packageDB);

		//
		//  Create SDOLibrary.java classes...
		//
		for( String sdoName : packageDB.getDefinitionFileKeysSet() )
		{
			
			//  Generate the package-specific SDOLibrary classes...
			String packageDir = fDir+"openadk/library/" + sdoName;

		    String packageSDOName = sdoName.substring( 0 , 1 ).toUpperCase() + sdoName.substring( 1 ) + "DTD";
		    
			File md = new File(packageDir);
			md.mkdirs();
			String packageFn = packageDir + File.separator + packageSDOName + getFileExtension() ;
			System.out.println("- Generating: "+ packageFn );

			out = null;
			try
			{
				out = new PrintWriter(new OutputStreamWriter(new FileOutputStream( packageFn ), "utf-8"),true );
				writeDTDHeader(out, packageDB ,sdoName);
				writeDTDClassComment(out, packageDB );
				writeSDOLibraryHeader(out, packageSDOName );

				//  Write out the ElementDef statics for each object
				ObjectDef[] o = databases[0].getObjects();
				// Sort them
				Arrays.sort( o, new Comparator<ObjectDef>()
					{
						public int compare( ObjectDef o1, ObjectDef o2 ) {
							return o1.getDTDSymbol().compareTo( o2.getDTDSymbol() );
						}
					}
				);
				
				// Write the ElementDef constants to the DTD class
				writeDTDConstants( out, sdoName, o );
				
				// Write the load() method on the DTD class
				writeDTDLoad(out, sdoName, packageDB, o);
				
				out.println();
				
				// Write the addElementMappings method on the DTD class
				writeDTDTableUpdates( out, databases[0], sdoName );

				writeClassFooter(out);
			}
			finally
			{
				if( out != null ) {
					try {
						out.close();
					} catch( Exception ex ) {
					}
				}
			}
		}

		//  Generate the Enumeration classes
		System.out.println("\r\nGenerating enum classes...");
		writeEnumClasses(dir, databases[0].getEnums() );
		
	}

	private void writeEnumClasses(String dir, EnumDef[] enums) throws IOException {
		String fn;
		PrintWriter out;
		for( int i = 0; i < enums.length; i++ )
		{
			File md = new File( dir + File.separator + enums[i].fPackage );
			md.mkdirs();

			fn = dir + File.separator + enums[i].fPackage+File.separator+enums[i].getName()+ getFileExtension();
			System.out.println("- Generating: "+fn);

			out = null;
			try
			{
				out = new PrintWriter(new OutputStreamWriter(new FileOutputStream( fn ), "utf-8"),true );
				writeEnumHeader(out,enums[i]);
				writeEnumClassComment(out,enums[i]);
				writeEnumClass(out, enums[i]);
			}
			finally
			{
				if( out != null ) {
					try {
						out.close();
					} catch( Exception e ) {
					}
				}
			}
		}
	}

	/**
	 * Write the load() method on the specified DTD class
	 * @param out
	 * @param sdoName
	 * @param db
	 * @param o
	 */
	private void writeDTDLoad(PrintWriter out, String sdoName, DB db, ObjectDef[] o) {
		// Write out the initialize() method
		out.println();
		writeDtdLoad( out );
		out.println("\t{");
		out.println("\t\t//  Objects defined by this SDO Library...");
		out.println();
		
		Vector<ObjectDef> sdoObjects = new Vector<ObjectDef>();
		for( int k = 0; k < o.length; k++ )
		{
			boolean trap = o[k].getName().equalsIgnoreCase("SIF_LogEvent");
			if( ( o[k].getFlags() & ObjectDef.FLAG_NO_SIFDTD ) == 0 )
			{
				if( !o[k].getLocalPackage().equals( sdoName ) ) {
					if( trap ) System.out.println("************1");
					continue;
				}

				sdoObjects.addElement(o[k]);
				String typeConverter = null;
				FieldType ft = o[k].getValueType();
				if( ft != null ){
					typeConverter = getTypeConverterName( ft );
				}
				

				writeElementDefCreationLine( 
						out, o[k].getDTDSymbol(), null, false, 
						o[k].getName(), o[k].getRenderAs(), 
						o[k].getSequenceOverride() == -1 ? 0 : o[k].getSequenceOverride(),
						o[k].getValueType(), o[k].getLocalPackage(),
						o[k].getEarliestVersion(), 
						o[k].getLatestVersion(), 
						( o[k].isTopic() ? "ElementDefImpl.FD_OBJECT":"" ), 
						typeConverter );
				
				//	Write out any aliases
				Map<String, List<SIFVersion>> aliases = o[k].getAliases();
				if( aliases != null )
				{
					for( Map.Entry<String, List<SIFVersion>> entry : aliases.entrySet() )
					{
						SIFVersion aliasVer = entry.getValue().get( 0 );
						StringBuffer buf = new StringBuffer();
						buf.append( aliasVer.getMajor() );
						buf.append( aliasVer.getMinor() );
						if( aliasVer.getRevision() > 0 ){
							buf.append( "r" );
							buf.append( aliasVer.getRevision() );
						}
						
						//LibraryDTD.TRANSACTIONLIST_TRANSACTION.defineVersionInfo(SIFVersion.SIF20, "Transaction", 1, (ElementDefImpl.FD_REPEATABLE)); // (SIF 20 alias)
						writeAliasDefinition(out, o[k].getDTDSymbol(), "0", buf.toString(), entry.getKey(), "0" );
					}
				}
				
				
			}
		}

		out.println();

		//  Write out a static ElementDef defining each SIF element
		for( int k = 0; k < sdoObjects.size(); k++ )
		{
			ObjectDef oo = (ObjectDef)sdoObjects.elementAt(k);

			out.println();
			out.println("\t\t// <" + ( oo.getRenderAs() == null ? oo.getName() : oo.getRenderAs() ) + "> fields (" + oo.getAllFields().length + " entries)" );
			
			FieldDef[] fields = oo.getDTDFields();

			for( int f = 0; f < fields.length; f++ )
			{
				if( ( fields[f].getFlags() & ObjectDef.FLAG_NO_SIFDTD ) == 0 )
				{
					String flags = getFieldFlags( fields[f].getFlags() );

					String fieldClassType = fields[f].getFieldType().getClassType();
					ObjectDef fod = db.getObject( fieldClassType );

					
					boolean useElementDefAlias = fields[f].isComplex() && !fieldClassType.equals(fields[f].getName());

					// if( db.getVersion().compareTo( fields[f].getEarliestVersion() ) >= 0 )
					{
						String packageName = (fod == null ? oo.getLocalPackage() : fod.getLocalPackage());
						String typeConverterName = null;
						if( !fields[f].isComplex() ){
							typeConverterName = getTypeConverterName( fields[f].getFieldType() );
						} else if ( fod != null ) {
							FieldType objSimpleType = fod.getValueType();
							if( objSimpleType != null ){
								typeConverterName = getTypeConverterName( objSimpleType );
							}
						}
								
						writeElementDefCreationLine( 
								out, fields[f].getElementDefConst( this ), oo.getDTDSymbol(), useElementDefAlias,
								 fields[f].getName(),  fields[f].getElementDefExpression(),  fields[f].getSequence(),  fields[f].getFieldType(),
								 packageName,  fields[f].getEarliestVersion(), fields[f].getLatestVersion(),  flags.toString(), typeConverterName );
												
						//  Write out any aliases
						Set<Alias> aliases = fields[f].getAliases();
						if( aliases != null )
						{
							for(Alias alias : aliases)
							{
								SIFVersion aliasVer = alias.getVersion();
								StringBuffer buf = new StringBuffer();
								buf.append( aliasVer.getMajor() );
								buf.append( aliasVer.getMinor() );
								if( aliasVer.getRevision() > 0 ){
									buf.append( "r" );
									buf.append( aliasVer.getRevision() );
								}

								writeAliasDefinition(
										out, fields[f].getElementDefConst( this ), 
										getFieldFlags( alias.getFlags() ), buf.toString(), 
										alias.getElementDefExpression(), String.valueOf( alias.getSequence() ) );
							}
						}
					}
				}
			}
			
			if( oo.isTopic() ) {
				//latestVersion may not be the latest one supported.  ie, if you want to do a build of the old dm, or if .net is behind/ahead of java in terms of sif version support
				SIFVersion latestVersion = SIFVersion.getEarliest(1);
				
				for ( DB dbInList : Main.self.fDBs.values() ) {
					if ( dbInList != null && dbInList.fVersion.compareTo(latestVersion) > 0 )
						latestVersion = dbInList.fVersion;
				}
				
				writeElementDefCreationLine( out, oo.getDTDSymbol() + "_SIF_EXTENDEDELEMENTS", oo.getDTDSymbol(), false, "SIF_ExtendedElements", null, 
						127, null, (Main.self.fLanguage.equals("cs")? "global" : "common"), SIFVersion.SIF15r1, latestVersion, "0", null );
				writeElementDefCreationLine( out, oo.getDTDSymbol() + "_SIF_METADATA", oo.getDTDSymbol(), false, "SIF_Metadata", null, 
						128, null, "datamodel", SIFVersion.SIF20, latestVersion, "0", null );
			}
		}

		out.println("\t}");
	}

	/**
	 * Write the element def constants for the specified library class
	 * @param out
	 * @param sdoName
	 * @param o
	 */
	private void writeDTDConstants(PrintWriter out, String sdoName, ObjectDef[] o) {
		// Write out the public constants defined by this package
		int dtdItemCount = 2;
		for( int k = 0; k < o.length; k++ )
		{
			if( !o[k].getLocalPackage().equals( sdoName ) ){
				continue;
			}
			if( ( o[k].getFlags() & ObjectDef.FLAG_NO_SIFDTD ) == 0 )
			{
				writeElementDefConst(out, "Defines the &lt;" + o[k].getName()+"&gt; SIF Data Object", o[k].getDTDSymbol()  );
				dtdItemCount++;
			}
		}

		out.println();

		String comment = null;

		for( int k = 0; k < o.length; k++ )
		{
			if( !o[k].getLocalPackage().equals( sdoName ) ){
				continue;
			}
			out.println();
			out.println("\t// Field elements of "+o[k].getDTDSymbol() + " (" + o[k].getAllFields().length + " fields)" );

			FieldDef[] fields = o[k].getAllFields();
			for( int f = 0; f < fields.length; f++ )
			{
				if( ( fields[f].getFlags() & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
					comment = fields[f].getName() + " attribute";
				}
				else
					comment = "&lt;" + fields[f].getName() + "&gt; element";

				if( ( fields[f].getFlags() & FieldDef.FLAG_NO_SIFDTD ) == 0 )
				{
					writeElementDefConst( out, "Defines the " + comment + " as a child of &lt;" + o[k].getName() + "&gt;", fields[f].getDTDSymbol()  );
					dtdItemCount++;
				}
			}

			if( o[k].isTopic() ) {
				writeElementDefConst( out, 
						"SIF 1.5 and later: Defines the built-in SIF_ExtendedElements element common to all SIF Data Objects",
						 o[k].getDTDSymbol() + "_SIF_EXTENDEDELEMENTS" );
				writeElementDefConst( out, 
						"SIF 2.0 and later: Defines the built-in SIF_Metadata element common to all SIF Data Objects",
						o[k].getDTDSymbol() + "_SIF_METADATA" );
				dtdItemCount+=2;
			}
		}
	}



	private String getFieldFlags(  int flags ) {
		StringBuffer strFlags = new StringBuffer();

		if( ( flags & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
			strFlags.append("ElementDefImpl.FD_ATTRIBUTE");
		} else if( ( flags & FieldDef.FLAG_COMPLEX ) == 0) {		
			 strFlags.append("ElementDefImpl.FD_FIELD");
		}

		if( ( flags & FieldDef.FLAG_DO_NOT_ENCODE ) != 0 ) {
			if( strFlags.length() > 0 )
				strFlags.append('|');
			strFlags.append("ElementDefImpl.FD_DO_NOT_ENCODE");
		}
		
		if( ( flags & FieldDef.FLAG_COLLAPSED ) != 0 ) {
			if( strFlags.length() > 0 )
				strFlags.append('|');
			strFlags.append("ElementDefImpl.FD_COLLAPSE");
		}

		if( (flags & FieldDef.FLAG_DEPRECATED) != 0 ) {
			if( strFlags.length() > 0 )
				strFlags.append('|');
			strFlags.append("ElementDefImpl.FD_DEPRECATED");
		}

		if( ( flags & FieldDef.FLAG_REPEATABLE ) != 0 ) {
			if( strFlags.length() > 0 )
				strFlags.append('|');
			strFlags.append("ElementDefImpl.FD_REPEATABLE");
		}
		return strFlags.toString();
	}
	
	
	
	protected String an(String str, boolean upperCase, boolean code) {
		StringBuffer b = new StringBuffer();
		char ch = Character.toLowerCase(str.charAt(0));
		if( ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == 'h' )
			b.append( upperCase ? "An":"an" );
		else
			b.append( upperCase ? "A":"a" );
	
		b.append(" ");
		if( code )
			b.append("<code>");
		b.append(str);
		if( code )
			b.append("</code>");
	
		return b.toString();
	}

	protected String plural(String str) {
		if( str.endsWith("s") )
			return str+"es";
		return str+"s";
	}

	

	protected void writeExtras(PrintWriter out, ObjectDef o) throws IOException {
		writeExtras( out, o.getExtrasFile() );
	}

	protected void writeExtras(PrintWriter out, String fn) throws IOException {
		if( fn != null )
		{
			BufferedReader in = null;
			try
			{
				in = new BufferedReader( new FileReader(fn) );
				String fn2 = fn.replace('\\','/');
				out.println("\r\n// BEGIN EXTRA METHODS ("+fn2+")\r\n");
	
				do
				{
					String s = in.readLine();
					out.println(s);
					System.out.println(s);
				}
				while( in.ready() );
	
				out.println("\r\n// END EXTRA METHODS\r\n");
			}
			finally
			{
				if( in != null ) {
					try {
						in.close();
					} catch( IOException ignored ) { }
				}
			}
		}
	}

	protected void writeFileHeader(PrintWriter out) {
		out.println("// THIS FILE WAS AUTO-GENERATED BY ADKGEN -- DO NOT MODIFY!");
		out.println();
		out.println("//");
		out.println("// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s).");
		out.println("// All rights reserved.");
		out.println("//");
		out.println();
	}

	/**
	 *  Generates a CSharp class file for an ObjectDef
	 */
	protected void generateObject(ObjectDef o) throws IOException, GeneratorException {
		
		if ( this instanceof JavaGenerator && "SIF_ExtendedElement".equals( o.fName ) ) {
			return;
		}
		
		String dir = fDir+"openadk.library."+ toProperCase( o.getLocalPackage() );
		dir = dir.replace('.',File.separatorChar);
		String fn = dir+File.separator+o.getName()+ getFileExtension();
		System.out.println("- Generating: "+fn);
	
		PrintWriter out = null;
		try
		{
			File md = new File(dir);
			md.mkdirs();
	
			out = new PrintWriter( new OutputStreamWriter(new FileOutputStream( fn ), "utf-8"),true );
			writeClassHeader(out,o);
			writeClassCtor(out,o);
			writeAbstractMethods(out,o);
			
			// If the object has a simple content model, write out an accessor
			// for the element content. This will consist of a single property 
			// name "Value" that gets and sets the text value of the element.
			if( o.getName().equals( "StringElement" ) ){
				System.out.println( "Break" );
			}
			
			FieldDef value =  o.getValueDef( this );
			if( value != null ){
				writeSimpleField( out, o, value );
			}
	
			//  Now write out getter/setter methods for each FieldDef. If the
			//  FieldDef is a complex object, multiple getters/setters may be
			//  written. If the FieldDef is a simple string-type field, only
			//  one set of getter/setter is written.
			//
			FieldDef[] fields = o.getAllFields();
			ADKElementType elementType = o.getElementType( fDB, this );
			for( int i = 0; i < fields.length; i++ )
			{
				if( fields[i].isComplex() )
				{
					ObjectDef ref = null;
					String classType = fields[i].getFieldType().getClassType();
					ref = fDB.getObject( classType );
					
					if( ref == null ) {
						throw new GeneratorException(
								fields[i].getName()+
								" references object type \"" + 
								classType +
								"\", but that object type is not defined" );
					} else {
					    writeComplexField(out,o,fields[i],ref);
					}
				}

				else{
					writeSimpleField(out,o,fields[i]);
				}
			}
	
			//  Special Case: If the element isEmpty and has only one attribute,
			//  override the toString() method to return that attribute's
			//  value (e.g. <StatePr Code="UT"/> falls into this category.)
			//
			if( o.isEmpty() )
			{
				FieldDef[] req = o.getMandatoryFields( this );
				if( req != null && req.length == 1 )
				{
					FieldDef def = req[0];
					writeToStringOverride(out, def);
				}
			}
	
			try
			{
			    writeExtras(out,o);
			}
			catch( Exception e )
			{
				throw new GeneratorException("Could not copy extras file for object "+o.getName()+": " + e );
			}
	
			writeClassFooter(out);
		}
		finally
		{
			if( out != null ) {
				try {
					out.close();
				} catch( Exception e ) {
				}
			}
		}
	}

	protected void writeClassFooter(PrintWriter out) {
		out.println("}");
	}
	/**
	 * @param m
	 * @return Returns an array of strings. The first element is the data type that should be passed in
	 * to the constructor. The second element is the argument name to use. The third element is a block of code that creates the actual datatype to set
	 * in the parent object.
	 */
	/* Unused Code
	protected String[] getConstructorParts( FieldDef m )
	{
		String[] returnVal = null;
		ObjectDef paramType = fDB.getObject(m.getName());
		if( paramType != null && paramType.getFields().length == 1 && !paramType.hasValueText() ) 
		{
			FieldDef[] mandatoryFields = paramType.getMandatoryFields();
	    	if( mandatoryFields.length == 1 && mandatoryFields[0].getEnum() == null )
	    	{
	    		returnVal = getConstructorParts( mandatoryFields[0] );
	    		if( returnVal == null )
	    		{
	    			returnVal = new String[3];
    				returnVal[0] = mandatoryFields[0].getClassType();
	    			returnVal[1] = toArgument( mandatoryFields[0].getName() );
	    			if( mandatoryFields[0].getClassType().equals("String"))
					{
						returnVal[2] = returnVal[1];
					}
					else
					{
						returnVal[2] = " new " + m.getClassType()+"( " + returnVal[1] + " ) " ;
					}
	    		}
	    		else
	    		{
	    			returnVal[2] = " new " + m.getClassType()+"( " + returnVal[2] + " ) " ;
	    		}
	    	}
	    	return returnVal;
		}

		return null;
	}
	protected abstract String toArgument( String src );
	*/
	protected String toArgument( FieldDef m )
	{
		ObjectDef paramType = fDB.getObject(m.getName());
		if( paramType != null && paramType.getAllFields().length == 1 && paramType.getValueDef( this ) == null ) {
			FieldDef[] mandatoryFields = paramType.getMandatoryFields( this );
	    	if( mandatoryFields.length == 1  ){
	    		if( mandatoryFields[0] == m ||	toArgument(mandatoryFields[0]) == null ){
	    			return mandatoryFields[0].getFieldType().getClassType();
	    		}
	    	}
		}
		return null;
	}

	protected abstract String toProperCase(String s);

	protected abstract String getFileExtension();

	protected void writeDTDTableUpdatesForObject(
			PrintWriter out, 
			DB db, 
			ObjectDef obj, 
			HashSet<String>alreadyAdded ) {
	
		String objName = obj.getName();
		
		
		if( objName.equals( "Demographics" ) ){
			System.out.println("Ready to Break;");
		}
		
		SortedSet<TagItem> tags = new TreeSet<TagItem>();
		
		addAliases( db, obj, objName, tags, obj.getLatestVersion(), null );
		
		for( TagItem tagItem : tags ){
			if( alreadyAdded.contains( tagItem.fTag ) ){
				throw new RuntimeException( tagItem.fTag + " is already defined." );
			}
			alreadyAdded.add( tagItem.fTag );
			writeSingleDTDTableUpdate( out, tagItem.fTag, tagItem.fElementDefConst, tagItem.fComment );
		}
	}
	
	private void addAliases( DB db, ObjectDef obj, String parentObjName, Set<TagItem> addTo, SIFVersion maxVersion, String comment )
	{
		
		for( FieldDef field : obj.getAllFields() ){
			if( (field.getFlags() & FieldDef.FLAG_NO_SIFDTD ) != 0  ||
				(field.getFlags() & FieldDef.FLAG_TEXT_VALUE ) != 0	){
				continue;
			}
			
			if( parentObjName.equals( "Demographics" ) && field.getName().equals( "RaceList" ) ){
				System.out.println( "Ready to Break" );
			}
		
			String elementDefConst = field.getElementDefConst( this );
			String renderAs = field.getRenderAs();
			if( renderAs == null ){
				renderAs = field.getName();
			}

			addTo.add( new TagItem( parentObjName + "_" + renderAs, elementDefConst, comment ) );
			
			if( ( field.getFlags() & FieldDef.FLAG_COLLAPSED ) != 0 ){
				ObjectDef collapsedContainer = db.getObject( field.getFieldType().getClassType() );
				addAliases( db, collapsedContainer, parentObjName, addTo, field.getEarliestVersion(), "Collapsed in " + field.getEarliestVersion() );
			}
			
			Set<Alias> aliases = field.getAliases();
			if( aliases != null ){
				for( Alias alias : aliases )
				{
					
					SIFVersion version = alias.getVersion();
					if( version.compareTo( maxVersion ) <= 0 ){
						if( ( alias.getFlags() & FieldDef.FLAG_COLLAPSED ) != 0 ){
							ObjectDef collapsedContainer = db.getObject( field.getFieldType().getClassType() );
							addAliases( db, collapsedContainer, parentObjName, addTo, version, "Collapsed in " + version );
						} else {
							TagItem item = new TagItem( parentObjName + "_" + alias.getTag(), elementDefConst, version.toString() + " alias" );
							addTo.add( item );
						}
					}
				}
			}
		}
	}
	
	

	protected abstract void writeSingleDTDTableUpdate(PrintWriter out, String tagCombination, String dtdConstName, String addComment);
	
	private static class TagItem implements Comparable<TagItem>
	{
		private String fTag;
		private String fComment;
		private String fElementDefConst;

		public TagItem( String tag, String elementDefConst, String comment )
		{
			fTag = tag;
			fComment = comment;
			fElementDefConst = elementDefConst;
		}
		
		public int compareTo( TagItem tag2 ){
			return fTag.compareTo( tag2.fTag );
		}
		
		@Override
		public boolean equals(Object arg0) {
			if( arg0 == null ){
				return false;
			}
			if( !(arg0 instanceof TagItem) ){
				return false;
			}
			return fTag.equals(((TagItem)arg0).fTag);
		}
		
		@Override
		public int hashCode() {
			return fTag.hashCode();
		}
	}
	
	
}
