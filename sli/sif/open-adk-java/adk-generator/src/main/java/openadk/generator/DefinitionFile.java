//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *  Parses a definition file.
 *
 */
public class DefinitionFile implements ErrorHandler
{
	/**
	 *  The local package for all classes generated from this file, defined by
	 *  the #package directive. This name is used to construct a fully-qualified
	 *  package name by concatenating the package prefix specified on the
	 *  adkgen command-line with the version of SIF associated with the
	 *  definition file. The result is a package name "{prefix}.{version}.{local-package}".
	 *  For example, "com.edustructures.sifworks.sif10r1.student"
	 */
	protected String fPackage;

	private String fLocalPackage;

	/**
	 *  The SIF version to which all definitions in this file apply
	 */
	protected SIFVersion fVersion;

	protected String fNamespace;

	/** The file */
	protected File fSrc;
	protected String fSrcDir;

	/** The DOM Document */
	protected Document fDoc;

	/** The ObjectDef we're currently processing */
	protected ObjectDef fObjectDef;

	/** The FieldDef we're currently processing */
	protected FieldDef fFieldDef;

	/** The EnumDef we're currently processing */
	protected EnumDef fEnumDef;

	/** The DB object to which all definitions are written */
	protected DB fDB;

	/** Used to assign sequential IDs to <object>'s */
	protected int sID = 1;

	private String fFriendlyName;


	/**
	 *  Constructor
	 */
	public DefinitionFile( File f )
	{
		this( f.getAbsolutePath() );
	}

	/**
	 *  Constructor
	 */
    public DefinitionFile( String f )
	{
		fSrc = new File( f );
		fSrcDir = fSrc.getAbsolutePath();
		int i = fSrcDir.lastIndexOf( File.separator );
		if( i != -1 ) {
			fSrcDir = fSrcDir.substring(0,i);
		}
	}

	/**
	 *  Parses this definition file
	 */
	public void parse()
		throws SAXException,IOException,ParseException
	{
		System.out.println("- "+fSrc);

		org.apache.xerces.parsers.DOMParser parser =
			new org.apache.xerces.parsers.DOMParser();
		parser.setErrorHandler(this);
        parser.parse(fSrc.getAbsolutePath());
		Document doc = parser.getDocument();
		traverse(doc);
    }

	/**
	 *  Traverse a node
	 */
    public void traverse( Node node )
		throws ParseException
	{
        // is there anything to do?
        if( node == null )
            return;

		//Node parent = node.getParentNode();
        int type = node.getNodeType();
        switch(type)
		{
	        case Node.DOCUMENT_NODE: {
                traverse(((Document)node).getDocumentElement());
                break;
            }

	        case Node.ELEMENT_NODE: {
				String name = node.getNodeName();
				if( name.startsWith("adk") )
					onRoot(node);
				else
				if( name.startsWith("obj") )
					onObject(node,false);
				else
				if( name.startsWith("enu") )
					onEnumeration(node);
				else
				if( name.startsWith("att") )
					onAttribute(node);
				else
				if( name.startsWith("ele") )
					onElement(node);
				else
				if( name.startsWith("des") )
					onDesc(node);
				else
				if( name.startsWith("val") )
					onValue(node);
				else
				if( name.startsWith("inf") )
					onObject(node,true);
				else
					throw new ParseException("<"+name+"> is not a recognized element");

                NodeList children = node.getChildNodes();
                if (children != null) {
                    int len = children.getLength();
                    for (int i = 0; i < len; i++) {
                        traverse(children.item(i));
                    }
                }

                break;
			}
        }
	}

	protected void onRoot( Node node )
		throws ParseException
	{
		fLocalPackage = (getAttr(node,"package"));
		String ver = getAttr(node,"version");
		fNamespace = getAttr(node,"namespace");
		fFriendlyName = getAttr(node,"name");
		
		if( getLocalPackage() == null || ver == null || fNamespace == null )
			throw new ParseException("<adk> must specify the package=, version=, and namespace= attributes");
	
		//TODO: I am ugly
		String language = Main.self.fLanguage;
		if ( "java".equals(language) ) {
			if ( "global".equalsIgnoreCase(fLocalPackage)) {
				fFriendlyName = "Common";
				fLocalPackage = "common";
			} else if ( "infrastructure".equalsIgnoreCase(fLocalPackage)) {
				fFriendlyName = "Infra";
				fLocalPackage = "infra";
			}
		}
		
		fVersion = SIFVersion.parse(ver);
		fPackage = Main.getPackage()+"."+getLocalPackage();

		System.out.println("    Package="+fPackage);
		System.out.println("    Version="+ver+" ("+fVersion.toString()+")");
		System.out.println("    Namespace="+fNamespace);

		fDB = Main.getDB(fVersion,fNamespace);
		fDB.defineDefinitionFile( this );
	}

	protected void onDesc( Node node )
		throws ParseException
	{
		String desc = getText(node);
		if( fFieldDef != null )
			fFieldDef.setDesc(desc);
		else
		if( fObjectDef != null )
			fObjectDef.setDesc(desc);
	}

	protected void onObject( Node node, boolean infra )
		throws ParseException
	{
		String name = getAttr(node,"name");
		if( name == null ){
			throw new ParseException("<object> or <infra> must specify a name= attribute");
		}

		if( infra ) {
			System.out.println("    Infra: "+name);
		} else {
			System.out.println("    Object: "+name);
		}
		
//		if( name.equals( "ProgramFundingSource" ) ){
//			System.out.println( "Ready to break" );
//		}
	
	    fObjectDef = fDB.defineObject(sID++,name,getLocalPackage());
		if( infra ){
			fObjectDef.setInfra();
		}

		fFieldDef = null;
		fEnumDef = null;
		String topic = getAttr(node,"topic");
		fObjectDef.setTopic( topic != null && ( topic.equalsIgnoreCase("yes") || topic.equalsIgnoreCase("true") ) );
		fObjectDef.setRenderAs(getAttr(node,"renderAs"));

		fObjectDef.setLatestVersion( fVersion );
		fObjectDef.setEarliestVersion( fVersion );

		if( getBooleanAttr(node,"empty",false) ){
			fObjectDef.setFlags( fObjectDef.getFlags() | ObjectDef.FLAG_EMPTYOBJECT );
		}
		if( !getBooleanAttr(node,"sifdtd",true) ){
			fObjectDef.setFlags( fObjectDef.getFlags() | AbstractDef.FLAG_NO_SIFDTD );
		}
		if( getBooleanAttr(node,"shared",false) ){
			fObjectDef.setShared( true );
		}
		
		if( getBooleanAttr( node, "collapsed", false ) ){
			fObjectDef.setFlags( fObjectDef.getFlags() | ObjectDef.FLAG_OBJECTCOLLAPSED );
		}
		
		String override = getAttr(node,"sequenceOverride");
		if( override != null ) {
			try {
				fObjectDef.setSequenceOverride( Integer.parseInt(override) );
			} catch( NumberFormatException nfe ) {
				throw new ParseException( "Invalid sequenceOverride value: " + override );
			}
		}

		String supercls = getAttr(node,"superclass");
		if( supercls == null ) {
			if( infra )
				supercls = "SIFMessagePayload";
			else
			if( fObjectDef.isTopic() )
				supercls = "SIFDataObject";
			else
				supercls = "SIFElement";
		}

		String draft = getAttr(node,"draft");
		if( draft != null && draft.equalsIgnoreCase("true") ){
			fObjectDef.setDraft();
		}

		
		String typ = getAttr(node,"type");
		if( typ != null ){
			supercls = typ;
		}
		fObjectDef.setSuperclass(supercls);
		
		String dataType = getAttr( node, "dataType" );
		if( dataType != null ){
			fObjectDef.setDataType( dataType );
		}
		
		String enumType = getAttr( node, "enum" );
		if( enumType != null ){
			fObjectDef.setEnumType( enumType );
		}
		
		String extras = getAttr( node,"extras" );
		if( extras != null && fObjectDef.getExtrasFile() == null ) {
			fObjectDef.setExtrasFile( fSrcDir + File.separator + extras );
		}
	}

	protected void onEnumeration( Node node )
		throws ParseException
	{
		String name = getAttr(node,"name");
		fEnumDef = new EnumDef(name,getLocalPackage());
		System.out.println("    Enumeration: "+name);

		fEnumDef.setLatestVersion( fVersion );
		fEnumDef.setEarliestVersion( fVersion );
		fDB.defineEnum(name,fEnumDef);
	}

	protected void onValue( Node node )
		throws ParseException
	{
		if( fEnumDef != null ) {
			String value = getAttr(node,"value");
			String desc = getAttr(node,"desc");
			String name = getAttr(node,"name");
			if( name == null )
				name = value;
			fEnumDef.defineValue(name,value,desc);
		}
	}

	protected void onAttribute( Node node )
		throws ParseException
	{
		fEnumDef = null;

		String name = getAttr(node,"name");
		String type = getAttr(node,"type");
		String flags = getAttr(node,"flags");
		String enumVal = getAttr(node,"enum");
		
		StringBuffer out = new StringBuffer("    - ");
		out.append(name);
		if( type != null ) {
			out.append("{");
			out.append(type);
			out.append("}");
		}

		fFieldDef = fObjectDef.defineAttr(name,type);
		if( flags != null )
			fFieldDef.setFlags(flags);
		if( enumVal != null ){
			fFieldDef.setEnum(enumVal);
		}
		fFieldDef.setRenderAs( getAttr(node,"renderAs") );
		
		String surrogate = getAttr(node,"surrogate");
		if( surrogate != null && !surrogate.endsWith( "}" ) )
		{
			// This should never happen, so throw an exception
			throw new ParseException( "Surrogate definition '" + surrogate + 
					"' on " + name + " is invalid. Surrogate syntax requires constructor information enclosed by braces {}");
		}
		
		fFieldDef.setSurrogate( surrogate );

		if( ( fFieldDef.getFlags() & AbstractDef.FLAG_MANDATORY ) != 0 && !getBooleanAttr(node,"key",true) )
		{
			//  By default all attributes with a "R" flag are used to generate
			//  the object's key. However, some attributes have an "R" flag but
			//  are not part of the key. When the key="false" attribute is
			//  specified, set the FLAG_NOT_A_KEY flag.

			fFieldDef.setFlags( fFieldDef.getFlags() | FieldDef.FLAG_NOT_A_KEY );
		}

		fFieldDef.setLatestVersion( fVersion );
		fFieldDef.setEarliestVersion( fVersion );

		System.out.println(out.toString());
	}

	protected void onElement( Node node )
		throws ParseException
	{
		fEnumDef = null;

		String name = getAttr(node,"name");
		String type = getAttr(node,"type");
		String flags = getAttr(node,"flags");
		String enumVal = getAttr(node,"enum");

		StringBuffer out = new StringBuffer("    > ");
		out.append(name);
		if( type != null ) {
			out.append("{");
			out.append(type);
			out.append("}");
		}

		fFieldDef = fObjectDef.defineElement(name,type);
		if( flags != null )
			fFieldDef.setFlags(flags);
		if( enumVal != null ){
			fFieldDef.setEnum(enumVal);
		}
		fFieldDef.setRenderAs( getAttr(node,"renderAs") );
		
		String surrogate = getAttr(node,"surrogate");
		if( surrogate != null && !surrogate.endsWith( "}" ) )
		{
			// This should never happen, so throw an exception
			throw new ParseException( "Surrogate definition '" + surrogate + 
					"' on " + name + " is invalid. Surrogate syntax requires constructor information enclosed by braces {}");
		}
		
		fFieldDef.setSurrogate( surrogate );

		String override = getAttr(node,"sequenceOverride");
		if( override != null ) {
			try {
				fFieldDef.setSequenceOverride( Integer.parseInt(override) );
			} catch( NumberFormatException nfe ) {
				throw new ParseException( "Invalid sequenceOverride value: " + override );
			}
		}

		if( !getBooleanAttr(node,"sifdtd",true) )
			fFieldDef.setFlags( fFieldDef.getFlags() | AbstractDef.FLAG_NO_SIFDTD );
		if( !getBooleanAttr(node,"encode",true) )
			fFieldDef.setFlags( fFieldDef.getFlags() | FieldDef.FLAG_DO_NOT_ENCODE );
		
		if( getBooleanAttr( node, "collapsed", false ) ){
			fFieldDef.setFlags( fFieldDef.getFlags() | FieldDef.FLAG_COLLAPSED );
		}
		
		
		String draft = getAttr(node,"draft");
		if( draft != null && draft.equalsIgnoreCase("true") )
			fFieldDef.setDraft();

		fFieldDef.setLatestVersion( fVersion );
		fFieldDef.setEarliestVersion( fVersion );

		if( ( fFieldDef.getFlags() & AbstractDef.FLAG_MANDATORY ) != 0 && !getBooleanAttr(node,"key",true) )
		{
			//  By default all attributes with a "R" flag are used to generate
			//  the object's key. However, some attributes have an "R" flag but
			//  are not part of the key. When the key="false" attribute is
			//  specified, set the FLAG_NOT_A_KEY flag.

			fFieldDef.setFlags( fFieldDef.getFlags() | FieldDef.FLAG_NOT_A_KEY );
		}

		System.out.println(out.toString());
		
		fFieldDef.validate();
	}

	protected String getText( Node node )
	{
		if( node != null ) {
			NodeList ch = node.getChildNodes();
	    	for( int i = 0; i < ch.getLength(); i++ ) {
				if( ch.item(i).getNodeType() == Node.TEXT_NODE )
	    			return ch.item(i).getNodeValue();
			}
		}
		return null;
	}

	protected String getAttr( Node node, String attr )
	{
		NamedNodeMap attrs = node.getAttributes();
		for(int x = 0; x < attrs.getLength(); x++ ){
			Node n = attrs.item( x );
			if( n.getLocalName().equalsIgnoreCase( attr ) ){
				return n.getNodeValue();	
			}
		}
		return null;
	}

	protected boolean getBooleanAttr( Node node, String attr, boolean defValue )
	{
		String s = getAttr(node,attr);
		if( s == null )
			return defValue;
		if( s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") )
			return true;

		return false;
	}

    //
    // ErrorHandler methods
    //

    /** Warning. */
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] at "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }

    /** Error. */
    public void error(SAXParseException ex) {
        System.err.println("[Error] at "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error] at "+
                           getLocationString(ex));
        throw ex;
    }

    //
    // Private methods
    //

    /** Returns a string of the location. */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(", Line=");
        str.append(ex.getLineNumber());
        str.append(", Col=");
        str.append(ex.getColumnNumber());

        return str.toString();

    } // getLocationString(SAXParseException):String



	public String getFriendlyName() {
		return fFriendlyName;
	}


	protected String getLocalPackage() {
		return fLocalPackage;
	}

}
