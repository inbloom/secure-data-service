//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 *  Generates ADK classes given an array of <code>DB</code> objects for each
 *  version of the SIF specification parsed during the parsing phase. The DB
 *  objects are merged into one, and from that the various .java classes are
 *  generated. This includes one .java class per ObjectDef; one .java class per
 *  EnumDef; the SIFDTD.java class; and one SIFDTD sub-class for each version of
 *  SIF represented in the set of parsed files (e.g. SIF10r1.java, SIF10.java,
 *  etc.)
 *
 */
public class JavaGenerator extends CodeGenerator
{
	/**
	 *  Constructor
	 */
    public JavaGenerator( String srcDir, String dstDir )
	{
    	super( srcDir, dstDir );
	}

    protected String getFileExtension(){
    	return ".java";
    }

	protected void writeClassHeader( PrintWriter out, ObjectDef o )
	{
		writeFileHeader(out);

		String opackage = o.getLocalPackage();
		
		if ( o.fName.contains("Currency")){
			out.print("");
		}
		
		out.println("package openadk.library."+o.getLocalPackage()+";");
		out.println();
		out.println("import openadk.library.*;");
		if( !o.getLocalPackage().endsWith("common") )
			out.println("import openadk.library.common.*;");
		if( o.getLocalPackage().endsWith("reporting"))
			out.println( "import openadk.library.infra.*;");
		if( o.getLocalPackage().endsWith("etranscripts")){
			out.println( "import openadk.library.gradebook.*;");
			out.println( "import openadk.library.student.*;");
		}
		out.println("import java.math.BigDecimal;");
		out.println("import java.util.*;");
		out.println();
		writeClassComment(out,o);
		out.println("public class "+o.getName() + getSuperClassSeperatorAndName( o ) );
		out.println("{");
		out.println( "\tprivate static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;" );
	}

	protected void writeDTDHeader( PrintWriter out, DB db, String pkg )
	{
		writeFileHeader(out);

		if( pkg != null )
			out.println("package openadk.library." + pkg + ";");
		else
			out.println("package openadk.library;");

		out.println();
		out.println("import java.util.*;");
		out.println("import openadk.library.*;");
		out.println("import openadk.library.common.CommonDTD;");
		out.println("import openadk.library.datamodel.DatamodelDTD;");
		out.println("import openadk.library.infra.InfraDTD;");
		out.println("import openadk.library.impl.*;");
		out.println();
	}

	protected void writeEnumHeader( PrintWriter out, EnumDef enumDef )
	{
		writeFileHeader(out);

		out.println("package openadk.library."+enumDef.fPackage+";");
		out.println();
		out.println("import openadk.library.*;");
		out.println();
	}

	protected void writeEnumClass(PrintWriter out, EnumDef enumDef) {
		out.println("public class "+enumDef.getName()+" extends SIFEnum");
		out.println("{");
		out.println( "\tprivate static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;" );

		int vv = 0;
		Hashtable t = enumDef.getValues();
		for( Enumeration e = t.elements(); e.hasMoreElements(); vv++ ) {
			ValueDef val = (ValueDef)e.nextElement();
			out.println("\t/**");
			out.println("\t *  " + ( val.fDesc == null ? val.fValue : val.fDesc ) + " (\""+val.fValue+"\")" );
			out.println("\t */");
			out.println("\tpublic static final "+enumDef.getName()+" "+symbol(val.fName)+" = " +
				"new "+enumDef.getName()+"(\""+val.fValue+"\");");
			out.println();
		}

		out.println("\t/**");
		out.println("\t *  Wrap an arbitrary string value in "+an(enumDef.getName(),false,false)+" object.");
		out.println("\t *  @param value The element/attribute value. This method does not verify");
		out.println("\t *      that the value is valid according to the SIF Specification.");
		out.println("\t */");
		out.println("\tpublic static "+enumDef.getName()+" wrap( String value ) {");
		out.println("\t\treturn new "+enumDef.getName()+"( value );");
		out.println("\t}");
		out.println();
		out.println("\tprivate "+enumDef.getName()+"( String value ) {");
		out.println("\t\tsuper(value);");
		out.println("\t}");
		out.println("}");
	}

	protected void writeSDOLibraryHeader(PrintWriter out, String className ) {
		out.println("public class " + className + " extends openadk.library.impl.SDOLibraryImpl");
		out.println("{");
	}

	protected void writeClassComment( PrintWriter out, ObjectDef o )
	{
		out.println("/**");
		out.println(" *  "+o.getDesc()+"<p>");
		if( o.isDraft() )
			out.println("\t *  <font face='verdana,helvetica' size='-1' color='red'><b>Note:</b> This is a Draft Object and is subject to change or removal prior to its final approval. Edustructures includes Draft Objects in the SIFWorks&reg; Agent Developer Kit (ADK) for early access by developers. In the future, this class may be changed or removed for consistency with the latest approved SIF Specification.</font><p>");
		out.println(" *");
		out.println(" *  @author Generated by adkgen");
		out.println(" *  @version "+o.getLatestVersion());
		out.println(" *  @since "+o.getEarliestVersion());
		out.println(" */");
	}

	protected void writeDTDClassComment( PrintWriter out, DB db ) throws IOException
	{
		out.println("/**");
		out.println(" *  Represents this package to the SIF Data Objects library. This class is used internally by the ADK.<p>");
		out.println(" *");
		out.println(" *  @author Generated by adkgen");
		out.println(" */");
	}

	protected void writeEnumClassComment( PrintWriter out, EnumDef enumDef )
	{
		out.println("/**");
		if( enumDef.getDesc() != null && enumDef.getDesc().length() > 0 )
		{
			out.println(" *  " + enumDef.getDesc() + "<p>" );
		}
		out.println(" *  Defines the set of values that can be specified whenever "+an(enumDef.getName(),false,false));
		out.println(" *  is used as a parameter to a method or constructor. Alternatively, the static");
		out.println(" *  <code>wrap</code> method can be called to encapsulate any string value in");
		out.println(" *  "+an(enumDef.getName(),false,false)+" object.<p>");
		out.println(" *");
		out.println(" *  @author Generated by adkgen");
		out.println(" *  @version "+enumDef.getLatestVersion());
		out.println(" *  @since "+enumDef.getEarliestVersion());
		out.println(" */");
	}

	protected void writeClassCtor( PrintWriter out, ObjectDef o )
	{
		int loopCount = ( o.isShared() ? 2 : 1 );

		for( int loop = 0; loop < loopCount; loop++ )
		{
			if( loop == 0 )
			{
				out.println("\t/**");
				out.println("\t *  Constructor");
				out.println("\t */");
				if( o.isTopic() )
				{
					out.println("\tpublic "+o.getName()+"() {");
					out.println("\t\tsuper( ADK.getSIFVersion(), "+o.getPackageQualifiedDTDSymbol()+" );");
				}
				else
				{
					out.println("\tpublic "+o.getName()+"() {");
					out.println("\t\tsuper( "+o.getPackageQualifiedDTDSymbol()+" );");
				}

				if( o.isInfra() ){
					out.println("\t}");
					out.println();
					out.println("\t/**");
					out.println("\t *  Constructor that accepts a SIFVersion");
					out.println("\t *  @param sifVersion The version of SIF to render this message in");
					out.println("\t */");
					out.println("\tpublic "+o.getName()+"( SIFVersion sifVersion ) {");
					out.println("\t\tsuper( sifVersion, "+o.getPackageQualifiedDTDSymbol()+" );");
				}
			}
			else
			{
				//  Write out a second form of constructor that accepts an
				//  ElementDef as its first parameter
				out.println("\t/**");
				out.println("\t *  This constructor is used by the SDO class library. It accepts an alternate");
				out.println("\t *  ElementDef constant.");
				out.println("\t */");
				out.println("\tprotected "+o.getName()+"( ElementDef alias ) {");
				out.println("\t\tsuper( alias );");
			}
			out.println("\t}");
			out.println();

			if( o.getSuperclass().equals("SIFTime") )
			{
				//
				//	Special Case: Inherit the constructors from SIFTime
				//
				out.println("/**");
				out.println(" *  Constructs a " + o.getName() + " object from a SIF timezone and time string<p>" );
				out.println(" *  @param timeZone A SIF timezone value");
				out.println(" *  @param time A SIF time value");
				out.println(" */");
				out.println("public " + o.getName() + "( String timeZone, String time )");
				out.println("{");
				out.println("\tsuper( timeZone, time );");
				out.println("\tfElementDef = SIFDTD."+o.getDTDSymbol()+";" );
				out.println("}");
				out.println("");

				out.println("/**");
				out.println(" *  Constructs a " + o.getName() + " using the TimeZone of the current locale");
				out.println(" *  @param time A SIF time value");
				out.println(" */");
				out.println("public " + o.getName() + "( String time )");
				out.println("{");
				out.println("\tsuper( time );");
				out.println("\tfElementDef = SIFDTD."+o.getDTDSymbol()+";" );
				out.println("}");
				out.println("");

				out.println("/**");
				out.println(" *  Constructs a " + o.getName() + " object from a java.util.Date object using the");
				out.println(" *  local TimeZone as returned by Java<p>");
				out.println(" *  @param time The time");
				out.println(" */");
				out.println("public " + o.getName() + "( java.util.Date time )");
				out.println("{");
				out.println("\tsuper( time );");
				out.println("\tfElementDef = SIFDTD."+o.getDTDSymbol()+";" );
				out.println("}");
				out.println("");

				out.println("/**");
				out.println(" *  Constructs a " + o.getName() + " object from a java.util.Date object using the");
				out.println(" *  specified TimeZone<p>");
				out.println(" *  @param timeZone The TimeZone");
				out.println(" *  @param time The time");
				out.println(" */");
				out.println("public " + o.getName() + "( TimeZone timeZone, java.util.Date time )");
				out.println("{");
				out.println("\tsuper( timeZone, time );");
				out.println("\tfElementDef = SIFDTD."+o.getDTDSymbol()+";" );
				out.println("}");
				out.println("");
			}

			//FieldType objValueType = o.getValueType();
			FieldDef[] m = o.getMandatoryFields( this );

			if(  m.length == 0 && !o.isInfra() ){
				// If this is a list container, it will have a single, repeatable
				// element as a child. Use this as a constructor
				FieldDef[] all = o.getAllFields();
				if( all.length == 1 && all[0].isRepeatable() ){
					m = all;
				}
			}

			if( (  m.length != 0 ) && !o.isInfra() )
			{
				out.println("\t/**");
				out.println("\t *  Constructor that accepts values for all mandatory fields");

				for( int i = 0; i < m.length; i++ )
					out.println("\t *  @param "+toProperCase(m[i].getName())+" "+m[i].getDesc());
//				if( objValueType != null )
//					out.println("\t *  @param value The value of the element");
				out.println("\t */");

				out.print("\t" + ( loop == 1 ? "protected" : "public" ) + " " + o.getName() + "( ");

				if( loop == 1 )
					out.print( "ElementDef alias, " );

				//  Insert all mandatory fields as constructor parameters.
				//
				//  Special Case: Any field that itself has one mandatory field
				//  will be included such that the field's single mandatory element
				//  type is specified instead of the field itself. For example,
				//  StatePr has one mandatory attribute, Code, which is of type
				//  StatePrCode. Rather than force agents to specify a StatePr
				//  object in the constructor, we use StatePrCode instead; in the
				//  constructor body we then automatically wrap the StatePrCode in
				//  a StatePr child element. Thus,
				//
				//  Instead of:
				//      public SIFDataObjectClass( StatePr state, ... ) {
				//          addChild(state);
				//
				//  Use:
				//      public SIFDataObjectClass( StatePrCode state, ... ) {
				//          addChild( new StatePr(state) );
				//
				for( int i = 0; i < m.length; i++ )
				{
					//  Is the aforementioned Special Case applicable to this field?
					String specialCaseType = toArgument(m[i]);
					if( specialCaseType != null )
						out.print(specialCaseType+" "+toProperCase(m[i].getName()));
					else
						out.print( getJavaType( m[i].getFieldType() ) + " "+toProperCase(m[i].getName()));

					if( i != m.length-1 )
						out.print(", ");
				}

//				if( objValueType != null ) {
//					if( m.length > 0  ){
//						out.print(", ");
//					}
//					out.print( getJavaType( objValueType ) +  " elementValue" );
//				}

				out.println(" ) {");
				if( o.isTopic() ) {
					out.println("\t\tsuper( ADK.getSIFVersion(), "+o.getPackageQualifiedDTDSymbol()+" );");
				}
				else
				{
					if( loop == 1 ) {
						out.println("\t\tsuper( alias );");
					} else {
						out.println("\t\tsuper( "+o.getPackageQualifiedDTDSymbol()+" );");
					}
				}

				for( int i = 0; i < m.length; i++ )
				{

					//  Is the aforementioned Special Case applicable to this field?
					if( toArgument( m[i] ) != null )
					{
						String argName;
						ADKDataType dataType = m[i].getFieldType().getDataType();
						if( !(dataType == ADKDataType.STRING || dataType == ADKDataType.ENUM ) )
						{
							argName = " new " + m[i].getFieldType().getClassType() + "( " +
								toProperCase( m[i].getName() ) + " )";
						}
						else
						{
							argName = toProperCase( m[i].getName() );
						}
						writeSetValueToField( out, o, m[i], argName );
					}
					else
					{
						writeSetValueToField( out, o, m[i], toProperCase( m[i].getName()));
					}
				}

//					//  Is the aforementioned Special Case applicable to this field?
//					if( specialCase_1(m[i]) != null )
//					{
//						out.print("\t\t"+ setorAdd + m[i].getName() + "(");
//						ADKDataType dataType = m[i].getFieldType().getDataType();
//						if( !(dataType == ADKDataType.STRING || dataType == ADKDataType.ENUM ) )
//						{
//							out.print(" new "+m[i].getFieldType().getClassType()+"( "+toProperCase(m[i].getName())+" ) );");
//						}
//						else
//							out.println(toProperCase(m[i].getName())+");");
//					}
//					else
//						out.println("\t\t" + setorAdd + m[i].getName()+"("+toProperCase(m[i].getName())+");");
//				}
//				if( objValueType != null ) {
//					out.println("\t\tsetValue( elementValue );");
//				}
				out.println("\t}");
				out.println();
			}
		}
	}



	private void writeSetValueToField( PrintWriter out, ObjectDef o, FieldDef field,  String argumentName )
	{
		if( field.getFieldType().getDataType() == ADKDataType.ENUM )
		{
			out.println( "\t\tthis.set" + field.getName() + "( " + argumentName + " );");
		}
		else if( field.isRepeatable() &&
				( o.getElementType( fDB, this ) == ADKElementType.SIFLIST || o.getElementType( fDB, this ) == ADKElementType.SIFACTIONLIST ) )
		{
			out.println("\t\tthis.safeAddChild( " + field.getElementDefConst( this ) + ", " + argumentName + " );");
		}
		else if( !field.isComplex() ||  !field.isRepeatable() )
		{
			out.println( "\t\tthis.set" + field.getName() + "(" + argumentName + ");");
		}
		else
		{
	    	// TODO: Does this case ever happen ( a mandatory, repeatable field )
	    	out.println("\t\tthis.add" + field.getName() + "( " + argumentName + " );");
		}
	}


	protected void writeElementDefCreationLine( PrintWriter out, String dtdSymbol, String parentDtdSymbol,
			boolean useElementDefAlias,String fieldName, String renderName, int sequenceNumber,
			FieldType type, String localPackage, SIFVersion earliestVersion,
			SIFVersion latestVersion, String flags, String typeConverter )
		{
			out.println("\t\t"+dtdSymbol+" = new " +
			( useElementDefAlias ? "ElementDefAlias" : "ElementDefImpl" ) + "( "+
			( parentDtdSymbol == null ? "null" : parentDtdSymbol ) + ",\""+
			( useElementDefAlias ?
				( fieldName+"\"," + ( renderName == null ? "null" : "\"" + renderName + "\"" ) + ",\"" + type.getClassType()+"\"," ) :
				( fieldName+"\"," + ( renderName == null ? "null" : "\"" + renderName + "\"" ) + "," ) ) +
			sequenceNumber+","+
			(localPackage == null ? "null" : "SIFDTD." + localPackage ) +","+
			( flags.length() == 0 ? "0" : "( " + flags + " )" ) +
			",SIFVersion."+Main.versionStr( earliestVersion )+
			",SIFVersion."+Main.versionStr( latestVersion )+
			(typeConverter != null ? ", " + typeConverter : "") +" );");
		}



	protected String getTypeConverterName( FieldType type ){
		String typeConverter = null;
		switch( type.getDataType() ){
		case STRING:
		case ENUM:
			typeConverter = "SIFTypeConverters.STRING";
			break;
		case BOOLEAN:
			typeConverter = "SIFTypeConverters.BOOLEAN";
			break;
		case DATE:
			typeConverter = "SIFTypeConverters.DATE";
			break;
		case TIME:
			typeConverter = "SIFTypeConverters.TIME";
			break;
		case DATETIME:
			typeConverter = "SIFTypeConverters.DATETIME";
			break;
		case INT:
		case UINT:
			typeConverter = "SIFTypeConverters.INT";
			break;
		case LONG:
		case ULONG:
			typeConverter = "SIFTypeConverters.LONG";
			break;
		case DECIMAL:
			typeConverter = "SIFTypeConverters.DECIMAL";
			break;
		case FLOAT:
			typeConverter = "SIFTypeConverters.FLOAT";
			break;
		case DURATION:
			typeConverter = "SIFTypeConverters.DURATION";

		}
		return typeConverter;
	}


	protected void writeToStringOverride(PrintWriter out, FieldDef def) {
		out.println("\t/**");
		out.println("\t *  Returns the value of the <i>" + def.getName()+ "</i> attribute" );
		out.println("\t */");
		out.println("\tpublic String toString() {");
		out.println("\t\treturn this.get" + def.getName() + "();");
		out.println("\t}");
		out.println();
	}

	@Override
	protected String getSuperClassSeperatorAndName( ObjectDef o )
	{
		if( o.getName().equals( "Grades" ) ){
			// break
			System.out.println( "Break on Annual Items" );
		}

		String superClass = o.getSuperclass();
		if( !( superClass.equals( "SIFElement" ) || superClass.equals( "SIFActionList" ) ) )
		{
			return " extends " + superClass;
		}
		FieldDef childType = o.getRepeatableChildDef();

		if( childType != null  ){
			ObjectDef childObject = fDB.getObject( childType.getFieldType().getClassType() );
			if( childObject == null ){
				throw new RuntimeException("Unable to find object definition {" +
						childType.getFieldType().getClassType() + "} for member: " +
						o.getName() + "." + childType.getName()	);
			}
			FieldDef[] childKeys = childObject.getKey( this );
			if( childKeys != null && childKeys.length == 1 ){
				if( childObject.getField( "SIF_Action" ) != null ){
					return " extends SIFActionList<" + childObject.getName() + ">";
				} else {
					return " extends SIFKeyedList<" + childObject.getName() + ">";
				}
			} else {
				return " extends SIFList<" + childObject.getName() + ">";
			}
		} else {
			FieldDef[] childKeys = o.getKey( this );
			if( childKeys != null && childKeys.length == 1 ){
				return " extends SIFKeyedElement";
			}
			return " extends SIFElement";
		}
	}


	protected void writeAliasDefinition(PrintWriter out, String elementDefName, String flags, String aliasVer, String renderAs, String sequence ) {

		out.println("\t\t"+elementDefName+".defineVersionInfo(" +
			"SIFVersion.SIF" + aliasVer + ", \"" +
			renderAs + "\", " +
			sequence + ", " +
//			( alias.deprecated ? "FD_DEPRECATED":"(byte)0" ) +
		    ( flags.length() == 0 ? "0" : "(" + flags + ")" ) +
			"); // (SIF " + aliasVer + " alias)" );
	}



	protected void writeAbstractMethods( PrintWriter out, ObjectDef o )
	{
//		if( o.isTopic() )
//		{
//		    out.println("\t/**");
//		    out.println("\t *  Gets the name of the Data Object type represented by this class.");
//			out.println("\t */");
//			out.println("\tprotected ElementDef getObjectType() { ");
//			out.println("\t\treturn ADK.DTD()."+o.getDTDSymbol()+";");
//			out.println("\t}");
//	    	out.println();
//		}

		FieldDef[] keys = o.getKey( this );

		if( keys != null && keys.length > 0 && !o.isInfra() )
		{
			//
			// String getKey()
			//
			out.println("\t/**");
			out.println("\t *  Gets the key of this object");
			out.println("\t *  @return The value of the object's Mandatory or Required attribute. If");
			out.println("\t *      an object has more than one such attribute, the key is a period-");
			out.println("\t *      delimited concatenation of the attribute values in sequential order");
			out.println("\t */");
			out.println("\tpublic String getKey() {");
			if( keys.length == 1 ) {
				out.println("\t\treturn getFieldValue( "+keys[0].getElementDefConst( this )+" );");
			}
			else
			{
				out.println("\t\tStringBuilder b = new StringBuilder();");
				for( int i = 0; i < keys.length; i++ ) {
					out.println("\t\tb.append( getFieldValue( "+keys[i].getElementDefConst( this )+" ) );");
					if( i != keys.length-1 )
						out.println("\t\tb.append('.');");
				}
				out.println("\t\treturn b.toString();");
			}
			out.println("\t}");
			out.println();

			//
			// ElementDef[] getKeyFields
			//
			out.println("\t/**");
			out.println("\t *  Gets the metadata fields that make up the key of this object");
			out.println("\t *  @return an array of metadata fields that make up the object's key" );
			out.println("\t */");
			out.println("\tpublic ElementDef[] getKeyFields() {");
			out.print("\t\treturn new ElementDef[] { " );
			for( int i = 0; i < keys.length; i++ ) {
				if( i > 0){
					out.print( ", " );
				}
				out.print( keys[i].getElementDefConst( this ) );
			}
			out.println(" };");
			out.println("\t}");
			out.println();

		}
	}

	protected void writeDTDAbstractMethods( PrintWriter out )
	{
	    // Not implemented
	}

	protected void writeDtdLoad( PrintWriter out )
	{
		out.println("\tpublic void load()");
	}

	protected void writeDTDTableUpdates( PrintWriter out, DB db, String pkg )
	{
		out.println("\tpublic void addElementMappings( Map<String, ElementDef> dtdMap )");
		out.println("\t{");

		HashSet<String> dictionaryItems = new HashSet<String>();
		ObjectDef[] objects = db.getObjects();
		Arrays.sort( objects,
				new Comparator()
				{
					public int compare( Object obj1, Object obj2 ){
						ObjectDef objectDef1 = (ObjectDef)obj1;
						ObjectDef objectDef2 = (ObjectDef)obj2;
						return objectDef1.getName().compareTo( objectDef2.getName() );
					}
				}
		);

		for( ObjectDef obj : objects )
		{
			String tag = obj.getName();
			if( !obj.getLocalPackage().equals(pkg) ){
				continue;
			}

			out.println();
			out.println("\t\t // "+ tag + " aliases and fields" );
			if( obj.getRenderAs() != null ){
				out.println("\t\tdtdMap.put(\""+obj.getRenderAs()+"\","+tag.toUpperCase()+" );");
			}
			out.println("\t\tdtdMap.put(\""+tag+"\","+tag.toUpperCase()+" );");

			// Check for aliases
			Map<String, List<SIFVersion>> aliases = obj.getAliases();
			if( aliases != null ){
				for( String renderAs: aliases.keySet() ){
					List<SIFVersion> versions = aliases.get( renderAs );
					out.print("\t\t// alias for version(s) : " );
					for( SIFVersion version : versions ){
						out.print( version.toString() );
						out.print( "," );
					}
					out.println();
					out.println("\t\tdtdMap.put(\""+renderAs+"\","+tag.toUpperCase()+" );");
				}
			}

			writeDTDTableUpdatesForObject( out, db, obj, dictionaryItems );

			if( obj.isTopic() ){
				out.println("\t\tdtdMap.put(\""+tag+"_SIF_ExtendedElements\","+tag.toUpperCase()+"_SIF_EXTENDEDELEMENTS );");
				out.println("\t\tdtdMap.put(\""+tag+"_SIF_Metadata\","+tag.toUpperCase()+"_SIF_METADATA );");
			}
		}
		out.println("\t}");
	}

	@Override
	protected void writeSingleDTDTableUpdate(PrintWriter out, String tagCombination, String dtdConstName, String addComment) {
		out.println("\t\tdtdMap.put(\""+ tagCombination +"\"," + dtdConstName + " );" + ( addComment != null ? " // " + addComment : "" ) );
	}



	private String getJavaType( FieldType type )
	{
		if( type.isComplex() ){
			return type.getClassType();
		} else if( type.isEnum() ){
			return type.getEnum();
		} else {
			switch( type.getDataType() ){
			case STRING:
			case ENUM:
			case SIFVERSION:
			return "String";
			case BOOLEAN:
				return "Boolean";
			case DATE:
			case DATETIME:
			case TIME:
				return "Calendar";
			case INT:
			case UINT:
				return "Integer";
			case LONG:
			case ULONG:
				return "Long";
			case DECIMAL:
				return "BigDecimal";
			case FLOAT:
				return "Float";
			case DURATION:
				return "javax.xml.datatype.Duration";
			}
			return null;
		}
	}

	protected String getADKSimpleType( ADKDataType dataType ){
		switch( dataType ){
			case BOOLEAN:
				return "SIFBoolean";
			case DATE:
				return "SIFDate";
			case DATETIME:
				return "SIFDateTime";
			case TIME:
				return "SIFTime";
			case DECIMAL:
				return "SIFDecimal";
			case FLOAT:
				return "SIFFloat";
			case DURATION:
				return "SIFDuration";
			case INT:
			case UINT:
				return "SIFInt";
			case LONG:
			case ULONG:
				return "SIFLong";
			case SIFVERSION:
			case STRING:
				return "SIFString";
		}
		return null;
	}

	protected void writeSimpleField( PrintWriter out, ObjectDef o, FieldDef f )
	{
		if( ( o.getFlags() & ObjectDef.FLAG_NO_SIFDTD ) != 0 || ( f.getFlags() & FieldDef.FLAG_NO_SIFDTD ) != 0 )
			return;

		FieldType ft = f.getFieldType();
		ADKDataType adkType = ft.getDataType();

		String javaType = getJavaType( ft );

		out.println("\t/**");
		String aore = null;
		if( ( f.getFlags() & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
			out.println("\t *  Gets the value of the <code>"+f.getName()+"</code> attribute.");
			aore = "attribute";
		} else {
			out.println("\t *  Gets the value of the <code>&lt;"+f.getName()+"&gt;</code> element.");
			aore = "element";
		}

		writeJavadoc(out,o,f,aore);

		out.println("\t *");
		out.println("\t *  @return The <code>"+f.getName()+"</code> "+aore+" of this object.");
		//out.println("\t *  @version "+f.getLatestVersion());
		out.println("\t *  @since "+f.getEarliestVersion());
		out.println("\t */");
		if( f.getFieldType().isEnum() ) {
			out.println("\tpublic String get"+f.getName()+"() { ");
			out.println("\t\treturn getFieldValue( "+f.getElementDefConst( this )+" );");
		}
		else
		{
			out.println("\tpublic " + javaType + " get"+f.getName()+"() { ");

//			if( nullCheck )
//			{
//				out.println("\t\tString __nullchk__ = getFieldValue( SIFDTD."+f.getDTDSymbol()+" );\r\n\t\treturn __nullchk__ == null ? null : " + castT+" __nullchk__ " + castT2 + ";" );
//			}
//			else
				out.println("\t\treturn (" + javaType + ") getSIFSimpleFieldValue( "+f.getElementDefConst( this )+" );");
		}
		out.println("\t}");
		out.println();

		out.println("\t/**");
		if( ( f.getFlags() & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
			out.println("\t *  Sets the value of the <code>"+f.getName()+"</code> attribute.");
		} else {
			if( f.isRepeatable() ) {
				out.println("\t *  Adds a new <code>&lt;"+f.getName()+"&gt;</code> child element.");
			} else {
				out.println("\t *  Sets the value of the <code>&lt;"+f.getName()+"&gt;</code> element.");
			}
		}

		writeJavadoc(out,o,f,aore);

		out.println("\t *");
		if( f.getFieldType().isEnum()  ) {
			out.println("\t *  @param value A constant defined by the <code>"+f.getFieldType().getEnum()+"</code> class");
		} else {
			out.println("\t *  @param value A <code>"+ javaType +"</code> object");
		}
		//out.println("\t *  @version "+f.getLatestVersion());
		out.println("\t *  @since "+f.getEarliestVersion());
		out.println("\t */");
		out.println("\tpublic void set"+f.getName()+"( "+ javaType +" value ) { ");

		if( f.getFieldType().isEnum() ) {
			out.println( "\t\tsetField( "+f.getElementDefConst( this )+ ", value );" );
		}else{
		    out.println( "\t\tsetFieldValue( "+f.getElementDefConst( this )+ ", new " + getADKSimpleType( adkType ) + "( value ), value );" );
		}

		out.println("\t}");
		out.println();

		if( f.getFieldType().isEnum()  )
		{
			out.println("\t/**");
	    	if( ( f.getFlags() & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
		    	out.println("\t *  Sets the value of the <code>"+f.getName()+"</code> attribute as a String.");
			} else {
				out.println("\t *  Sets the value of the <code>&lt;"+f.getName()+"&gt;</code> element as a String.");
			}

		    writeJavadoc(out,o,f,aore);

			out.println("\t *");
			out.println("\t *  @param value The value as a String");
			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.println("\tpublic void set"+f.getName()+"( String value ) { ");
			out.println("\t\tsetField( "+f.getElementDefConst( this )+", value );");
			out.println("\t}");
			out.println();
		}
	}

	protected void writeComplexField( PrintWriter out, ObjectDef o, FieldDef f, ObjectDef type )
	throws GeneratorException
	{
		// NOTE: New to ADK 2.0 is the SIFAction list and SIFList base classes that use generics
		// to provide a useful collection API. This class does not generate as many get or set
		// accessors for these elements because the underlying base class does it for them.
		ADKElementType elementType = o.getElementType( fDB, this );
		boolean parentIsList = false;
		if( ( elementType == ADKElementType.SIFACTIONLIST || elementType == ADKElementType.SIFLIST ) ){
			parentIsList = true;
		}

		if( ( o.getFlags() & ObjectDef.FLAG_NO_SIFDTD ) != 0 || ( f.getFlags() & FieldDef.FLAG_NO_SIFDTD ) != 0 )
			return;

		//  Determine if the 'type' object has a name that differs from the
		//  implementation class name - that is, uses an ElementDefAlias instead
		//  of ElementDef. Country and StatePr are two that fall into this
		//  category. If any do, then the following action will be taken (Country
		//  is used as the example):
		//
		//  1. The method "setCountryOfResidency( Country country )" will not be
		//     written out because that would require the user to properly
		//     construct a Country object with the CountryOfResidency
		//
		//  2. Write out the "setCountryOfResidency( CountryCode code )" method
		//     instead, but the body is written to call the
		//     "Country( SIFDTD.DEMOGRAPHICS_COUNTRYOFRESIDENCY, code )"
		//     constructor instead of the usual "Country( code )" constructor
		//
		boolean useElementDefAlias = !f.getSuperclass().equals(f.getName());
		if( useElementDefAlias ) {
			ObjectDef test = fDB.getObject( f.getSuperclass() );
			useElementDefAlias = ( test != null && test.isShared() );
		}


		FieldDef[] m = type.getMandatoryFields( this );
		String aore = null;

		String setOrAdd = f.isRepeatable() ? "add" : "set";

		if( !parentIsList )
		{
			out.println("\t/**");

			if( ( f.getFlags() & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
				out.println("\t *  Sets the value of the <code>"+f.getName()+"</code> attribute.");
				aore = "attribute";
			} else {
				if( f.isRepeatable() ) {
					out.println("\t *  Adds a new <code>&lt;"+f.getName()+"&gt;</code> child element.");
				} else {
					out.println("\t *  Sets the value of the <code>&lt;"+f.getName()+"&gt;</code> element.");
				}
				aore = "element";
			}

			writeJavadoc(out,o,f,aore);

			out.println("\t *");
			if( f.getFieldType().isEnum()) {
				out.println("\t *  @param value A constant defined by the <code>" + f.getFieldType().getEnum() + "</code> class");
			} else {
				out.println("\t *  @param value A <code>"+type.getName()+"</code> object");
			}
			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.println("\tpublic void " + setOrAdd + f.getName()+"( "+ type.getName() +" value ) { ");
			if( !f.isRepeatable() )
				out.println("\t\tremoveChild( "+f.getElementDefConst( this )+" );");
			out.println("\t\taddChild( " + f.getElementDefConst( this ) + ", value);");
			out.println("\t}");
			out.println();
		}

		if( !f.getFieldType().isEnum() && ( ( f.getFlags() & FieldDef.FLAG_ATTRIBUTE ) == 0 ) && m.length != 0 && !o.isInfra() )
		{
			//  Write a setObjectType() convenience method; this one has all of
			//  the parameters of the parameterized constructor for ObjectType.
			//  Instead of writing "setObjectType( new ObjectType(a,b,c) )",
			//  an agent can simply write "setObjectType( a,b,c )"
			//

			out.println("\t/**");
			if( f.isRepeatable() ) {
				out.println("\t *  Adds a new <code>&lt;"+f.getName()+"&gt;</code> repeatable element.");
			} else {
				out.println("\t *  Sets the value of the <code>&lt;"+f.getName()+"&gt;</code> child element.");
			}
			if( !useElementDefAlias ) {
				out.println("\t *  This form of <code>set"+f.getName()+"</code> is provided as a convenience method");
	    		out.println("\t *  that is functionally equivalent to the version of <code>set"+f.getName()+"</code>");
		    	out.println("\t *  that accepts a single <code>"+type.getName()+"</code> object.");
			}
			out.println("\t *");

			//FieldType objValueType = type.getValueType();
			for( int i = 0; i < m.length; i++ )
				out.println("\t *  @param "+toProperCase(m[i].getName())+" "+m[i].getDesc());
//			if( objValueType != null )
//				out.println("\t *  @param value The value of this "+type.getName());

			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.print("\tpublic void " + setOrAdd + f.getName()+"( ");

			for( int i = 0; i < m.length; i++ )
			{
				String specialCaseType = toArgument(m[i]);
    			if( specialCaseType != null )
	    			out.print(specialCaseType+" "+toProperCase(m[i].getName()));
				else
					out.print( getJavaType( m[i].getFieldType() ) + " " + toProperCase(m[i].getName()));

				if( i != m.length-1 )
					out.print(", ");
			}
//			if( m.length > 0 ){
//				if( objValueType != null ){
//					out.print(", ");
//					out.print( getJavaType( objValueType ) + " value");
//				}
//			}
			out.println(" ) {");

			if( useElementDefAlias )
			{
				if( !f.isRepeatable() )
	    			out.println( "\t\tremoveChild( " + f.getElementDefConst( this ) + ");" );
				out.print( "\t\taddChild( " + f.getElementDefConst( this ) + ", new "+type.getName()+"( " + f.getElementDefConst( this ) + ", " );
			}
			else
			{
				if( !f.isRepeatable() )
	    			out.println( "\t\tremoveChild( " + f.getElementDefConst( this ) + ");" );
				out.print("\t\taddChild( " + f.getElementDefConst( this ) + ", new " + type.getName() + "( ");
			}

			for( int i = 0; i < m.length; i++ ) {
				out.print(toProperCase(m[i].getName()));
				if( i != m.length-1 )
					out.print(", ");
			}
//			if( m.length > 0 ){
//				if( objValueType != null ){
//					out.print(", ");
//					out.print("value");
//				}
//			}

			out.println(" ) );");
			out.println("\t}");
			out.println();
		}


		if( f.isRepeatable() )
		{
			FieldDef[] keys = type.getKey( this );
			if( keys.length != 0 )
			{
				//
				//  removeObjectType( String key )
				//
				out.println("\t/**");
				out.println("\t *  Removes "+an(f.getFieldType().getClassType(),false,true)+" object instance. More than one instance can be defined for this object because it is a repeatable field element.");
				out.println("\t *");
				for( int i = 0; i < keys.length; i++ ) {
					out.println("\t *  @param "+toProperCase(keys[i].getName())+" Identifies the "+type.getName()+" object to remove by its "+keys[i].getName()+" value");
				}
				//out.println("\t *  @version "+f.getLatestVersion());
				out.println("\t *  @since "+f.getEarliestVersion());
				out.println("\t */");
				out.print("\tpublic void remove"+f.getName()+"( ");
				for( int i = 0; i < keys.length; i++ ) {
					out.print( getJavaType( keys[i].getFieldType() ) + " "+toProperCase(keys[i].getName()));
					if( i != keys.length-1 )
						out.print(", ");
				}
				out.println(" ) { ");
				out.print("\t\tremoveChild( "+f.getElementDefConst( this )+", new String[] { ");
				for( int i = 0; i < keys.length; i++ )
				{
					if( keys[i].getFieldType().getDataType() == ADKDataType.INT )
						out.print("String.valueOf(" + toProperCase(keys[i].getName()) + ")" );
					else
						out.print(toProperCase(keys[i].getName())+".toString()");

					if( i != keys.length-1 )
						out.print(",");
				}
				out.println(" } );");
				out.println("\t}");
				out.println();

				//
				//  getObjectType( String key )
				//
				out.println("\t/**");
				out.println("\t *  Gets "+an(f.getFieldType().getClassType(),false,true)+" object instance. More than one instance can be defined for this object because it is a repeatable field element.");
				out.println("\t *");
				for( int i = 0; i < keys.length; i++ ) {
					out.println("\t *  @param "+toProperCase(keys[i].getName())+" Identifies the "+type.getName()+" object to return by its \""+keys[i].getName()+"\" attribute value");
				}
				out.println("\t *  @return "+an(type.getName(),true,true)+" object");
				//out.println("\t *  @version "+f.getLatestVersion());
				out.println("\t *  @since "+f.getEarliestVersion());
				out.println("\t */");
				out.print("\tpublic "+type.getName()+" get"+f.getName()+"( ");
				for( int i = 0; i < keys.length; i++ ) {
					out.print( getJavaType( keys[i].getFieldType() ) + " "+toProperCase(keys[i].getName()));
					if( i != keys.length-1 )
						out.print(", ");
				}
				out.println(" ) { ");
				out.print("\t\treturn ("+type.getName()+")getChild( "+f.getElementDefConst( this )+", new String[] { ");
				for( int i = 0; i < keys.length; i++ )
				{
					if( keys[i].getFieldType().getDataType() == ADKDataType.INT )
						out.print("String.valueOf(" + toProperCase(keys[i].getName()) + ")" );
					else
						out.print(toProperCase(keys[i].getName())+".toString()");

					if( i != keys.length-1 )
						out.print(",");
				}
				out.println(" } );");
				out.println("\t}");
				out.println();
			}

			// GET/SET repeatable elements are now handled by the base class,
			// SIF_Repeatable element list

			String pluralTypeName = plural(f.getName());

			//
			//  ObjectType[] getObjectTypes()
			//
			// e.g.
			// List<? extends SIFElement> v = getChildrenV( SIFDTD.CIRCTX_FINEINFO);
			// FineInfo[] cvt = new FineInfo[v.size()];
			// v.toArray(cvt);
			// return cvt;
			//
			out.println("\t/**");
			out.println("\t *  Gets all <code>"+f.getFieldType().getClassType()+"</code> object instances. More than one instance can be defined for this object because it is a repeatable field element.");
			out.println("\t *");
			out.println("\t *  @return An array of <code>"+type.getName()+"</code> objects");
			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.println("\tpublic "+type.getName()+"[] get"+pluralTypeName+"() {");
			out.println("\t\tList<SIFElement> v = getChildList( " +f.getElementDefConst( this )+");");
			out.println("\t\t"+type.getName()+"[] cvt = new "+type.getName()+"[v.size()];");
			out.println("\t\tv.toArray(cvt);");
			out.println("\t\treturn cvt;");
			out.println("\t}");
			out.println();

			//
			//  setObjectTypes( ObjectType[] )
			//
			out.println("\t/**");
			out.println("\t * Sets an array of <code>"+f.getFieldType().getClassType()+"</code> objects. All existing " );
			out.println("\t * <code>"+f.getFieldType().getClassType()+"</code> instances " );
			out.println("\t * are removed and replaced with this list. Calling this method with the " );
			out.println("\t * parameter value set to null removes all <code>"+plural(f.getFieldType().getClassType())+"</code>.");
			out.println("\t *");
			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.println("\tpublic void set"+pluralTypeName+"( "+type.getName()+"[] "+pluralTypeName.toLowerCase()+" ) {");
			out.println("\t\tsetChildren( "+f.getElementDefConst( this )+", "+pluralTypeName.toLowerCase()+" );");
			out.println("\t}");
			out.println();

		}
		else
		{
			out.println("\t/**");

			if( ( f.getFlags() & FieldDef.FLAG_ATTRIBUTE ) != 0 ) {
				out.println("\t *  Gets the value of the <code>"+f.getName()+"</code> attribute.");
			} else {
				out.println("\t *  Gets the value of the <code>&lt;"+f.getName()+"&gt;</code> element.");
			}

			writeJavadoc(out,o,f,aore);

			out.println("\t *");
			out.println("\t *  @return "+an(type.getName(),true,true)+" object");
			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.println("\tpublic "+type.getName()+" get"+f.getName()+"() { ");
			out.println("\t\treturn ("+type.getName()+")getChild( "+f.getElementDefConst( this )+");");
			out.println("\t}");
			out.println();

			out.println("\t/**");
			out.println("\t *  Removes the <code>"+f.getName()+"</code> child element previously created by calling <code>set" + f.getName() + "</code>");
			out.println("\t *");
			//out.println("\t *  @version "+f.getLatestVersion());
			out.println("\t *  @since "+f.getEarliestVersion());
			out.println("\t */");
			out.println("\tpublic void remove"+f.getName()+"() { ");
			out.println("\t\tremoveChild( "+f.getElementDefConst( this )+" );");
			out.println("\t}");
			out.println();

		}
	}

	protected void writeJavadoc( PrintWriter out, ObjectDef o, FieldDef f, String attrOrElement )
	{
		if( f.getDesc() != null && f.getDesc().length() > 0 )
			out.println("\t* <p> The SIF specification defines the meaning of this "+attrOrElement+" as: \r\n"
					+   "\t* <i>\"" + f.getDesc() + "\"</i><p>");

		Alias[] uniqueTags = f.getUniqueTagAliases();
		if( uniqueTags.length > 1 )
		{
			StringBuilder b = new StringBuilder();
			b.append("\t *  This " + attrOrElement + " is known by more than one tag name depending on the version of SIF in use. The ADK will use the tag names shown below when parsing and rendering " + attrOrElement + "s of this kind.<p>\r\n");
			b.append("\t *  <table width='50%' border='1'><tr><td align='center'><font face='verdana,helvetica' size='-1'>Version</font></td><td align='center'><font face='verdana,helvetica' size='-1'>Tag</font></td></tr>\r\n" );
			int diffs = 0;

			for( Alias alias : uniqueTags )
			{
				if( !alias.getTag().equals( f.getTag() ) )
					diffs++;

				b.append("\t *  <tr>");
				b.append("\t *  <td><font face='verdana,helvetica' size='-1'>SIF" + alias.getVersion() + " (and greater)</font></td><td>\"" + alias.getTag() + "\"</td>");
	    		b.append("\t *  </tr>\r\n");
			}

			b.append("\t *  </table><p>");
			if( diffs > 0 )
			{
				out.println(b.toString());
			}
		}
	}

	protected String toProperCase( String str )
	{
		return toJavaCase( str );
	}

	/**
	 *  Formats a string using Java conventions
	 *  such that all preceding characters of the string up to
	 *  the first capitalized character are all lowercase. For example, "fooBar",
	 *  "FooBar", and "FOOBar" will all be returned as "fooBar".
	 */
	public static String toJavaCase( String str )
	{
		StringBuffer b = new StringBuffer();
		b.append( Character.toLowerCase(str.charAt(0)) );

		char ch = 0;
		boolean flatten = true;

		for( int i = 1; i < str.length(); i++ )
		{
			ch = str.charAt(i);
			if( flatten && ( i+1 < str.length() && !Character.isLowerCase(ch) && Character.isLowerCase(str.charAt(i+1) ))) {
				flatten = false;
				b.append( str.substring(i) );
				break;
			}

			if( ch != '_' )
				b.append( flatten ? Character.toLowerCase(ch) : ch );
		}

		return b.toString();
	}




	protected String symbol(String s) {
		StringBuffer b = new StringBuffer();

		if( Character.isDigit(s.charAt(0)) )
			b.append("_");

		char ch = 0;
		for( int i = 0; i < s.length(); i++ ) {
			ch = s.charAt(i);
			if( ch == '-' )
				b.append("_");
			else
				b.append( ch );
		}

		return b.toString();
	}

	/**
	 * Writes a single ElementDef const line to the specified PrintWriter
	 * @param out
	 * @param o
	 */
	protected void writeElementDefConst(PrintWriter out, String comment, String constName ) {
		out.println("\t/** " + comment + " */");
		out.println("\tpublic static ElementDef " + constName + " = null;");
	}

	@Override
	protected void writeSIFDTDClass(DB database, String dir, DB packageDB) throws IOException {
		PrintWriter out = null;

		String fn = dir+File.separator + "SIFDTD" + getFileExtension();
		System.out.println("- Generating: "+fn);

		try
		{
			File md = new File(dir);
			md.mkdirs();

			out = new PrintWriter( new FileWriter(fn),true );
			writeDTDHeader(out,null,null);
			writeExtras(out,"SIFDTD_comments.txt");
			out.println("public class SIFDTD implements DTD");
			out.println("{");

			for( int i = 0; i < InfraMessages.length; i++ ) {
				out.println("\t/** Identifies the "+InfraMessages[i]+" element */");
				out.println("\tpublic static final byte MSGTYP_"+InfraMessages[i].substring(4).toUpperCase()+" = "+(i+1)+";");
				out.println();
			}

			//  Special case
			out.println("\t// SIF_Message mapping used internally by SIFParser");
			out.println("\tpublic static ElementDef SIF_MESSAGE = new ElementDefImpl(null,\"SIF_Message\",null,0,\"impl\",SIFVersion.SIF11, SIFVersion.LATEST );");
			out.println("\tpublic static ElementDef SIF_MESSAGE_VERSION = new ElementDefImpl( SIFDTD.SIF_MESSAGE,\"Version\",null,1,SIFDTD.infra,(ElementDefImpl.FD_FIELD),SIFVersion.SIF11, SIFVersion.LATEST );");
			out.println();
			out.println("\t// Declare all object and field elements defined by all versions of SIF");
			out.println("\t// supported by the class framework. Definitions are created by version-");
			out.println("\t// dependent subclasses of SIFDTD (e.g. SIF10r1, SIF10r2, etc.)");

			//  Write out the ElementDef statics for each object
			ObjectDef[] o = database.getObjects();
			Arrays.sort( o, new Comparator<ObjectDef>()
				{
					public int compare( ObjectDef o1, ObjectDef o2 ) {
						return o1.getDTDSymbol().compareTo( o2.getDTDSymbol() );
					}

				}
			);

			int dtdItemCount = 2;
			for( int k = 0; k < o.length; k++ )
			{
				if( ( o[k].getFlags() & ObjectDef.FLAG_NO_SIFDTD ) == 0 )
				{
					dtdItemCount++;
				}
			}


			ArrayList<String> publicPackages = new ArrayList<String>();

			out.println();
			out.println("\t// Package names that comprise the SIF Data Objects library");
			for( String packageName : packageDB.getDefinitionFileKeysSet() ) {
				DefinitionFile packageFile = packageDB.getDefinitionFile( packageName );
				out.println( "\t/** The name of the " + packageFile.getFriendlyName() + " package */" );
				out.println("\tpublic static final String "+ packageName + " = \"" + packageName + "\";");

				if( !( packageName.equalsIgnoreCase( "infra" ) ||
						packageName.equalsIgnoreCase( "common") ) ){
					publicPackages.add( packageName );
				}
			}


			out.println();
			out.println("\t// Constants identifying each package in the SIF Data Objects library");
			out.println( "\t/** All SDO libraries */" );
			out.println("\tpublic static final int SDO_ALL = 0xFFFFFFFF;");

			out.println( "\t/** No SDO libraries */" );
			out.println("\tpublic static final int SDO_NONE = 0x00000000;");

			out.println( "\t//  These are always loaded regardless of what the user specifies.");
			out.println( "\t//  They are considered \"built-in\" SDO libraries but under the hood they're ");
			out.println( "\t//  treated just like any other SDO package.");
			out.println("\tprivate static final int SDO_INFRA = 0x40000000;");
			out.println("\tprivate static final int SDO_COMMON = 0x80000000;");


			StringBuilder allPackages = new StringBuilder();
			int packageConst = 1;
			for( String packageName : publicPackages ) {
				DefinitionFile packageFile = packageDB.getDefinitionFile( packageName );
				out.println( "\t/** Identifies the " + packageFile.getFriendlyName() + " package */" );
				out.println("\tpublic static final int SDO_"+ packageName.toUpperCase() + " = 0x00000" + Integer.toHexString( packageConst ) + ";");
				packageConst *=2;
				allPackages.append( packageName );
				allPackages.append( ", " );
			}

			out.println();
			out.println( "\t/** An array of all available package names */" );
			out.println("\tprivate static final String[] sLibNames = new String[] { " + allPackages.substring( 0, allPackages.length() -2 ) + " };");

			String nmspc = packageDB.getDefinitionFile( publicPackages.get( 0 ) ).fNamespace;
			// Strip off the trailing "2.x"
			nmspc = nmspc.substring( 0, nmspc.lastIndexOf( '/' ) );
			out.println();
			out.println( "\t/** The base xmlns for this edition of the ADK without the version */" );
			out.println("\tpublic static final String XMLNS_BASE=\"" + nmspc + "\";" );

			out.println();
			writeDTDAbstractMethods(out);

	    	out.println("\tpublic static HashMap<String,ElementDef> sElementDefs = new HashMap<String,ElementDef>(" + String.valueOf( dtdItemCount ) + ");");
			out.println("\tstatic {");

			//  Special case
	    	out.println("\t\tsElementDefs.put(\"SIF_Message\",SIF_MESSAGE );");
		    out.println("\t\tsElementDefs.put(\"SIF_Message_Version\",SIF_MESSAGE_VERSION );");
			out.println("\t};");

			out.println();
			out.println("\t// Maps infrastructure messages to type codes");
			out.println("\tprotected static HashMap<String,Byte> sTypemap = new HashMap<String,Byte>();");
			out.println("\tstatic {");
			for( int i = 0; i < InfraMessages.length; i++ )
				out.println("\t\tsTypemap.put(\""+InfraMessages[i]+"\", new Byte((byte)"+(i+1)+") );");
			out.println("\t};");
			out.println();

			out.println("\t// Infrastructure messages indexed by type codes");
			out.println("\tprotected static String[] sTagmap = new String[] {");
			for( int i = 0; i < InfraMessages.length; i++ )
				out.println("\t\t\""+InfraMessages[i]+"\",");
			out.println("\t};");
			out.println();

			writeExtras( out, "SIFDTD_template.txt" );

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




}
