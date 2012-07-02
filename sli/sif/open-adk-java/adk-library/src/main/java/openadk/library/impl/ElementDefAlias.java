//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;

/**
 *  An ElementDef that accepts an implementation class name as one of its
 *  constructor parameters and returns that class name in the getClassName
 *  method. This class is used only for those elements where the name of the
 *  implementation class (e.g. "Country") is different than the name of the
 *  element tag (e.g. "CountryOfBirth").
 */
public class ElementDefAlias extends ElementDefImpl
{
	protected String fClassName;

	/**
	 *  Constructs an ElementDefAlias<p>
	 *
	 *  @param parent The parent of this element
	 *  @param tag The name of the element
	 *  @param sequence The zero-based ordering of this element within its parent
	 *      or -1 if a top-level element
	 *  @param localPackage The name of the package where the corresponding
	 *      DataObject class is defined, excluding the
	 *      <code>com.edustructures.sifworks</code> prefix
	 *  @param min The earliest version of SIF supported by this element
	 *  @param max The latest version of SIF supported by this element
	 */
    public ElementDefAlias( ElementDef parent, String name, String tag, String className, int sequence, String localPackage, SIFVersion earliestVersion, SIFVersion latestVersion )
	{
		this(parent,name,tag,className, sequence,localPackage,0, earliestVersion, latestVersion, null );
	}
    
    public ElementDefAlias( ElementDef parent, String name, String tag, String className, int sequence, String localPackage, int flags, SIFVersion earliestVersion, SIFVersion latestVersion )
	{
		this(parent,name,tag,className,sequence,localPackage,flags,earliestVersion, latestVersion, null);
	}


	/**
	 *  Constructs an ElementDefAlias with flag<p>
	 *
	 *  @param parent The parent of this element
	 *  @param tag The name of the element
	 *  @param sequence The zero-based ordering of this element within its parent
	 *      or -1 if a top-level element
	 *  @param localPackage The name of the package where the corresponding
	 *      DataObject class is defined, excluding the
	 *      <code>com.edustructures.sifworks</code> prefix
	 *  @param flags One of the following: FD_ATTRIBUTE if this element should
	 *      be rendered as an attribute of its parent rather than a child
	 *      element; FD_FIELD if this element is a simple field with no child
	 *      elements; or FD_OBJECT if this element is a SIF Data Object such
	 *      as StudentPersonal or an infrastructure message such as SIF_Ack
	 *  @param min The earliest version of SIF supported by this element
	 *  @param max The latest version of SIF supported by this element
	 */
    public ElementDefAlias( ElementDef parent, String name, String tag, String className, int sequence, String localPackage, int flags, SIFVersion earliestVersion, SIFVersion latestVersion, SIFTypeConverter converter )
	{
		super(parent,name,tag,sequence,localPackage,flags,earliestVersion,latestVersion,converter);
		fClassName = className;
	}

	/**
	 * 	Return the class name of this alias ElementDef, which will be different
	 * 	than the tag name (e.g. "Country" for an alias that represents 
	 * 	"CountryOfBirth")
	 */
	public String getClassName() {
		return fClassName;
	}
	
	/**
	 * 	Return the name of this alias ElementDef, overridden here to be the same 
	 * 	as the classname regardless of what is passed to the constructor. Note the
	 * 	name will be different than the tag name for an alias (e.g. "Country" is
	 * 	returned for an alias that represents "CountryOfBirth")
	 */
	public String name() {
		return fClassName;
	}
	
	public String internalName() {
		return fName;
	}
}
