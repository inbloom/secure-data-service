//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.metadata;

import openadk.library.*;
import openadk.library.impl.*;


/**
 */
public class ADKMetadata
{
	/**
	 * 	Metadata flag: Identifies a repeatable element
	 */
	public static final byte MD_REPEATABLE = ElementDefImpl.FD_REPEATABLE;
	
	
	private static ADKMetadata gSingleton = null;
	
	private ADKMetadata()
	{
		
	}
	
	public static ADKMetadata getInstance() 
	{
		if( !ADK.isInitialized() ) {
			throw new IllegalStateException( "ADK is not initialized" );
		}
		if( gSingleton == null ) {
			gSingleton = new ADKMetadata();
		}
		
		return gSingleton;
	}
	
	/**
	 * 	Define a top-level SIF Data Object.
	 * 
	 * 	@param name The tag name of the data object
	 * 	@param earliestVersion The earliest version of the specification this object should
	 * 		be recognized in
	 * 	@return An ElementDef instance encapsulating metadata for this data object
	 */
	public ElementDef defineDataObject( String name, SIFVersion earliestVersion )
	{
		ElementDef ed = new ElementDefImpl(null,name,null,0,"custom",ElementDefImpl.FD_OBJECT,earliestVersion, SIFVersion.LATEST );
		SIFDTD.sElementDefs.put( name, ed );
		return ed;
	}
	
	/**
	 * 	Define a complex element.<p>
	 * 
	 * 	A complex element has attributes and/or elements of its own and is represented 
	 * 	by its own class. Complex elements must be defined with this method, and then 
	 * 	added as a child element to a SIF Data Object by calling {@link #defineChildElement}.<p>
	 * 	
	 * 	@param name The tag name of the element
	 * 	@param earliestVersion The earliest version of the specification this element should
	 * 		be recognized in
	 * 	@return An ElementDef instance encapsulating metadata for this element
	 */
	public ElementDef defineElement( String name, SIFVersion earliestVersion )
	{
		return new ElementDefImpl(null,name,null,0,"custom",(byte)0,earliestVersion, SIFVersion.LATEST );
	}
	
	/**
	 * 	Define an attribute of a SIF Data Object.<p>
	 * 
	 * 	@param parent The ElementDef constant identifying the parent data object
	 * 	@param name The tag name of the attribute
	 * 	@param sequence The zero-based sequence number of the attribute
	 * 	@param earliestVersion The earliest version of the specification this attribute should
	 * 		be recognized in
	 * 	@return An ElementDef instance encapsulating metadata for this attribute
	 */
	public ElementDef defineAttribute( ElementDef parent, String name, int sequence, SIFVersion earliestVersion )
	{
		if( parent == null ) {
			throw new IllegalArgumentException( "Parent cannot be null" );
		}
		
		ElementDef ed = new ElementDefImpl(parent,name,null,sequence,"custom",ElementDefImpl.FD_ATTRIBUTE,earliestVersion, SIFVersion.LATEST );
		SIFDTD.sElementDefs.put( parent.name() + "_" + name, ed );
		return ed;
	}
	
	/**
	 * 	Define a field of a SIF Data Object.<p>
	 * 
	 * 	A field is a simple child element that has no attributes or elements of its own and 
	 * 	is not represented by its own class. For example, the <code>&lt;LocalId&gt;</code> 
	 * 	common element in SIF 1.5. Internally, the ADK stores fields more efficiently than
	 * 	complex elements.<p>
	 * 	
	 * 	@param parent The ElementDef constant identifying the parent data object or element
	 * 	@param name The tag name of the element
	 * 	@param sequence The zero-based sequence number of the element
	 * 	@param flags Optional flags for this field (e.g. <code>MD_REPEATABLE</code>), or zero
	 * 		if no flags are applicable
	 * 	@param earliestVersion The earliest version of the specification this element should
	 * 		be recognized in
	 * 	@return An ElementDef instance encapsulating metadata for this element
	 */
	public ElementDef defineField( ElementDef parent, String name, int sequence, byte flags, SIFVersion earliestVersion )
	{
		if( parent == null ) {
			throw new IllegalArgumentException( "Parent cannot be null" );
		}
		
		ElementDef ed = new ElementDefImpl(parent,name,null,sequence,"custom",(byte)(ElementDefImpl.FD_FIELD | flags),earliestVersion, SIFVersion.LATEST );
		SIFDTD.sElementDefs.put( parent.name() + "_" + name, ed );
		return ed;
	}
	
	/**
	 * 	Define a complex child element of a SIF Data Object.<p>
	 * 
	 * 	The child element must be an existing element, such as a Common Element
	 * 	(e.g. <code>SIFDTD.NAME</code>)
	 * 	
	 * 	@param parent The ElementDef constant identifying the parent data object or element
	 * 	@param element The ElementDef constant of an existing element, such as a
	 * 		Common Element (e.g. <code>SIFDTD.NAME</code>)
	 * 	@param sequence The zero-based sequence number of the element
	 * 	@param flags Optional flags for this field (e.g. <code>MD_REPEATABLE</code>), or zero
	 * 		if no flags are applicable
	 * 	@param earliestVersion The earliest version of the specification this element should
	 * 		be recognized in
	 * 	@return An ElementDef instance encapsulating metadata for this element
	 */
	public ElementDef defineChildElement( ElementDef parent, ElementDef element, int sequence, byte flags, SIFVersion earliestVersion )
	{
		if( parent == null ) {
			throw new IllegalArgumentException( "Parent cannot be null" );
		}
		if( element == null ) {
			throw new IllegalArgumentException( "Element cannot be null" );
		}
		
		ElementDef ed = new ElementDefImpl(parent,element.name(),null,sequence,element.getPackage(),flags,earliestVersion, SIFVersion.LATEST );
		SIFDTD.sElementDefs.put( parent.name() + "_" + element.name(), ed );
		return ed;
	}
	
}
