//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.io.Serializable;

/**
 *  The abstract base class for all element and attribute classes in the SIF
 *  Data Objects library.<p>
 *
 *  Agent developers do not generally work with this class directly.<p>
 *
 *  <b>ElementDef</b><p>
 *
 *  Each Element instance is associated with an ElementDef that both identifies
 *  and describes the element or attribute encapsulated by this class. ElementDef
 *  constants are defined by the <code>SIFDTD</code> class for each element and
 * 	attribute defined in the SIF Specification. These objects provide
 * 	information about how to render elements in a version-dependent way,
 *  including the tag name and sequence number (which may vary from one version
 *  of SIF to the next), the SDO implementation class name, the earliest version
 *  of SIF the element or attribute appeared in, and the latest version of SIF
 *  that supports the element or attribute.<p>
 *
 *  An ElementDef must be provided to the constructor.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public abstract class Element implements Serializable, Cloneable
{
	/**
	 * The default java serialization version for ADK SDO classes
	 */
	public static final int CURRENT_SERIALIZE_VERSION = 2;

	/**
	 *  State flag indicating this Element has a dirty value. This state is
	 *  enabled at the time of constuction (i.e. all elements are dirty by
	 *  default) and must be explicitly disabled by an agent as necessary.
	 */
	private static final byte DIRTY = 0x01;

	/**
	 *  State flag indicating this Element has an empty value. This state is
	 *  disabled at the time of construction, but may be set or cleared by
	 *  methods that set this element's value.
	 */
	private static final byte EMPTY = 0x02;


//	/**
//	 * State flag indicating that this Element has a "Deleted='True'" attribute.
//	 * The "Deleted='True'" attribute is a flag that has a meaning only for repeatable
//	 * elements
//	 */
//	protected static final byte DELETED = 0x04;

	/**
	 *  Option flag indicating XML Encoding of character entities should not
	 * 	be performed on this Element's text content when rendered by the ADK.
	 */
	private static final byte OPT_DO_NOT_ENCODE = 0x04;

	/**
	 *  Identifies this field by its ElementDef constant defined in SIFDTD
	 */
	protected transient ElementDef fElementDef;

	/**
	 *  State flags keep track of whether this Element is changed or has an
	 *  empty value. By default the ADK does not track the state of an Element
	 *  when the #setTextValue method is called to change its value. The
	 * 	programmer must explicitly call the #setChanged and #setEmpty methods
	 * 	to mark an Element as changed or empty.
	 */
	protected byte fFlags;

	/**
	 * 	The parent Element, or null if there is no parent
	 */
	protected Element fParent;

	/**
	 *  Constructor
	 *  @param def The metadata that describes this Element
	 */
    public Element( ElementDef def )
	{
		if( def == null )
			throw new RuntimeException( "SIF " + ADK.getSIFVersion() + " does not support this element or attribute, or the required SDO library is not loaded (" + getClass().toString() + ")" );

		fElementDef = def;
		fFlags = DIRTY;
    }

	/**
	 *  Gets the metadata for this Element
	 *  @return an ElementDef that describes this Element
	 */
	public ElementDef getElementDef()
	{
		return fElementDef;
	}

	/**
	 * 	Sets the metadata for this Element.<p>
	 *
	 * 	Note this method should not generally be called by agents because the
	 * 	ElementDef metadata is established in the constructor. It is provided
	 * 	in order to support the dynamic creation of Element instances by clients
	 * 	that do not (or cannot) use reflection to call the default constructor.
	 * 	These clients can call the <code>Class.newInstance</code> method followed
	 * 	by <code>setElementDef</code> to construct an Element dynamically.<p>
	 *
	 * 	@param def An ElementDef instance that describes this Element
	 */
	public void setElementDef( ElementDef def )
	{
		fElementDef = def;
	}

	/**
	 *  Gets the text value of this element if applicable. The
	 *  default format of the text value is the SIF 1.x format<p>
	 *  To change the format used for Text values on elements, set the
	 *  {@link ADK#setTextFormatter(SIFFormatter)} property
	 *  @return The text value of this element
	 */
	public abstract String getTextValue();


	/**
	 *  Sets the text value of this element if applicable. The
	 *  text value will be parsed into the native datatype of the
	 *  the element. <p>
	 *  The formatter used for parsing by default is
	 *  the SIF 1.x formatter, which means this value must be able
	 *  to be parsed using SIF 1.x formatting rules.
	 *  To change the format used for Text values on elements, set the
	 *  {@link ADK#setTextFormatter(SIFFormatter)} property
	 *  @param value The text value of this element
	 */
	public abstract void setTextValue( String value );


	/**
	 * Sets the SIF strongly-typed value of this element
	 * @param value The SIF Value to set
	 */
	public abstract void setSIFValue( SIFSimpleType value );


	/**
	 * Gets the SIF strongly-typed value of this element
	 * @return The value of this element as a <c>SIFSimpleType</c>
	 */
	public abstract SIFSimpleType getSIFValue();

	/**
	 *  Sets this DataObject and each of its children to the dirty state. An
	 *  object in the dirty state will not be written to an XML stream when a
	 *  SIF message is rendered.
	 */
	public void setChanged()
	{
		setChanged(true);
	}

	/**
	 *  Sets this DataObject and each of its children to the specified dirty
	 *  state. An object in the dirty state will not be written to an XML stream
	 *  when a SIF message is rendered.
	 *
	 *  @param changed true to set the dirty state, false to clear it
	 */
	public void setChanged( boolean changed )
	{
		if( changed ) {
			fFlags |= DIRTY;
			if( fParent != null ){
				fParent.setChildChanged();
			}
		} else {
			fFlags &= ~DIRTY;
		}
	}

	/**
	 * Called by children when they are set to a "changed" state. The
	 * parent of any child element that is changed should also be marked as
	 * changed (but not any other children of the parent).
	 */
	protected void setChildChanged(){
		// don't want to recurse children. This just a call
		// to recurse up the chain
		if( !isChanged() ){
			fFlags |= DIRTY;
			if( fParent != null ){
				fParent.setChildChanged();
			}
		}

	}

	/**
	 *  Sets this DataObject and each of its children to the empty state. An
	 *  object in the empty state will be written to an XML stream as an empty
	 *  element with no attributes and no child elements.
	 */
	public void setEmpty()
	{
		setEmpty(true);
	}

	/**
	 *  Sets this DataObject and each of its children to the specified empty
	 *  state.  An object in the empty state will be written to an XML stream as
	 *  an empty element with no attributes and no child elements.
	 *
	 *  @param empty true to set the empty state, false to clear it
	 */
	public void setEmpty( boolean empty )
	{
		if( empty ) {
			fFlags |= EMPTY;
		} else {
			fFlags &= ~EMPTY;
		}
	}

	/**
	 *  Determines if this object is in the changed state.
	 *  @return true if this object has been marked changed. The return value
	 *      assumes all children are in the same state.
	 */
	public boolean isChanged() {
		return ( fFlags & DIRTY ) != 0;
	}

	/**
	 *  Determines if this object is in the empty state
	 *  @return true if this object has explicitly been marked empty. The return
	 *      value assumes all children are in the same state.
	 */
	public boolean isEmpty() {
		return ( fFlags & EMPTY ) != 0;
	}

	/**
	 * 	Determines if automatic XML Encoding of character entities should be
	 * 	performed on this element when rendered by the ADK. By default, all
	 * 	elements and attributes are encoded. Use the #setDoNotEncode method to
	 * 	turn off automatic encoding for an element if you will be assigning
	 * 	XML content to its text value (e.g. if you are using SIF_ExtendedElement
	 * 	to exchange raw XML content with another agent).
	 *
	 * 	@return <code>true</code> if automatic XML Encoding is disabled for this
	 * 		element; <code>false</code> if enabled (the default)
	 */
	public boolean isDoNotEncode()
	{
		if( ( fFlags & OPT_DO_NOT_ENCODE ) != 0 )
			return true;
		if( fElementDef != null ) {
			return fElementDef.isDoNotEncode();
		}

		return false;
	}

	/**
	 * 	Determines if automatic XML Encoding of character entities should be
	 * 	performed on this element when rendered by the ADK. By default, all
	 * 	elements and attributes are encoded. Use this method to turn off
	 * 	automatic encoding for an element if you will be assigning XML content
	 * 	to its text value (e.g. if you are using SIF_ExtendedElement
	 * 	to exchange raw XML content with another agent).
	 *
	 * 	@param option <code>true</code> if automatic XML Encoding is disabled
	 * 		for this element; <code>false</code> if enabled (the default)
	 */
	public void setDoNotEncode( boolean option )
	{
		if( option ) {
			fFlags |= OPT_DO_NOT_ENCODE;
		} else {
			fFlags &= ~OPT_DO_NOT_ENCODE;
		}
	}

	/**
	 *  Compare the text value of this Element to another Element<p>
	 *
	 *  @param target The Element to be compared
	 *
	 *  @return the value <code>0</code> if the argument's text value is
	 *      lexicographically equal to this Element's text value; a value less
	 *      than <code>0</code> if the argument's value is lexicographically
	 *      greater than this Element's text value; and a value greater than
	 *      <code>0</code> if the argument's text value is lexicographically
	 *      less than this Element's text value. If one Element's text value is
	 *      null and the others is not, a negative value is returned.
	 */
	public int compareTo( Element target )
	{
		String s1 = getTextValue();
		String s2 = target.getTextValue();

		if( s1 != null && s2 != null )
			return s1.compareTo(s2);
		if( s1 == null && s2 == null )
			return 0;

		return -1;
	}

	private void writeObject( java.io.ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();

		//
		// Write serialize version
		//
		out.write( CURRENT_SERIALIZE_VERSION );



	}

	private void readObject( java.io.ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		//
		// Read serialize version
		//
		int version = in.read();
		// NOTE: This type of version checking is also done by the Java serialization
		// infrastructure. We decided to keep our check as well because we may decide
		// to implement our own versioning strategy. For now, however, just throw an exception
		// if the versions don't match.
		if( version != Element.CURRENT_SERIALIZE_VERSION ){
			throw new IOException("Cannot deserialize Element with version signature ("+version+")");
		}
	}

	/**
	 *  Returns the value of <code>getTextValue</code>
	 *  @return The text value of this element if applicable
	 */
	public String toString()
	{
		String retval = "";
		if (getTextValue() != null)
			retval = getTextValue();
		else if (fElementDef != null)
			retval = fElementDef.tag(ADK.getSIFVersion());
		
		return retval;
//		return getTextValue();
	}

	/**
	 *  Gets the parent of this Element.<p>
	 * 	@return this Element's parent or <code>null</code> if it has none
	 * 	@see #setParent
	 */
	public Element getParent()
	{
		return fParent;
	}

	/**
	 *  Sets the parent of this Element.<p>
	 * 	@param parent the new parent Element
	 * 	@see #getParent
	 */
	public void setParent( Element parent )
	{
		fParent = parent;
	}

	/**
	 * Enumerating the ancestry of this object to return the root Element<p>
	 * @return the root Element that is a parent of this Element
	 */
	public Element getRoot()
	{
		if( fParent == null )
			return this;

		Element p = fParent;
		while( p.fParent != null )
			p = p.fParent;

		return p;
	}

}
