//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import openadk.library.*;
import openadk.library.impl.surrogates.RenderSurrogate;
import openadk.library.impl.surrogates.XPathSurrogate;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *  Provides metadata for a single SIF data object type or field (an attribute
 *  or child element). This information is used internally by the class framework
 *  to parse and render messages.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class ElementDefImpl implements ElementDef
{
	// Cached array of supported SIF versions for faster lookups
	private static SIFVersion[] sSifVersions = ADK.getSupportedSIFVersions();

	/**
	 *  Flag indicating this is a field that should be rendered as an attribute
	 *  of its parent rather than a child of its parent.
	 */
	public static final int FD_ATTRIBUTE = 0x01;;

	/**
	 *  Flag indicating this is a simple field element with no children
	 */
	public static final int FD_FIELD = 0x02;

	/**
	 *  Flag indicating this is a top-level element (SIF_Ack, StudentPersonal, etc.)
	 */
	public static final int FD_OBJECT = 0x04;

	/**
	 *  Flag indicating this element is deprecated in this version of SIF
	 */
	public static final int FD_DEPRECATED = 0x08;

	/**
	 *  Flag indicating this element is repeatable in this version of SIF
	 */
	public static final int FD_REPEATABLE = 0x10;

	/**
	 * 	Flag indicating the content of this element should not be automatically
	 * 	escaped by the SIFWriter class.
	 */
	public static final int FD_DO_NOT_ENCODE = 0x20;

	/**
	 *  Flag indicating this element is a repeatable element container  and that the
	 *  container should be "collapsed" in this version of SIF.
	 *  This causes the repeatable element container to not be written in this version of SIF
	 */
	public static final int FD_COLLAPSE = 0x40;

	/**
	 * Flag indicating this element is the payload value of an XML Element
	 */
	public static final int FD_ELEMENT_VALUE = 0x80;



	/**
	 * A special flag in SIF used to indicate that the specified element is deleted
	 */
	public static final ElementDefImpl DELETED_FLAG =
		new ElementDefImpl(null,"Deleted",null,1,SIFDTD.common,(byte)0, SIFVersion.LATEST, SIFVersion.LATEST, SIFTypeConverters.BOOLEAN );

	/**
	 *  An array of VersionInfo objects that describe the element tag name or
	 *  attribute name and sequence number for each version of SIF. The first
	 *  element in the array represents SIF 1.0r1 (the first version supported
	 *  by the ADK), and the last element in the array represents the last
	 *  version of SIF supported by the ADK. Any array element with a null value
	 *  is considered to be identical to the first non-null element preceding
	 *  it.
	 */
	protected AbstractVersionInfo[] fInfo = new AbstractVersionInfo[ ADK.getSupportedSIFVersions().length ];

	/**
	 *  The version-independent name of this element (typically the same as
	 *  the tag name for SIF 1.0r1)
	 */
	protected String fName;

	/**
	 *  Version-independent flags
	 */
	protected int fFlags;

	/**
	 *  The parent element
	 */
	protected ElementDefImpl fParent;

	/**
	 *  The local package name where this element is defined in the SDO class library
	 */
	protected String fPackage;

	/**
	 *  The children of this element
	 */
	private HashMap<String, ElementDef> fChildren;

	/**
	 * The latest version this element or attribute appears in
	 */
	private final SIFVersion fLatestVersion;

	/**
	 * The SIF data type converter to use for this element
	 */
	protected SIFTypeConverter fTypeConverter;

	/**
	 *  Constructs an ElementDef with flag<p>
	 *
	 *  @param parent The parent of this element
	 *  @param name The version-independent name of the element
	 *  @param tag The element or attribute tag (if different from the name)
	 *  @param sequence The zero-based ordering of this element within its parent
	 *      or -1 if a top-level element
	 *  @param localPackage The name of the package where the corresponding
	 *      DataObject class is defined, excluding the
	 *      <code>com.edustructures.sifworks</code> prefix
	 *  @param earliestVersion The earliest version of SIF supported by this
	 *      element. If the element is supported in any other version of SIF -
	 *      or is deprecated in a later version - the SDOLibrary class must
	 *      define it by calling <code>defineVersionInfo</code>
	 */
    public ElementDefImpl( ElementDef parent, String name, String tag, int sequence, String localPackage, SIFVersion earliestVersion, SIFVersion latestVersion )
	{
		this( parent, name, tag, sequence, localPackage, (byte)0, earliestVersion, latestVersion );
	}


	/**
	 *  Constructs an ElementDef with flag<p>
	 *
	 *  @param parent The parent of this element
	 *  @param name The version-independent name of the element
	 *  @param tag The element or attribute tag (if different from the name)
	 *  @param sequence The zero-based ordering of this element within its parent
	 *      or -1 if a top-level element
	 *  @param localPackage The name of the package where the corresponding
	 *      DataObject class is defined, excluding the
	 *      <code>com.edustructures.sifworks</code> prefix
	 *  @param flags One of the following: FD_ATTRIBUTE if this element should
	 *      be rendered as an attribute of its parent rather than a child
	 *      element; FD_FIELD if this element is a simple field with no child
	 *      elements; or FD_OBJECT if this element is a SIF Data Object such
	 *      as StudentPersonal or an infrastructure message such as SIF_Ack;
	 *      FD_DEPRECATED if this element no longer applies to this version of
	 *      SIF
	 *  @param earliestVersion The earliest version of SIF supported by this
	 *      element. If the element is supported in any other version of SIF -
	 *      or is deprecated in a later version - the SDOLibrary class must
	 *      define it by calling <code>defineVersionInfo</code>
	 */
    public ElementDefImpl( ElementDef parent, String name, String tag, int sequence, String localPackage, int flags, SIFVersion earliestVersion, SIFVersion latestVersion )
	{
    	this( parent, name, tag, sequence, localPackage, flags, earliestVersion, latestVersion, (SIFTypeConverter)null );
	}

    public ElementDefImpl( ElementDef parent, String name, String tag, int sequence, String localPackage, int flags, SIFVersion earliestVersion, SIFVersion latestVersion, SIFTypeConverter typeConverter )
	{
		fName = name.intern();

		if( ( flags & FD_ATTRIBUTE ) != 0 ){
			// If this is an attribute, it is also a simple field
			flags |= FD_FIELD;
		}
		fLatestVersion = latestVersion;
		fFlags = flags;
		fPackage = localPackage.intern();
		fParent = (ElementDefImpl)parent;
		fTypeConverter = typeConverter;


		defineVersionInfo( earliestVersion, tag == null ? name : tag, sequence, flags );

		if( fParent != null ){
			fParent.addChild( this );
		}
	}

    protected void addChild( ElementDef child ){
    	if( fChildren == null )	{
    		fChildren = new HashMap<String, ElementDef>();
    	}
		fChildren.put( child.name(), child );
    }

	public void defineVersionInfo( SIFVersion version, String tag, int sequence, int flags )
	{
		AbstractVersionInfo vi = null;

		for( int i = 0; i < sSifVersions.length; i++ ) {
			if( version == sSifVersions[i] ) {
				if( fInfo[i] == null ) {
					fInfo[i] = vi = createVersionInfo( tag );
				} else {
					vi = fInfo[i];
				}
				break;
			}
		}

		if( vi == null )
			throw new IllegalArgumentException( "SIF " + version.toString() + " is not supported by the ADK" );

		if( ( flags & FD_DEPRECATED ) != 0 )
			vi.setFlag( AbstractVersionInfo.FLAG_DEPRECATED, true );
		if( ( flags & FD_REPEATABLE ) != 0 )
			vi.setFlag( AbstractVersionInfo.FLAG_REPEATABLE, true );
		if( ( flags & FD_ATTRIBUTE ) != 0 )
			vi.setFlag( AbstractVersionInfo.FLAG_ATTRIBUTE, true );
		if( ( flags & FD_COLLAPSE ) != 0 ){
			vi.setFlag( AbstractVersionInfo.FLAG_COLLAPSE, true );
		}

		vi.setSequence( sequence );
	}

	public String name()
	{
		return fName;
	}

	public String internalName()
	{
		return fName;
	}

	public String tag( SIFVersion version )
	{
		return info(version).getTag();
	}

	public int sequence( SIFVersion version )
	{
		return info(version).getSequence();
	}

	public String getSDOPath()
	{
		StringBuilder b = new StringBuilder( fName );
		ElementDefImpl p = fParent;
		while( p != null ) {
			b.insert(0,p.fName+"_");
			p = p.fParent;
		}

		return b.toString();
	}

	public String getSQPPath( SIFVersion version )
	{
		StringBuilder b = new StringBuilder( isAttribute( version ) ? "@" + tag(version) : tag(version) );
		ElementDefImpl p = fParent;
		while( p != null && !p.isObject() ) {
			ElementVersionInfo evi = p.getVersionInfo( version );
			if( !evi.isCollapsed() ){
				b.insert( 0, '/' );
				b.insert(0, evi.getTag() );
			}
			p = p.fParent;
		}
		return b.toString();
	}

	public ElementDef getParent()
	{
		return fParent;
	}

	public List<ElementDef> getChildren()
	{
		List<ElementDef>children = new ArrayList<ElementDef>();
		if( fChildren != null ){
			children.addAll( fChildren.values() );
			// TODO: Add support for looking up the children of Common elements
			// For example, LibraryPatronStatus_ElectronicIdList/ElectronicId has no children
			// in it's metadata because it's been re-assigned
		}
		return children;
	}

	/**
	 *  Gets the root metadata object
	 *  @return The root metadata object
	 */
	public ElementDef getRoot()
	{
		ElementDefImpl d = this;
		while( d.fParent != null ) {
			d = d.fParent;
		}

		return d;
	}

	/**
	 *  Returns the version-independent name of this element or attribute
	 *  @see #getName
	 */
	public String toString() {
		return fName;
	}



	public String getFQClassName() {

		StringBuilder sbuf = new StringBuilder();
		sbuf.append( "openadk.library." );

		if( fPackage != null )
		{
			sbuf.append( fPackage );
			sbuf.append( '.' );
		}
		sbuf.append( getClassName() );

		return sbuf.toString();

	}

	public int getSequence( SIFVersion version ) {
		return info(version).getSequence();
	}

	public boolean isAttribute( SIFVersion version ) {
		return info(version).getFlag( AbstractVersionInfo.FLAG_ATTRIBUTE );
	}

	public boolean isField() {
		return ( fFlags & FD_FIELD ) != 0;
	}

	public boolean isObject() {
		return ( fFlags & FD_OBJECT ) != 0;
	}

	public boolean isElementValue() {
		return ( fFlags & FD_ELEMENT_VALUE ) != 0;
	}

	public boolean isSupported( SIFVersion version )
	{
	    SIFVersion earliestVersion = getEarliestVersion();
	    
		boolean retval =  (earliestVersion == null || earliestVersion.compareTo( version ) < 1) &&
        (fLatestVersion == null || fLatestVersion.compareTo( version ) > -1);
				
		if (!retval) {
			String earliest = "";
			if (earliestVersion != null)
				earliest = earliestVersion.toString();
			
			String latest = "";
			if (fLatestVersion != null)
				latest = fLatestVersion.toString();
			
			ADK.getLog().info(this.toString() + " Does NOT support version: " + version + " Earliest: " + earliest + " Latest : " + latest);
		}
		
	    return retval;
	}

	public boolean isDeprecated( SIFVersion version ) {
		return info(version).getFlag( AbstractVersionInfo.FLAG_DEPRECATED );
	}

	public boolean isRepeatable( SIFVersion version ) {
		return info(version).getFlag( AbstractVersionInfo.FLAG_REPEATABLE );
	}

	public boolean isCollapsed( SIFVersion version ){
		return info(version).getFlag( AbstractVersionInfo.FLAG_COLLAPSE );
	}

	public boolean isDoNotEncode() {
		return ( fFlags & FD_DO_NOT_ENCODE ) != 0;
	}

	public String getPackage() {
		return fPackage;
	}
	public SIFVersion getEarliestVersion() {
		for( int i = 0; i < sSifVersions.length; i++ ) {
			if( fInfo[i] != null )
				return sSifVersions[i];
		}
		return null;
	}

	public SIFVersion getLatestVersion() {
		return fLatestVersion;
	}

	/**
	 *  Lookup the VersionInfo object for a specific version of SIF.
	 *  @return The AbstractVersionInfo instance
	 *  @throws RuntimeException if the ElementDef does not exist in the specified version of SIF
	 */
	protected AbstractVersionInfo info( SIFVersion v )
	{
		return getAbstractVersionInfo( v, true );
	}

	/**
	 *  Lookup the VersionInfo object for a specific version of SIF.
	 * @param v The version of SIF to search
	 * @param throwIfNotExists True if a RuntimeException should be thrown if the version information does not exist.
	 *    If False, NULL will be returned
	 * @return The AbstractVersionInfo instance or null
	 */
	protected AbstractVersionInfo getAbstractVersionInfo( SIFVersion v, boolean throwIfNotExists )
	{
		int last = -1;

		// Search the list of SIFVersions that the ADK supports. The list
		// is searched incrementally, starting with the oldest version. If
		// a version is found that directly matches the requested SIF Version,
		// return that entry. Otherwise, return the next previous entry from the list
		for(int i= 0; i < sSifVersions.length; i++ )
		{
			int comparison =  sSifVersions[i].compareTo( v );
			if( comparison < 1 && fInfo[i] != null ){
				last = i;
			}

			if( comparison > -1 )
			{
				// We have reached the SIFVersion in the list of supported versions that
				// is greater than or equal to the requested version. Return the last AbstractVersionInfo
				// from our array that we found
				break;
			}
		}

		if( last == -1 ){
			if( throwIfNotExists ){
				throw new RuntimeException( "Element or attribute \""+name()+"\" is not supported in SIF " + v.toString() );
			} else {
				return null;
			}
		}

		return fInfo[last];
	}

	public SIFTypeConverter getTypeConverter() {
		return fTypeConverter;
	}

	private AbstractVersionInfo createVersionInfo( String renderTag ){
		if( renderTag.startsWith( "~") ){
			// The tilde (~) symbolizes that this version needs a
			// custom surrogate. The Surrogate expression syntax is:
			// "~SurrogateName{constructor}renderAs"
			int surrogateEnd = renderTag.lastIndexOf( '}' );
			String surrogate = renderTag.substring( 0, surrogateEnd + 1 );
			String renderAs = null;
			if( surrogateEnd + 1 < renderTag.length() ){
				renderAs = renderTag.substring( surrogateEnd + 1 );
			}
			if( renderAs == null ){
				renderAs = name();
			}
			return new SurrogateVersionInfo( renderAs, surrogate, this );
		} else {
			return new TaggedVersionInfo( renderTag );
		}
	}
	static abstract class AbstractVersionInfo implements ElementVersionInfo
	{
		/**
		 *  Flag indicating this is a field that should be rendered as an attribute
		 *  of its parent rather than a child of its parent.
		 */
		static final int FLAG_ATTRIBUTE = 0x01000000;
		static final int FLAG_REPEATABLE 	= 0x02000000;
		static final int FLAG_DEPRECATED 	= 0x04000000;
		static final int FLAG_COLLAPSE 	= 0x08000000;

		public int fFlag;

		private String fTag;

		protected AbstractVersionInfo( String tag )
		{
			if( tag !=null ){
				fTag = tag.intern();
			}
		}


		public void setSequence( int sequence ){
			if( sequence >= 0 ){
				fFlag = fFlag | sequence;
			}
		}

		public int getSequence(){
			return fFlag & 0x00FFFFFF;
		}

		public boolean getFlag( int flag ){
			return (flag & fFlag) != 0;
		}

		public void setFlag( int flag, boolean value ){
			if( value ){
				fFlag |=  flag;
			} else {
				fFlag &= ~flag;
			}
		}

		public String getTag() {
			return fTag;
		}

		public boolean isCollapsed() {
			return getFlag( AbstractVersionInfo.FLAG_COLLAPSE );
		}

		public boolean isRepeatable() {
			return getFlag( AbstractVersionInfo.FLAG_REPEATABLE );
		}

		public boolean isAttribute() {
			return getFlag( AbstractVersionInfo.FLAG_ATTRIBUTE );
		}

	}

	static class TaggedVersionInfo extends AbstractVersionInfo
	{
		public TaggedVersionInfo( String tag )
		{
			super( tag );
		}

		public RenderSurrogate getSurrogate() {
			return null;
		}
	}

	static class SurrogateVersionInfo extends AbstractVersionInfo
	{
		private RenderSurrogate fSurrogate;
		private String fInitializer;
		private ElementDef fDef;
		public SurrogateVersionInfo( String renderAs, String surrogateString, ElementDef def )
		{
			super( renderAs );
			fInitializer = surrogateString;
			fDef = def;
		}

		@Override
		public String getTag()
		{
			String tag = super.getTag();
			if( tag == null ){
				tag = getSurrogate().getPath();
			}
			return tag;
		};

		public synchronized RenderSurrogate getSurrogate()  {

			if( fSurrogate == null ){
				String surrogateClassName = fInitializer;
				String initializer = null;
				int classInitializerStart = fInitializer.indexOf( "{" );
				if( classInitializerStart > -1 ){
					surrogateClassName = fInitializer.substring( 1, classInitializerStart );
					initializer = fInitializer.substring( classInitializerStart + 1 );
					if( initializer.equals( "}" ) ){
						initializer = null;
					} else {
						initializer = initializer.substring( 0, initializer.length() -1 );
					}
				} else {
					surrogateClassName = fInitializer.substring( 1 );
				}
				if( surrogateClassName.equals( "XPathSurrogate" ) ){
					fSurrogate = new XPathSurrogate( fDef, initializer );
				}
				else {
					try{
						Class surrogateClass = Class.forName( "openadk.library.impl.surrogates."  + surrogateClassName );
						Constructor c = null;
						if( initializer == null ){
							c = surrogateClass.getConstructor( new Class[] { ElementDef.class } );
							fSurrogate = ( RenderSurrogate )c.newInstance( fDef );
						} else {
							c = surrogateClass.getConstructor( new Class[] { ElementDef.class, String.class } );
							fSurrogate = ( RenderSurrogate )c.newInstance( fDef, initializer );
						}
					} catch( Exception iex ){
						throw new UnsupportedOperationException( "Surrogate " + fInitializer + " is not defined in this version of the ADK." + iex , iex );
					}
				}

				// Now that the Surrogate has been loaded, the fInitializer
				// data is no longer needed. Free the memory used by it
				fInitializer = null;

			}
			return fSurrogate;
		}
	}

	public ElementVersionInfo getVersionInfo(SIFVersion version) {
		return getAbstractVersionInfo( version, false );
	}


	public String getClassName() {
		return fName;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.ElementDef#hasSimpleContent()
	 */
	public boolean hasSimpleContent() {
		return fTypeConverter != null;
	}


	/**
	 * 	Helper method that can be called from a Serializable class's writeObject
	 * 	method to serialize an ElementDef.<p>
	 *
	 * 	ElementDefImpl is not Serializable, so this method can be called by classes
	 * 	that need to serialize an ElementDef data member. The approach taken is to
	 * 	serialize only the SDO path to the ElementDef. During deserialization, that
	 * 	path is used to lookup the corresponding ElementDef in the ADK.DTD()
	 * 	dictionary. The process that is deserializing the object must have
	 * 	previously initialized the ADK.<p>
	 *
	 * 	@param element The ElementDef being serialized
	 * 	@param out The ObjectOutputStream to which the caller is serializing
	 */
	public static void writeObject( ElementDef element, ObjectOutputStream out ) throws IOException {
		out.writeUTF(element.getSDOPath());
	}

	/**
	 * 	Helper method that can be called from a Serializable class's readObject
	 * 	method to deserialize an ElementDef.<p>
	 *
	 * 	ElementDefImpl is not Serializable, so this method can be called by classes
	 * 	that need to deserialize an ElementDef data member. The approach taken by
	 * 	the writeObject method is to serialize only the SDO path to the ElementDef.
	 * 	During deserialization, that path is used to lookup the corresponding
	 * 	ElementDef in the ADK.DTD() dictionary. The process that is deserializing
	 * 	the object must have previously initialized the ADK.<p>
	 *
	 * 	@param in The ObjectInputStream from which the caller is deserializing
	 * 	@return An ElementDef
	 */
	public static ElementDef readObject( ObjectInputStream in ) throws IOException {
		if( !ADK.isInitialized() )
			throw new RuntimeException("Cannot deserialize ElementDef: ADK is not initialized");
		String path = in.readUTF();
		if( path != null && path.length()>0 ) {
			return ADK.DTD().lookupElementDef(path);
		}
		throw new RuntimeException( "Unable to deserialize ElementDef: " + path);
	}
}
