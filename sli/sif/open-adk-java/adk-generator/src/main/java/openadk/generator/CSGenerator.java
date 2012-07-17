//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Generates CSharp ADK classes given an array of <code>DB</code> objects for
 * each version of the SIF specification parsed during the parsing phase. The DB
 * objects are merged into one, and from that the various .cs classes are
 * generated. This includes one .cs class per ObjectDef; one .cs class per
 * EnumDef; the SIFDTD.cs class; and one SIFDTD sub-class for each version of
 * SIF represented in the set of parsed files (e.g. SIF10r1.cs, SIF10.cs, etc.)
 * 
 */
public class CSGenerator extends CodeGenerator {
	private String locale;

	/**
	 * Constructor
	 */
	public CSGenerator(String srcDir, String dstDir, String locale) {
		super(srcDir, dstDir);
		this.locale = locale;
	}

	protected String getFileExtension() {
		return ".cs";
	}

	protected void writeSDOLibraryHeader(PrintWriter out, String className) {
		out.println("\tpublic class " + className + " : OpenADK.Library.Impl.SdoLibraryImpl");
		out.println("\t{");
	}

	protected void writeClassHeader(PrintWriter out, ObjectDef o) {
		
		String localPackage = o.getLocalPackage();
		
		writeFileHeader(out);

		out.println("using System;");
		out.println("using System.Text;");
		out.println("using System.Security.Permissions;");
		out.println("using System.Runtime.Serialization;");
		out.println("using OpenADK.Library;");

		if (!localPackage.endsWith("global")) {
			out.println("using OpenADK.Library.Global;");
		}
		
		if ( localPackage.endsWith("infrastructure")) {
			out.println("using OpenADK.Library.Infra;");
		}

		if (!localPackage.endsWith("common") && !localPackage.endsWith("global") && !localPackage.endsWith("infra"))
			out.println("using OpenADK.Library." + locale + ".Common;");
		if (localPackage.endsWith("reporting"))
			out.println("using OpenADK.Library.Infra;");
		if (localPackage.endsWith("etranscripts")) {
			out.println("using OpenADK.Library." + locale + ".Gradebook;");
			out.println("using OpenADK.Library." + locale + ".Student;");
		}
		if ("StudentRecordExchangeData".equals(o.getName())) {
			out.println("using OpenADK.Library." + locale + ".Etranscripts;");
		}
		
		out.println();

		if ( localPackage.endsWith("global") || localPackage.endsWith("infra") ) {
			out.println("namespace OpenADK.Library." + toProperCase(o.getLocalPackage()) + "{");
		} else {
			out.println("namespace OpenADK.Library." + locale + "." + toProperCase(o.getLocalPackage()) + "{");
		}
		out.println();
		writeClassComment(out, o);
		if (o.isDraft()) {
			out.println("[Obsolete(\"This is a draft object and is subject to change or removal prior to its final approval\" , false)]");
		} else if (o.isDeprecated()) {
			out.println("[Obsolete(\"This object has been deprecated\" , false)]");
		}
		out.println("[Serializable]");
		out.println("public class " + o.getName() + getSuperClassSeperatorAndName(o));
		out.println("{");

	}

	protected void writeExtras(PrintWriter out, ObjectDef o) throws IOException {
		String extrasFile = o.getExtrasFile();
		if (extrasFile != null) {
			extrasFile = extrasFile + ".cs";
			out.println("\t\t#region EXTRA METHODS");
			writeExtras(out, extrasFile);
			out.println("\t\t#endregion // EXTRA METHODS");

		}
	}

	protected void writeClassFooter(PrintWriter out) {
		out.println("}}");
	}

	protected void writeDTDHeader(PrintWriter out, DB db, String pkg) {
		writeFileHeader(out);

		out.println("using System;");
		out.println("using System.Collections.Generic;");
		out.println("using System.Reflection;");
		out.println("using System.Runtime.CompilerServices;");
		out.println("using OpenADK.Util;");
		out.println("using OpenADK.Library;");
		out.println("using OpenADK.Library.Impl;");
		
		if (pkg!= null && pkg.endsWith("instr")) {
			out.println("using GlobalDTD = OpenADK.Library.Global.GlobalDTD;");
		}
		
		if (pkg != null && !pkg.endsWith("global") && !pkg.endsWith("infra")) {
			out.println("using CommonDTD = OpenADK.Library." + locale + ".Common.CommonDTD;");
			out.println("using ReportingDTD = OpenADK.Library." + locale + ".Reporting.ReportingDTD;");
			out.println();
			out.println("namespace OpenADK.Library." + locale + "." + toProperCase(pkg));
		} else if ( pkg != null ) {
				out.println("using SifDtd = OpenADK.Library.us.SifDtd;");  //This is core only...
				out.println();
				out.println( "namespace OpenADK.Library." + toProperCase(pkg) );
		} else {
			out.println();
			out.println("namespace OpenADK.Library." + locale);
		}

		out.println("{");

	}

	
	protected void writeEnumHeader(PrintWriter out, EnumDef enumDef) {
		writeFileHeader(out);
		out.println("using System;");
		out.println("using OpenADK.Library;");
		out.println();

		if ( "core".equals(locale) )
			out.println("namespace OpenADK.Library." + toProperCase(enumDef.fPackage));
		else {
			out.println("namespace OpenADK.Library." + locale + "." + toProperCase(enumDef.fPackage));
		}
		out.println("{");
	}

	protected void writeElementDefCreationLine(PrintWriter out, String dtdSymbol, String parentDtdSymbol, boolean useElementDefAlias, String fieldName, String renderName, int sequenceNumber, FieldType classType, String localPackage,
			SIFVersion earliestVersion, SIFVersion latestVersion, String flags, String typeConverter) {
		
		String localeString;		
		
		if ( localPackage == null || localPackage.endsWith("global") || localPackage.endsWith("infra") ) {
			localeString = "null";
		}
		else if ( "core".equals( locale ) ) { //locale-specific referenced from the core... (SIF_Metadata afaik)
			localeString = "Adk.Dtd.GetType().Namespace.Substring(Adk.Dtd.GetType().Namespace.LastIndexOf('.') + 1)";
		} else {
			localeString = "\"" + locale + "\"";
		}
		
		
		out.println("\t\t" + dtdSymbol + " = new " + (useElementDefAlias ? "ElementDefAlias" : "ElementDefImpl") + "( " + (parentDtdSymbol == null ? "null" : parentDtdSymbol) + ", \""
				+ (useElementDefAlias ? (fieldName + "\", " + (renderName == null ? "null" : "\"" + renderName + "\"") + ", \"" + toCSharpType(classType) + "\", ") : (fieldName + "\", " + (renderName == null ? "null" : "\"" + renderName + "\"") + ", "))
				+ sequenceNumber + ", " + "SifDtd." + localPackage.toUpperCase() + ", " + localeString + ", "
				+ (flags.length() == 0 ? "0" : "(" + flags.toString() + ")") + ", SifVersion." + Main.versionStr(earliestVersion) + ", SifVersion." + Main.versionStr(latestVersion) + (typeConverter != null ? ", " + typeConverter : "") + " );");
	}

	protected void writeAliasDefinition(PrintWriter out, String elementDefName, String flags, String aliasVer, String renderAs, String sequence) {

		out.println("\t\t" + elementDefName + ".DefineVersionInfo(" + "SifVersion.SIF" + aliasVer + ", \"" + renderAs + "\", " + sequence + ", " + (flags.length() == 0 ? "0" : "(" + flags + ")") + "); // (Sif " + aliasVer + " alias)");
	}

	protected void writeToStringOverride(PrintWriter out, FieldDef def) {
		out.println("\t\t///<summary>Returns the value of the <c>" + def.getName() + "</c> attribute</summary>");
		out.println("\t\tpublic override string ToString() {");
		out.println("\t\t\treturn this." + def.getName() + ";");
		out.println("\t}");
		out.println();
	}

	protected String getSuperClassSeperatorAndName(ObjectDef o) {

		if (o.fName.equals("AlertMsg")) {
			System.out.println("Time to Break for debugging");
		}

		String superClass = o.getSuperclass();
		if (!(superClass.equals("SIFElement") || superClass.equals("SIFActionList"))) {
			if (superClass.startsWith("SIF")) {
				superClass = "Sif" + superClass.substring(3);
			}
			return " : " + superClass;
		}

		// BEGIN
		FieldDef childType = o.getRepeatableChildDef();

		if (childType != null) {
			ObjectDef childObject = fDB.getObject(childType.getFieldType().getClassType());
			if (childObject == null) {
				throw new RuntimeException("Unable to find object definition {" + childType.getFieldType().getClassType() + "} for member: " + o.getName() + "." + childType.getName());
			}
			FieldDef[] childKeys = childObject.getKey(this);
			if (childKeys != null && childKeys.length == 1) {
				if (childObject.getField("SIF_Action") != null) {
					return " : SifActionList<" + childObject.getName() + ">";
				} else {
					return " : SifKeyedList<" + childObject.getName() + ">";
				}
			} else {
				return " : SifList<" + childObject.getName() + ">";
			}
		} else {
			FieldDef[] childKeys = o.getKey(this);
			if (childKeys != null && childKeys.length == 1) {
				return " : SifKeyedElement";
			}
			return " : SifElement";
		}
	}

	protected void writeEnumClass(PrintWriter out, EnumDef enumDef) {

		out.println("\t[Serializable]");
		out.println("\tpublic class " + enumDef.getName() + " : SifEnum");
		out.println("\t{");

		int vv = 0;
		Hashtable t = enumDef.getValues();
		for (Enumeration e = t.elements(); e.hasMoreElements(); vv++) {
			ValueDef val = (ValueDef) e.nextElement();
			out.println("\t/// <summary>" + commentSafeString(val.fDesc, val.fValue) + " (\"" + val.fValue + "\")</summary>");
			out.println("\tpublic static readonly " + enumDef.getName() + " " + symbol(val.fName) + " = " + "new " + enumDef.getName() + "(\"" + val.fValue + "\");");
			out.println();
		}

		out.println("\t///<summary>Wrap an arbitrary string value in " + an(enumDef.getName(), false, false) + " object.</summary>");
		out.println("\t///<param name=\"wrappedValue\">The element/attribute value.</param>");
		out.println("\t///<remarks>This method does not verify");
		out.println("\t///that the value is valid according to the SIF Specification</remarks>");
		out.println("\tpublic static " + enumDef.getName() + " Wrap( String wrappedValue ) {");
		out.println("\t\treturn new " + enumDef.getName() + "( wrappedValue );");
		out.println("\t}");
		out.println();
		out.println("\tprivate " + enumDef.getName() + "( string enumDefValue ) : base( enumDefValue ) {}");
		out.println("\t}\r\n}");
	}

	protected void writeClassComment(PrintWriter out, ObjectDef o) {
		out.println("/// <summary>" + commentSafeString(o.getDesc(), o.getName()) + "</summary>");
		out.println("/// <remarks>");
		if (o.isDraft())
			out.println("/// <note type=\"note\">This is a Draft Object and is subject to change or removal prior to its final approval. Edustructures includes Draft Objects in the SIFWorks&amp;reg; Agent Developer Kit (ADK) for early access by developers. In the future, this class may be changed or removed for consistency with the latest approved SIF Specification.</note>");
		out.println("///");
		out.println("/// <para>Author: Generated by adkgen</para>");
		out.println("/// <para>Version: " + o.getLatestVersion() + "</para>");
		out.println("/// <para>Since: " + o.getEarliestVersion() + "</para>");
		out.println("/// </remarks>");
	}

	protected void writeDTDClassComment(PrintWriter out, DB db) throws IOException {

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("SIFDTD_Comments_CS.txt"));
			out.println("\r\n// BEGIN FILE... (SIFDTD_Comments_CS.txt)\r\n");
			do {
				String s = in.readLine();
				out.println(s);
				System.out.println(s);
			} while (in.ready());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {/* Do Nothing */
				}
			}
		}
	}

	protected void writeEnumClassComment(PrintWriter out, EnumDef enumDef) {
		out.println("\t///<summary>");
		if (enumDef.getDesc() != null && enumDef.getDesc().length() > 0) {
			out.println("\t///" + enumDef.getDesc());
		} else {
			out.println("\t/// Defines the set of values that can be specified whenever " + an(enumDef.getName(), false, false));
			out.println("\t/// is used as a parameter to a method or constructor. ");
		}
		out.println("\t///</summary>");
		out.println("\t/// <remarks>");
		if (enumDef.getDesc() != null && enumDef.getDesc().length() > 0) {
			out.println("/t///  <para>" + enumDef.getDesc() + "</para>");
		}
		out.println("\t/// Alternatively, the static");
		out.println("\t///  <see cref=\"Wrap\"/> method can be called to encapsulate any string value in");
		out.println("\t///  " + an(enumDef.getName(), false, false) + " object.");
		out.println("\t/// <para>Author: Generated by adkgen</para>");
		out.println("\t/// <para>Version: " + enumDef.getLatestVersion() + "</para>");
		out.println("\t/// <para>Since: " + enumDef.getEarliestVersion() + "</para>");
		out.println("\t/// </remarks>");
	}

	protected void writeClassCtor(PrintWriter out, ObjectDef o) {
		int loopCount = (o.isShared() ? 2 : 1);

		for (int loop = 0; loop < loopCount; loop++) {
			if (loop == 0) {
				out.println("\t/// <summary>");
				out.println("\t/// Creates an instance of " + an(o.getName(), false, false));
				out.println("\t/// </summary>");
				if (o.isTopic()) {
					out.println("\tpublic " + o.getName() + "() : base( Adk.SifVersion, " + o.getPackageQualifiedDTDSymbol() + " ){}");
				} else {
					out.println("\tpublic " + o.getName() + "() : base ( " + o.getPackageQualifiedDTDSymbol() + " ){}");
				}

				if (o.isInfra()) {
					out.println("\t/// <summary>");
					out.println("\t/// Creates an instance of " + an(o.getName(), false, false));
					out.println("\t/// </summary>");
					out.println("\t///  <param name=\"sifVersion\">The version of SIF to render this message in</param>");
					out.println("\t///");
					out.println("\tpublic " + o.getName() + "( SifVersion sifVersion ) : base( sifVersion, " + o.getPackageQualifiedDTDSymbol() + " ){}");
				}

			} else {
				// Write out a second form of constructor that accepts an
				// ElementDef as its first parameter
				out.println("\t/// <summary>");
				out.println("\t///  This constructor is used by the SDO class library. It accepts an alternate");
				out.println("\t///  ElementDef constant.");
				out.println("\t/// </summary>");
				out.println("\tprotected " + o.getName() + "( IElementDef alias ) : base( alias ) {}");
			}
			out.println();

			List<FieldDef> mandatoryFields = new ArrayList<FieldDef>();
			for (FieldDef def : o.getMandatoryFields(this)) {
				mandatoryFields.add(def);
			}

			// FieldDef elementValue = o.getValueDef( this );
			// if( elementValue != null ){
			// mandatoryFields.add( elementValue );
			// }

			if (mandatoryFields.size() == 0 && !o.isInfra()) {
				// If this is a list container, it will have a single,
				// repeatable
				// element as a child. Use this as a constructor
				FieldDef[] all = o.getAllFields();
				if (all.length == 1 && all[0].isRepeatable()) {
					mandatoryFields.add(all[0]);
				}
			}

			if (o.fName.equals("SIF_Version")) {
				System.out.println("Ready to Break");
			}

			if (mandatoryFields.size() > 0 && !o.isInfra()) {
				out.println("\t/// <summary>");
				out.println("\t/// Constructor that accepts values for all mandatory fields");
				out.println("\t/// </summary>");
				for (FieldDef def : mandatoryFields) {
					out.println("\t///<param name=\"" + toArgument(def.getName()) + "\">" + commentSafeString(def.getDesc(), def.getName()) + "</param>");
				}

				out.println("\t///");

				out.print("\t" + (loop == 1 ? "internal" : "public") + " " + o.getName() + "( ");

				if (loop == 1)
					out.print("IElementDef alias, ");

				// Insert all mandatory fields as constructor parameters.
				//
				// Special Case: Any field that itself has one mandatory field
				// will be included such that the field's single mandatory
				// element
				// type is specified instead of the field itself. For example,
				// StatePr has one mandatory attribute, Code, which is of type
				// StatePrCode. Rather than force agents to specify a StatePr
				// object in the constructor, we use StatePrCode instead; in the
				// constructor body we then automatically wrap the StatePrCode
				// in
				// a StatePr child element. Thus,
				//
				// Instead of:
				// public SIFDataObjectClass( StatePr state, ... ) {
				// addChild(state);
				//
				// Use:
				// public SIFDataObjectClass( StatePrCode state, ... ) {
				// addChild( new StatePr(state) );
				//
				for (int i = 0; i < mandatoryFields.size(); i++) {
					FieldDef def = mandatoryFields.get(i);
					// Is the aforementioned Special Case applicable to this
					// field?
					String specialCaseType = toArgument(def);
					if (specialCaseType != null)
						out.print(specialCaseType + " " + toArgument(def.getName()));
					else
						out.print(toCSharpType(def.getFieldType()) + " " + toArgument(def.getName()));

					if (i != mandatoryFields.size() - 1)
						out.print(", ");
				}

				out.print(" )");
				if (o.isTopic()) {
					out.println(" : base( Adk.SifVersion, " + o.getPackageQualifiedDTDSymbol() + " )");
				} else {
					if (loop == 1) {
						out.println(" : base( alias )");
					} else {
						out.println(" : base( " + o.getPackageQualifiedDTDSymbol() + " )");
					}
				}
				out.println("\t{");

				for (FieldDef field : mandatoryFields) {
					// Is the aforementioned Special Case applicable to this
					// field?
					if (toArgument(field) != null) {
						String argName;
						ADKDataType dataType = field.getFieldType().getDataType();
						if (!(dataType == ADKDataType.STRING || dataType == ADKDataType.ENUM)) {
							argName = " new " + toCSharpType(field.getFieldType()) + "( " + toArgument(field.getName()) + " )";
						} else {
							argName = toArgument(field.getName());
						}
						writeSetValueToField(out, o, field, argName);
					} else {
						writeSetValueToField(out, o, field, toArgument(field.getName()));
					}
				}

				out.println("\t}");
				out.println();
			}
		}

		// Write the .Net Serialization Constructor
		out.println("\t/// <summary>");
		out.println("\t/// Constructor used by the .Net Serialization formatter");
		out.println("\t/// </summary>");
		out.println("\t[SecurityPermission( SecurityAction.Demand, SerializationFormatter=true )] ");
		out.println("\tprotected " + o.getName() + "( SerializationInfo info, StreamingContext context ) : base( info, context ) {} ");

	}

	private void writeSetValueToField(PrintWriter out, ObjectDef o, FieldDef field, String argumentName) {
		if (field.getFieldType().getDataType() == ADKDataType.ENUM) {
			out.println("\t\tthis.Set" + field.getName() + "( " + argumentName + " );");
		} else if (field.isRepeatable() && (o.getElementType(fDB, this) == ADKElementType.SIFLIST || o.getElementType(fDB, this) == ADKElementType.SIFACTIONLIST)) {
			out.println("\t\tthis.SafeAddChild( " + field.getElementDefConst(this) + ", " + argumentName + " );");
		} else if (!field.isComplex() || !field.isRepeatable()) {
			out.println("\t\tthis." + field.getName() + " = " + argumentName + ";");
		} else {
			// TODO: Does this case ever happen ( a mandatory, repeatable field
			// )
			out.println("\t\tthis.Add" + field.getName() + "( " + argumentName + " );");
		}
	}

	protected void writeAbstractMethods(PrintWriter out, ObjectDef o) {
		// if( o.isTopic() )
		// {
		// out.println("\t/**");
		// out.println("\t *  Gets the name of the Data Object type represented by this class.");
		// out.println("\t */");
		// out.println("\tprotected ElementDef getObjectType() { ");
		// out.println("\t\treturn ADK.DTD()."+o.getDTDSymbol()+";");
		// out.println("\t}");
		// out.println();
		// }

		FieldDef[] keys = o.getKey(this);

		if (keys != null && keys.length > 0 && !o.isInfra()) {

			// /// <summary>
			// /// Gets the metadata fields that make up the key of this object
			// /// </summary>
			// /// <value>an array of metadata fields that make up the object's
			// key</value>
			// public override IElementDef[] KeyFields {
			// get { return new IElementDef[]
			// {AssessmentDTD.SPECIALCONDITION_CODE}; }
			// }

			//
			// ElementDef[] KeyFields {get;}
			//
			ADKElementType type = o.getElementType(fDB, this);
			String override = (type == ADKElementType.SIFKEYEDELEMENT || type == ADKElementType.SIFDATAOBJECT) ? "override" : "";
			out.println("\t/// <summary>");
			out.println("\t/// Gets the metadata fields that make up the key of this object");
			out.println("\t/// </summary>");
			out.println("\t/// <value>");
			out.println("\t/// an array of metadata fields that make up the object's key");
			out.println("\t/// </value>");
			out.println("\tpublic " + override + " IElementDef[] KeyFields {");
			out.print("\t\tget { return new IElementDef[] { ");
			for (int i = 0; i < keys.length; i++) {
				if (i > 0) {
					out.print(", ");
				}
				out.print(keys[i].getElementDefConst(this));
			}
			out.println(" }; }");
			out.println("\t}");
			out.println();

		}
	}

	protected void writeDTDAbstractMethods(PrintWriter out) {
	}

	protected void writeDTDTableUpdates(PrintWriter out, DB db, String pkg) {
		out.println("\t#region Update SifDtd");

		out.println("\tpublic override void AddElementMappings( IDictionary<String, IElementDef> dictionary )");
		out.println("\t{");

		HashSet<String> dictionaryItems = new HashSet<String>();

		for (ObjectDef obj : db.getObjects()) {
			String tag = obj.getName();
			if (!obj.getLocalPackage().equals(pkg))
				continue;

			// out.println("\t\t// " + tag + " Object" );
			// if( obj.isInfra() || obj.isTopic() ){
			out.println("\t\tdictionary[ \"" + tag + "\" ] = " + tag.toUpperCase() + ";");
			// }
			if (obj.isTopic()) {
				out.println("\t\tdictionary[ \"" + tag + "_SIF_ExtendedElements\" ] = " + tag.toUpperCase() + "_SIF_EXTENDEDELEMENTS ;");
				out.println("\t\tdictionary[ \"" + tag + "_SIF_Metadata\" ] = " + tag.toUpperCase() + "_SIF_METADATA;");
			}

			writeDTDTableUpdatesForObject(out, db, obj, dictionaryItems);
		}

		out.println("\t}");

		out.println("\t#endregion");
	}

	@Override
	protected void writeSingleDTDTableUpdate(PrintWriter out, String tagCombination, String dtdConstName, String addComment) {
		out.println("\t\tdictionary[\"" + tagCombination + "\"] = " + dtdConstName + " ;" + (addComment != null ? "//" + addComment : ""));
	}

	protected void writeSimpleField(PrintWriter out, ObjectDef o, FieldDef f) {

		if ((o.getFlags() & ObjectDef.FLAG_NO_SIFDTD) != 0 || (f.getFlags() & FieldDef.FLAG_NO_SIFDTD) != 0)
			return;
		FieldType ft = f.getFieldType();
		ADKDataType adkType = ft.getDataType();
		String csharpType = toCSharpType(ft);

		out.println("\t/// <summary>");
		String aore = null;
		if ((f.getFlags() & FieldDef.FLAG_ATTRIBUTE) != 0) {
			out.println("\t/// Gets or sets the value of the <c>" + f.getName() + "</c> attribute.");
			aore = "attribute";
		} else if ((f.getFlags() & FieldDef.FLAG_TEXT_VALUE) != 0) {
			out.println("\t/// Gets or sets the value of the <c>&lt;" + o.getName() + "&gt;</c> element.");
			aore = "of the content";
		} else {
			out.println("\t/// Gets or sets the value of the <c>&lt;" + f.getName() + "&gt;</c> element.");
			aore = "element";
		}
		out.println("\t/// </summary>");
		out.println("\t/// <value> The <c>" + f.getName() + "</c> " + aore + " of this object.</value>");
		out.println("\t/// <remarks>");
		writeJavadoc(out, o, f, aore);
		out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
		out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
		out.println("\t/// </remarks>");
		if (f.getFieldType().isEnum()) {
			out.println("\tpublic string " + f.getName() + "\r\n\t{\r\n\t\tget\r\n\t\t{ ");
			out.println("\t\t\treturn GetFieldValue( " + f.getElementDefConst(this) + " );");
			out.println("\t\t}\r\n\t\tset\r\n\t\t{");
			out.println("\t\t\tSetField( " + f.getElementDefConst(this) + ", value );");
			out.println("\t\t}\r\n\t}");
			out.println();
		} else {
			if (((f.getName().equalsIgnoreCase("refid") && o.getSuperclass().equalsIgnoreCase("SIFDataObject")) || (o.getSuperclass().equalsIgnoreCase("SIFTime") && f.getName().equalsIgnoreCase("Zone")))) {
				out.println("\tpublic override " + csharpType + " " + f.getName() + "\r\n\t{\r\n\t\tget\r\n\t\t{");
			} else if (o.getName().equalsIgnoreCase("Time") && f.getName().equalsIgnoreCase("Zone")) {
				out.println("\tpublic virtual " + csharpType + " " + f.getName() + "\r\n\t{\r\n\t\tget\r\n\t\t{");
			} else {
				out.println("\tpublic " + csharpType + " " + f.getName() + "\r\n\t{\r\n\t\tget\r\n\t\t{");
			}

			out.println("\t\t\treturn (" + csharpType + ") GetSifSimpleFieldValue( " + f.getElementDefConst(this) + " ) ;");

			out.println("\t\t}\r\n\t\tset\r\n\t\t{");
			out.println("\t\t\tSetFieldValue( " + f.getElementDefConst(this) + ", new " + getADKSimpleType(adkType) + "( value ), value );");
			out.println("\t\t}\r\n\t}");
			out.println();
		}

		if (f.getFieldType().isEnum()) {
			out.println("\t/// <summary>");
			if ((f.getFlags() & FieldDef.FLAG_ATTRIBUTE) != 0) {
				out.println("\t/// Sets the value of the <c>" + f.getName() + "</c> attribute.");
			} else {
				out.println("\t/// Sets the value of the <c>&lt;" + f.getName() + "&gt;</c> element.");
			}
			out.println("\t/// </summary>");
			out.println("\t/// <param name=\"val\">A " + csharpType + " object</param>");
			out.println("\t/// <remarks>");
			writeJavadoc(out, o, f, aore);
			out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
			out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
			out.println("\t/// </remarks>");
			if (f.getFieldType().isEnum()) {
				out.println("\tpublic void Set" + f.getName() + "( " + csharpType + " val )\r\n\t{");
				out.println("\t\tSetField( " + f.getElementDefConst(this) + ", val );");
				out.println("\t}");
				out.println();
			}
		}
	}

	protected String getADKSimpleType(ADKDataType dataType) {
		switch (dataType) {
		case BOOLEAN:
			return "SifBoolean";
		case DATE:
			return "SifDate";
		case DATETIME:
			return "SifDateTime";
		case TIME:
			return "SifTime";
		case DECIMAL:
			return "SifDecimal";
		case DURATION:
			return "SifDuration";
		case FLOAT:
			return "SifFloat";
		case UINT:
		case INT:
			return "SifInt";
		case ULONG:
		case LONG:
			return "SifLong";
		case SIFVERSION:
		case STRING:
			return "SifString";
		case ENUM:
			return "SifEnum";
		}
		return null;
	}

	protected void writeComplexField(PrintWriter out, ObjectDef o, FieldDef f, ObjectDef type) throws GeneratorException {

		boolean isSifList = false;
		// New to ADK 2.0: The ADK defines specialized subclasses
		// of SIFElement, using Generics, that handle all the tasks
		// of dealing with repeatable child elements. If the object
		// is defined as either an ActionList or List, don't auto-generate
		// any of the remaining code for repeatable elements because they are
		// handled more genericlly by SIFActionList or SIFList
		ADKElementType elementType = o.getElementType(fDB, this);
		if ((elementType == ADKElementType.SIFACTIONLIST || elementType == ADKElementType.SIFLIST)) {
			isSifList = true;
		}

		if ((o.getFlags() & ObjectDef.FLAG_NO_SIFDTD) != 0 || (f.getFlags() & FieldDef.FLAG_NO_SIFDTD) != 0)
			return;

		// Determine if the 'type' object has a name that differs from the
		// implementation class name - that is, uses an ElementDefAlias instead
		// of ElementDef. Country and StatePr are two that fall into this
		// category. If any do, then the following action will be taken (Country
		// is used as the example):
		//
		// 1. The method "setCountryOfResidency( Country country )" will not be
		// written out because that would require the user to properly
		// construct a Country object with the CountryOfResidency
		//
		// 2. Write out the "setCountryOfResidency( CountryCode code )" method
		// instead, but the body is written to call the
		// "Country( SifDtd.DEMOGRAPHICS_COUNTRYOFRESIDENCY, code )"
		// constructor instead of the usual "Country( code )" constructor
		//
		boolean useElementDefAlias = !f.getSuperclass().equals(f.getName());
		if (useElementDefAlias) {
			ObjectDef test = fDB.getObject(f.getSuperclass());
			useElementDefAlias = (test != null && test.isShared());
		}

		if (type.getName().equals("SIFTime")) {
			// The .Net ADK renames this element with a different case
			type.setName("SifTime");
		}

		FieldDef[] m = type.getMandatoryFields(this);
		String aore = null;

		if (!isSifList)// !useElementDefAlias )
		{
			if (f.isRepeatable()) {
				out.println("\t/// <summary>Adds a new <c>&lt;" + f.getName() + "&gt;</c> child element.</summary>");

				aore = "element";

				if (f.getFieldType().isEnum()) {
					out.println("\t/// <param name=\"val\">A constant defined by <see cref=\"" + f.getFieldType().getEnum() + "\"/></param>");
				} else {
					out.println("\t/// <param name=\"val\">A " + type.getName() + " object</param>");
				}
				out.println("\t/// <remarks>");
				writeJavadoc(out, o, f, aore);
				out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
				out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
				out.println("\t/// </remarks>");
				out.println("\tpublic void Add" + f.getName() + "( " + type.getName() + " val ) { ");
				if (!f.isRepeatable())
					out.println("\t\tRemoveChild( " + f.getElementDefConst(this) + " );");
				out.println("\t\tAddChild( " + f.getElementDefConst(this) + ", val );");
				out.println("\t}");
				out.println();
			}
		}

		if (!f.getFieldType().isEnum() && ((f.getFlags() & FieldDef.FLAG_ATTRIBUTE) == 0) && m.length != 0 && !o.isInfra()) {
			// Write a setObjectType() convenience method; this one has all of
			// the parameters of the parameterized constructor for ObjectType.
			// Instead of writing "setObjectType( new ObjectType(a,b,c) )",
			// an agent can simply write "setObjectType( a,b,c )"
			//
			String setOrAdd = "Set";
			if (f.isRepeatable()) {
				setOrAdd = "Add";
			}
			out.println("\t///<summary>" + setOrAdd + "s the value of the <c>&lt;" + f.getName() + "&gt;</c> element.</summary>");

			for (int i = 0; i < m.length; i++)
				out.println("\t/// <param name=\"" + toProperCase(m[i].getName()) + "\">" + commentSafeString(m[i].getDesc(), m[i].getName()) + "</param>");

			out.println("\t///<remarks>");
			if (!useElementDefAlias) {
				out.println("\t/// <para>This form of <c>set" + f.getName() + "</c> is provided as a convenience method");
				out.print("\t/// that is functionally equivalent to the");
				if (setOrAdd.equals("Add")) {
					out.println(" method <c>Add" + f.getName() + "</c></para>");
				} else {
					out.println(" <c>" + f.getName() + "</c></para>");
				}
			}
			out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
			out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
			out.println("\t/// </remarks>");
			out.print("\tpublic void " + setOrAdd + f.getName() + "( ");

			for (int i = 0; i < m.length; i++) {
				String specialCaseType = toArgument(m[i]);
				if (specialCaseType != null)
					out.print(specialCaseType + " " + toProperCase(m[i].getName()));
				else
					out.print(toCSharpType(m[i].getFieldType()) + " " + toProperCase(m[i].getName()));

				if (i != m.length - 1)
					out.print(", ");
			}

			out.println(" ) {");

			if (useElementDefAlias) {
				if (!f.isRepeatable())
					out.println("\t\tRemoveChild( " + f.getElementDefConst(this) + ");");
				out.print("\t\tAddChild( " + f.getElementDefConst(this) + ", new " + type.getName() + "( " + f.getElementDefConst(this) + ", ");
			} else {
				if (!f.isRepeatable())
					out.println("\t\tRemoveChild( " + f.getElementDefConst(this) + ");");
				out.print("\t\tAddChild( " + f.getElementDefConst(this) + ", new " + type.getName() + "( ");
			}

			for (int i = 0; i < m.length; i++) {
				out.print(toProperCase(m[i].getName()));
				if (i != m.length - 1)
					out.print(", ");
			}
			//
			out.println(" ) );");
			out.println("\t}");
			out.println();
		}

		if (isSifList) {
			return;
		}

		if (f.isRepeatable()) {
			FieldDef[] keys = type.getKey(this);
			if (keys.length != 0) {
				//
				// removeObjectType( String key )
				//
				out.println("\t/// <summary>");
				out.println("\t/// Removes " + an(f.getFieldType().getClassType(), false, true) + " object instance. More than one instance can be defined for this object because it is a repeatable field element.");
				out.println("\t/// </summary>");
				for (int i = 0; i < keys.length; i++) {
					out.println("\t/// <param name=\"" + toProperCase(keys[i].getName()) + "\">Identifies the " + type.getName() + " object to remove by its " + keys[i].getName() + " value</param>");
				}
				out.println("\t/// <remarks>");
				out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
				out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
				out.println("\t/// </remarks>");
				out.print("\tpublic void Remove" + f.getName() + "( ");
				for (int i = 0; i < keys.length; i++) {
					out.print(toCSharpType(keys[i].getFieldType()) + " " + toProperCase(keys[i].getName()));
					if (i != keys.length - 1)
						out.print(", ");
				}
				out.println(" ) { ");
				out.print("\t\tRemoveChild( " + f.getElementDefConst(this) + ", new String[] { ");
				for (int i = 0; i < keys.length; i++) {
					out.print(toProperCase(keys[i].getName()) + ".ToString()");
					if (i != keys.length - 1)
						out.print(",");
				}
				out.println(" } );");
				out.println("\t}");
				out.println();

				//
				// getObjectType( String key )
				//
				out.println("\t/// <summary>");
				out.println("\t/// Gets " + an(f.getFieldType().getClassType(), false, true) + " object instance. More than one instance can be defined for this object because it is a repeatable field element.");
				out.println("\t/// </summary>");
				for (int i = 0; i < keys.length; i++) {
					out.println("\t/// <param name=\"" + toProperCase(keys[i].getName()) + "\">Identifies the " + type.getName() + " object to return by its \"" + keys[i].getName() + "\" attribute value</param>");
				}
				out.println("\t/// <returns>" + an(type.getName(), true, false) + " object</returns>");
				out.println("\t/// <remarks>");
				out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
				out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
				out.println("\t/// </remarks>");
				out.print("\tpublic " + type.getName() + " Get" + f.getName() + "( ");
				for (int i = 0; i < keys.length; i++) {
					out.print(toCSharpType(keys[i].getFieldType()) + " " + toProperCase(keys[i].getName()));
					if (i != keys.length - 1)
						out.print(", ");
				}
				out.println(" ) { ");
				out.print("\t\treturn (" + type.getName() + ")GetChild( " + f.getElementDefConst(this) + ", new string[] { ");
				for (int i = 0; i < keys.length; i++) {
					out.print(toProperCase(keys[i].getName()) + ".ToString()");

					if (i != keys.length - 1)
						out.print(",");
				}
				out.println(" } );");
				out.println("\t}");
				out.println();
			}

			// TODO: The Get and Set of repeatable children is now handled by
			// SIFRepeatableElementList
			// It would be nice to do away with these APIS to look better in
			// Microsoft
			// compatible API testing

			String pluralTypeName = plural(f.getName());
			//
			// ObjectType[] GetObjectTypes()
			//

			out.println("\t/// <summary>");
			out.println("\t/// Gets all " + f.getFieldType().getClassType() + " object instances. More than once instance can be defined for this object because it is a repeatable field element.");
			out.println("\t/// </summary>");
			out.println("\t/// <returns>An array of " + type.getName() + " objects</returns>");
			out.println("\t/// <remarks>");
			out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
			out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
			out.println("\t/// </remarks>");
			out.println("\tpublic " + type.getName() + "[] Get" + plural(f.getName()) + "()\r\n\t{");
			out.println("\t\treturn GetChildren<" + type.getName() + ">().ToArray();");
			out.println("\t}");
			out.println();

			//
			// void SetObjectTypes( ObjectType[] )
			//
			out.println("\t/// <summary>");
			out.println("\t/// Sets all " + f.getFieldType().getClassType() + " object instances. All existing ");
			out.println("\t/// <c>" + f.getFieldType().getClassType() + "</c> instances ");
			out.println("\t/// are removed and replaced with this list. Calling this method with the ");
			out.println("\t/// parameter value set to null removes all <c>" + plural(f.getFieldType().getClassType()) + "</c>.");
			out.println("\t/// </summary>");
			out.println("\t/// <remarks>");
			out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
			out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
			out.println("\t/// </remarks>");
			out.println("\tpublic void Set" + pluralTypeName + "( " + type.getName() + "[] items)\r\n\t{");
			out.println("\t\tSetChildren( " + f.getElementDefConst(this) + ", items );");
			out.println("\t}");
			out.println();

		} else {
			out.println("\t/// <summary>");

			if ((f.getFlags() & FieldDef.FLAG_ATTRIBUTE) != 0) {
				out.println("\t/// Gets or sets the value of the <c>" + f.getName() + "</c> attribute.");
			} else {
				out.println("\t/// Gets or sets the value of the <c>&lt;" + f.getName() + "&gt;</c> element.");
			}
			out.println("\t/// </summary>");
			out.println("\t/// <value> " + an(type.getName(), true, false) + " </value>");
			out.println("\t/// <remarks>");
			writeJavadoc(out, o, f, aore);
			out.println("\t/// <para>To remove the <c>" + type.getName() + "</c>, set <c>" + f.getName() + "</c> to <c>null</c></para>");
			out.println("\t/// <para>Version: " + f.getLatestVersion() + "</para>");
			out.println("\t/// <para>Since: " + f.getEarliestVersion() + "</para>");
			out.println("\t/// </remarks>");
			out.println("\tpublic " + type.getName() + " " + f.getName() + "\r\n\t{\r\n\t\tget\r\n\t\t{");
			out.println("\t\t\treturn (" + type.getName() + ")GetChild( " + f.getElementDefConst(this) + ");");
			out.println("\t\t}\r\n\t\tset\r\n\t\t{");
			out.println("\t\t\tRemoveChild( " + f.getElementDefConst(this) + ");");
			out.println("\t\t\tif( value != null)\r\n\t\t\t{");
			out.println("\t\t\t\tAddChild( " + f.getElementDefConst(this) + ", value );\r\n\t\t\t}");
			out.println("\t\t}\r\n\t}");
			out.println();
		}
	}

	protected void writeJavadoc(PrintWriter out, ObjectDef o, FieldDef f, String attrOrElement) {
		if (f.getDesc() != null && f.getDesc().length() > 0) {
			out.println("\t/// <para>The SIF specification defines the meaning of this " + attrOrElement + " as: \"" + commentSafeString(f.getDesc(), f.getName()) + "\"</para>");
		}

		Alias[] uniqueTags = f.getUniqueTagAliases();
		if (uniqueTags.length > 1) {
			StringBuilder b = new StringBuilder();
			b.append("\t/// <para>This " + attrOrElement + " is known by more than one tag name depending on the version of SIF in use. \r\n");
			b.append("\t/// The ADK will use the tag names shown below when parsing and rendering " + attrOrElement + "s of this kind.</para>\r\n");
			b.append("\t/// <list type=\"table\"><listheader><term>Version</term><description>Tag</description></listheader>;\r\n");
			int diffs = 0;

			for (Alias alias : uniqueTags) {
				if (!alias.getTag().equals(f.getTag())) {
					diffs++;
					b.append("\t/// <item><term>" + alias.getVersion() + " (and greater)</term><description>&lt;" + alias.getTag() + "&gt;</description></item>\r\n");
				}
			}
			b.append("\t/// </list>");
			if (diffs > 0) {
				out.println(b.toString());
			}
		}
	}

	/**
	 * Formats a string such that the first letter is uppercase and preceding
	 * characters of the string up to the first capitalized character are all
	 * lowercase. For example, "fooBar", "FooBar", and "FOOBar" will all be
	 * returned as "FooBar".
	 */
	protected String toProperCase(String str) {
		StringBuffer b = new StringBuffer();
		b.append(Character.toUpperCase(str.charAt(0)));

		char ch = 0;
		boolean flatten = true;

		for (int i = 1; i < str.length(); i++) {
			ch = str.charAt(i);
			if (flatten && (i + 1 < str.length() && !Character.isLowerCase(ch) && Character.isLowerCase(str.charAt(i + 1)))) {
				flatten = false;
				b.append(str.substring(i));
				break;
			}

			if (ch != '_')
				b.append(flatten ? Character.toLowerCase(ch) : ch);
		}

		return b.toString();
	}

	private String toCSharpType(FieldType type) {
		if (type.isComplex()) {
			return type.getClassType();
		} else if (type.isEnum()) {
			return type.getEnum();
		} else {
			switch (type.getDataType()) {
			case STRING:
			case ENUM:
			case SIFVERSION: {
				return "string";
			}
			case BOOLEAN: {
				return "bool?";
			}
			case DATE:
			case DATETIME:
			case TIME: {
				return "DateTime?";
			}
			case UINT:
			case INT: {
				return "int?";
			}
			case ULONG: {
				return "ulong?";
			}
			case LONG: {
				return "long?";
			}
			case DECIMAL: {
				return "decimal?";
			}
			case FLOAT: {
				return "float?";
			}
			case DURATION: {
				return "TimeSpan?";
			}
			}
			return null;
		}
	}

	/**
	 * Formats a string such that the first letter is lowercase and preceding
	 * characters of the string up to the first capitalized character are all
	 * lowercase. For example, "fooBar", "FooBar", and "FOOBar" will all be
	 * returned as "fooBar".
	 */
	protected String toArgument(String str) {
		if (str.equalsIgnoreCase("override")) {
			return "overrideValue";
		}

		StringBuffer b = new StringBuffer();
		b.append(Character.toLowerCase(str.charAt(0)));

		char ch = 0;
		boolean flatten = true;

		for (int i = 1; i < str.length(); i++) {
			ch = str.charAt(i);
			if (flatten && (i + 1 < str.length() && !Character.isLowerCase(ch) && Character.isLowerCase(str.charAt(i + 1)))) {
				flatten = false;
				b.append(str.substring(i));
				break;
			}

			if (ch != '_')
				b.append(flatten ? Character.toLowerCase(ch) : ch);
		}

		return b.toString();
	}

	protected String commentSafeString(String comment, String objectName) {
		if (comment == null || comment.length() == 0) {
			return an(objectName, true, false);
		} else {
			String returnVal = replaceAll(comment, '\t', "");
			if (returnVal.endsWith("\n")) {
				returnVal = returnVal.substring(0, returnVal.length() - 1);
			}
			returnVal = replaceAll(returnVal, '&', "&amp;");
			returnVal = replaceAll(returnVal, '\n', "\r\n\t/// ");
			return returnVal;
		}
	}

	/**
	 * Replace all occurrences of the specified character with a string.
	 * 
	 * @param src
	 *            The source string
	 * @param ch
	 *            The character to replace
	 * @param replaceWith
	 *            The replacement string
	 * @return The resulting string
	 * @deprecated Java 5.0 has a String.replaceAll method, but 1.4 doesn't. We
	 *             aren't yet ready to move to 5.0 (it isn't supported on all
	 *             platforms we rely on), so this is a temporary method to use
	 *             until the 5.0 switch is made.
	 */
	private String replaceAll(String src, char ch, String replaceWith) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c == ch) {
				b.append(replaceWith);
			} else {
				b.append(c);
			}
		}

		return b.toString();
	}

	protected String symbol(String s) {
		StringBuffer b = new StringBuffer();

		if (Character.isDigit(s.charAt(0)))
			b.append("C");

		char ch = 0;
		for (int i = 0; i < s.length(); i++) {
			ch = s.charAt(i);
			if (ch == '-')
				b.append("_");
			else
				b.append((char) ch);
		}

		return b.toString();
	}

	protected String an(String str, boolean upperCase, boolean code) {
		StringBuffer b = new StringBuffer();
		char ch = Character.toLowerCase(str.charAt(0));
		if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == 'h')
			b.append(upperCase ? "An" : "an");
		else
			b.append(upperCase ? "A" : "a");

		b.append(" ");
		if (code)
			b.append("<see cref=\"");
		b.append(str);
		if (code)
			b.append("\"/>");

		return b.toString();
	}

	protected String getTypeConverterName(FieldType type) {
		String typeConverter = null;
		switch (type.getDataType()) {
		case STRING:
		case ENUM:
			typeConverter = "SifTypeConverters.STRING";
			break;
		case BOOLEAN:
			typeConverter = "SifTypeConverters.BOOLEAN";
			break;
		case DATE:
			typeConverter = "SifTypeConverters.DATE";
			break;
		case TIME:
			typeConverter = "SifTypeConverters.TIME";
			break;
		case DATETIME:
			typeConverter = "SifTypeConverters.DATETIME";
			break;
		case UINT:
		case INT:
			typeConverter = "SifTypeConverters.INT";
			break;
		case ULONG:
		case LONG:
			typeConverter = "SifTypeConverters.LONG";
			break;
		case DECIMAL:
			typeConverter = "SifTypeConverters.DECIMAL";
			break;
		case FLOAT:
			typeConverter = "SifTypeConverters.FLOAT";
			break;
		case DURATION:
			typeConverter = "SifTypeConverters.DURATION";
		}
		return typeConverter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adkgen.CodeGenerator#writeElementDefConst(java.io.PrintWriter,
	 * java.lang.String, java.lang.String)
	 */
	protected void writeElementDefConst(PrintWriter out, String comment, String defConstName) {
		out.println("\t/** " + comment + " */");
		out.println("\tpublic static IElementDef " + defConstName + " = null;");
	}

	@Override
	protected void writeSIFDTDClass(DB database, String dir, DB packageDB) throws IOException {
		String fn = dir + File.separator + "SifDtd.cs";
		System.out.println("- Generating: " + fn);

		PrintWriter out = null;

		try {
			File md = new File(dir);
			md.mkdirs();

			out = new PrintWriter(new FileWriter(fn), true);
			writeDTDHeader(out, null, null);
			out.println("using System.Text;");
			// out.println("using Edustructures.SifWorks." + locale +
			// ".Datamodel;");
			writeDTDClassComment(out, null);
			out.println("public sealed partial class SifDtd : DTDInternals, ISifDtd");
			out.println("{");

			// TODO: Have this write out the SifMessageType enumDef
			//
			// for( int i = 0; i < Infra10r1.length; i++ ) {
			// out.println("\t/** Identifies the "+Infra10r1[i]+" element */");
			// out.println("\tpublic static final byte MSGTYP_"+Infra10r1[i].substring(4).toUpperCase()+" = "+(i+1)+";");
			// out.println();
			// }

			// Special case
			out.println("\t// SIF_Message mapping used internally by SIFParser");
			out.println("\tpublic static IElementDef SIF_MESSAGE = new ElementDefImpl(null,\"SIF_Message\",null,0,\"Impl\",SifVersion.SIF11, SifVersion.LATEST );");
			out.println("\tpublic static IElementDef SIF_MESSAGE_VERSION = new ElementDefImpl( SifDtd.SIF_MESSAGE,\"Version\",null,1,SifDtd.INFRA,(byte)(ElementDefImpl.FD_FIELD),SifVersion.SIF11, SifVersion.LATEST );");
			out.println();
			out.println("\t// Declare all object and field elements defined by all versions of SIF");
			out.println("\t// supported by the class framework.");

			// Write out the ElementDef statics for each object
			ObjectDef[] o = database.getObjects();
			Arrays.sort(o, new Comparator<ObjectDef>() {
				public int compare(ObjectDef o1, ObjectDef o2) {
					return o1.getDTDSymbol().compareTo(o2.getDTDSymbol());
				}

			});

			int dtdItemCount = 2;
			for (int k = 0; k < o.length; k++) {
				if ((o[k].getFlags() & ObjectDef.FLAG_NO_SIFDTD) == 0) {
					dtdItemCount++;
				}
			}

			ArrayList<String> publicPackages = new ArrayList<String>();

			out.println();
			out.println("\t// Package names that comprise the SIF Data Objects library");
			for (String packageName : packageDB.getDefinitionFileKeysSet()) {
				DefinitionFile packageFile = packageDB.getDefinitionFile(packageName);
				out.println("\t/** The name of the " + packageFile.getFriendlyName() + " package */");
				out.println("\tpublic const string " + packageName.toUpperCase() + " = \"" + toProperCase(packageName) + "\";");

				if (!(packageName.equalsIgnoreCase("infra") || packageName.equalsIgnoreCase("common") || packageName.equalsIgnoreCase("global"))) {
					publicPackages.add(packageName);
				}
			}
			
			out.println();
			out.println("\t// The name of the data model variant this class is defined in");
			out.println("\tpublic string Variant { ");
			out.println( "\t\tget{" );
			out.println( "\t\t\treturn \"" + Main.self.fLocale.toLowerCase() + "\";" );
			out.println( "\t\t}" );
			out.println( "\t}");
			String sdofn = dir + File.separator + "SdoLibraryType.cs";
			System.out.println("- Generating: " + fn);

			
			
			out.println( "\tpublic List<string> LoadedLibraryNames" );
			out.println( "\t{" );
			out.println( "\tget" );
			out.println( "\t\t\t{" );
			out.println( "\t\t\tList<string> libNames = new List<string>();" );
			out.println();
			out.println( "\t\t\tint[] availableValues = (int[])Enum.GetValues(typeof(SdoLibraryType));" );
			out.println();
			out.println( "\t\t\tforeach (int a in availableValues)" );
			out.println( "\t\t\t{" );
			out.println( "\t\t\t\tif (a != (int)SdoLibraryType.All && (fLoaded & a) != 0)" );
			out.println( "\t\t\t\t{" );
			out.println( "\t\t\t\t\tlibNames.Add(GetLibraryName(a));" );
			out.println( "\t\t\t\t}" );
			out.println( "\t\t\t}" );
			out.println();
			out.println( "\t\t\treturn libNames;" );
			out.println( "\t\t}" );
		    out.println( "\t}" );			
			
			PrintWriter slt = null;

			try {

				slt = new PrintWriter(new FileWriter(sdofn), true);
				writeFileHeader(slt);
				slt.println("using System;");
				slt.println();
				slt.println("namespace OpenADK.Library." + locale + "\r\n{");
				slt.println("\t/// <summary>");
				slt.println("\t/// Values identifying each package in the SIF Data Objects library");
				slt.println("\t/// </summary>");
				slt.println("\t[Flags]");
				slt.println("\tpublic enum SdoLibraryType : int\r\n{");

				slt.println("\t/// <summary> All SDO libraries </summary>");
				slt.println("\tAll = -1, // 0xFFFFFFFF");
				slt.println();

				slt.println("\t/// <summary> No SDO libraries </summary>");
				slt.println("\tNone = 0x00000000,");
				slt.println();

				slt.println("\t//  These are always loaded regardless of what the user specifies.");
				slt.println("\t//  They are considered \"built-in\" SDO libraries but under the hood they're ");
				slt.println("\t//  treated just like any other SDO package.");

				slt.println("\t/// <summary>Identifies the Infrastructure Sdo library</summary>");
				slt.println("\tGlobal = 0x40000000,");
				slt.println();

				slt.println("\t/// <summary>Identifies the Infrastructure Sdo library</summary>");
				slt.println("\tInfra = 0x20000000,");
				slt.println();

				slt.println("\t/// <summary>Identifies the Infrastructure Sdo library</summary>");
				slt.println("\tCommon = 0x10000000,");
				slt.println();

				int packageConst = 1;
				for (String packageName : publicPackages) {
					
					DefinitionFile packageFile = packageDB.getDefinitionFile(packageName);

					slt.println("\t/// <summary>Identifies the " + packageFile.getFriendlyName() + " Sdo library</summary>");
					
					slt.println("\t" + toProperCase(packageName) + " = 0x00000" + Integer.toHexString(packageConst) + ",");			
						
					slt.println();
					packageConst *= 2;
				}

				writeClassFooter(slt);

			} finally {
				if (slt != null) {
					try {
						slt.close();
					} catch (Exception e) {
					}
				}
			}

			if ( publicPackages.size() > 0 ) {
				
				String nmspc = packageDB.getDefinitionFile(publicPackages.get(0)).fNamespace;
				// Strip off the trailing "2.x"
				nmspc = nmspc.substring(0, nmspc.lastIndexOf('/'));
				out.println();
				out.println("\t/** The base xmlns for this edition of the ADK without the version */");
				out.println("\tpublic string XMLNS_BASE {" );
				out.println( "\t\tget{");
				out.println( "\t\t\treturn \"" + nmspc + "\";" );
				out.println( "\t\t}" );
				out.println( "\t}" );
	
				out.println();
				writeDTDAbstractMethods(out);
	
				out.println("\tinternal static IDictionary<String,IElementDef> sElementDefs = new Dictionary<String,IElementDef>( " + dtdItemCount + " );");
				out.println("\tstatic SifDtd()\r\n\t{");
	
				// Special case
				out.println("\t\tsElementDefs[ \"SIF_Message\" ] = SIF_MESSAGE;");
				out.println("\t\tsElementDefs[ \"SIF_Message_Version\" ] = SIF_MESSAGE_VERSION;");
				out.println("\t}\r\n");
	
				out.println();
	
				writeExtras(out, "SIFDTD_Template_CS.txt");
	
				out.println("}}");
			} else {
				//Will not compile without something here, even if it's worthless
				out.println(); 
				out.println("\t/** The base xmlns for this edition of the ADK without the version */");
				out.println("\tpublic string XMLNS_BASE {" );
				out.println( "\t\tget{");
				out.println( "\t\t\treturn \"\";" );
				out.println( "\t\t}" );
				out.println( "\t}" );
	
				out.println();				
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}

	}

	@Override
	protected void writeDtdLoad(PrintWriter out) {
		out.println("\tpublic override void Load()");

	}

}
