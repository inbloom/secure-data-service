//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openadk.library.impl.*;

/**
 * The abstract base class for all SIF Elements.
 * <p>
 * 
 * Each object type and complex field element defined in the SIF Specification
 * is encapsulated by a subclass of SIFElement. Objects include
 * &lt;StudentPersonal&gt;, &lt;StaffPersonal&gt;, &lt;BusInfo&gt;, etc. while
 * complex field elements include &lt;Address&gt;, &lt;OtherId&gt;,
 * &lt;PhoneNumber&gt; and so on. Simple fields which have only a string value
 * but which have no child elements are encapsulated by the SimpleField class
 * instead of by SIFElement. An example of such a field is the &lt;FirstName&gt;
 * child of the &lt;Name&gt; element.
 * <p>
 * 
 * SIFElements may have a single parent and zero or more children. Complex
 * fields are always represented as child objects. The addChild, getChildren,
 * removeChild, countChildren, and removeAllChildren methods are provided to
 * manipulate the child list. Simple fields are stored in a dictionary keyed by
 * the field's <code>ElementDef</code> constant as defined in the SIFDTD class.
 * The value of a simple field is encapsulated by a SimpleField object, which
 * stores not only the current string value of the field but also its change
 * state and a reference to its ElementDef.
 * <p>
 * 
 * <b>Comparing SIFElement Graphs</b>
 * </p>
 * 
 * Agent developers do not typically work with SIFElement objects directly. The
 * <code>compareGraphTo</code> method is one exception. Agents can use this
 * method to compare the contents of two SIFElements in order to determine which
 * elements and attributes are different. For an example of how this method can
 * be used to assist in SIF_Event reporting, refer to the SchoolInfoProvider ADK
 * Example program. Similarly, the SIFDiff example program demonstrates using
 * the <code>compareGraphTo</code> method to display the differences between two
 * SIF Data Objects read from disk.
 * <p>
 * 
 * <b>SIFVersion</b>
 * <p>
 * 
 * The abstract <code>getSIFVersion</code> method returns the SIFVersion that is
 * currently in effect for this object. This is often used to determine the
 * element tag name and sequence number when rendering XML because these may
 * change from one version of SIF to the next. However, the SIFElement class
 * does not itself keep track of version; it is up to the derived class to do
 * so. Both SIFDataObject and SIFMessagePayload store the SIF version associated
 * with their objects. By working up the object ancestry, it is possible to
 * determine the SIFVersion currently associated with a SIFElement. The
 * <code>effectiveSIFVersion</code> method performs this task.
 * <p>
 * 
 * @author Eric Petersen
 * @version 1.0
 */
public abstract class SIFElement extends Element {
	/**
	 * Field values. Simple fields (i.e. attributes and child elements that have
	 * only a text value and no attributes) are stored in the hashtable as
	 * SimpleField objects keyed by their associated ElementDef tag. ElementDef
	 * constants are defined by the SIFDTD class. Complex fields are stored in
	 * the fChildren vector.
	 */
	protected Map<String, SimpleField> fFields;

	/**
	 * Used for storing a unique identifier for this element. Used in getID()
	 * and setID();
	 */
	private String fId;

	/**
	 * Child elements.
	 */
	protected List<SIFElement> fChildren;

	/**
	 * The object that should be synchronized on for any add or remove
	 * operations affecting the children elements
	 */
	protected transient Object fSyncLock = new Object();

	/**
	 * Constructs a SIFElement
	 * <p>
	 * 
	 * @param def
	 *            The ElementDef constant from the <code>SIFDTD</code> class
	 *            that provides metadata for this element
	 */
	public SIFElement(ElementDef def) {
		super(def);
		fParent = null;
	}

	/**
	 * Gets the SIFVersion associated with this element, if applicable.
	 * <p>
	 * 
	 * The base class implementation of this method always returns null.
	 * <p>
	 * 
	 * @return A SIFVersion object that identifies the version of SIF that
	 *         should be used to render this object (or that was used to parse
	 *         it). Not all implementation classes store a SIFVersion, so null
	 *         may be returned.
	 */
	public SIFVersion getSIFVersion() {
		return null;
	}

	/**
	 * Sets the SIFVersion associated with this element, if applicable.
	 * <p>
	 * 
	 * The base class implementation of this method does nothing.
	 * <p>
	 * 
	 * @param version
	 *            A SIFVersion object that identifies the version of SIF that
	 *            should be used to render this object (or that was used to
	 *            parse it). Not all implementation classes store a SIFVersion,
	 *            so calling this method may have no affect.
	 */
	public void setSIFVersion(SIFVersion version) {
		// The base implementation does nothing
	}

	/**
	 * Gets the SIFVersion effective for this element by searching the ancestry
	 * until a valid SIFVersion is returned by one of the parent objects.
	 * <p>
	 * 
	 * @return A SIFVersion object that identifies the version of SIF that
	 *         should be used to render this object (or that was used to parse
	 *         it).
	 */
	public SIFVersion effectiveSIFVersion() {
		SIFVersion v = getSIFVersion();
		SIFElement p = (SIFElement) fParent;
		while (v == null && p != null) {
			v = p.getSIFVersion();
			p = (SIFElement) p.fParent;
		}

		if (v == null) {
			v = ADK.getSIFVersion();
		}
		return v;
	}

	/**
	 * Gets the tag name for this element. The effective version of SIF is used
	 * to determine the exact tag name since tag names may change from one
	 * version of SIF to the next.
	 * <p>
	 * 
	 * Note: In order for this method to return the proper tag name, it must
	 * know the version of SIF in use. The version is obtained by visiting the
	 * element ancestry and calling getSIFVersion on each parent until a
	 * non-null value is returned. Thus, this is a relatively expensive
	 * operation and should only be called when the SIFVersion is not known. If
	 * the SIFVersion is known, calling
	 * <code>getElementDef().tag(<i>version</i>)</code> directly is preferred.
	 * <p>
	 * 
	 * @return The element tag name that should be used when rendering XML
	 * 
	 * @see #effectiveSIFVersion
	 */
	public String tag() {
		return fElementDef.tag(effectiveSIFVersion());
	}

	/**
	 * Gets the Vector of child objects.
	 */
	protected List<SIFElement> _childList() {
		if (fChildren == null) {
			synchronized (fSyncLock) {
				if (fChildren == null) {
					fChildren = new ArrayList<SIFElement>(1);
				}
			}
		}
		return fChildren;
	}

	/**
	 * Returns the unique identifier that was set to the
	 * {@link #setXmlId(String)} method.
	 * <p>
	 * This value is not used by the ADK and is reserved for use by the
	 * application.
	 * 
	 * @return a string value that uniquely identifies this object to the
	 *         application.
	 */
	public String getXmlId() {
		return fId;
	}

	/**
	 * Sets an identifier that can be used to uniquely identify this SIFElement
	 * instance to an application.
	 * <p>
	 * This property is not used by the ADK and is reserved for use by the
	 * application.
	 * 
	 * @param id
	 *            a String value that uniquely identifies this object to the
	 *            application that is using it.
	 */
	public void setXmlId(String id) {
		fId = id;
	}

	/**
	 * Gets the key of this object. All SIFElements must be able to return a
	 * unique key that distinguishes the object from its peers in a child list.
	 * For SIF, the key is almost always the "RefId" field of an object. In some
	 * cases more than one attribute is combined to form a key; in this case the
	 * convention "attr1.attr2" should be used, where the key values are listed
	 * in sequential order.
	 * <p>
	 * 
	 * @return The key value of this object
	 */
	public String getKey() {
		return null;
	}

	/**
	 * Gets the child object with the matching element name and key
	 * <p>
	 * 
	 * @param name
	 *            The version-independent element name. Note the element name is
	 *            not necessarily the same as the element tag, which is version
	 *            dependent.
	 * @param key
	 *            The key to match
	 * @return The SIFElement that has a matching element name and key, or null
	 *         if no matches found
	 */
	public SIFElement getChild(String name, String key) {
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			for (SIFElement child : v) {
				if (((ElementDefImpl) child.fElementDef).internalName().equals(name) && (key == null || (child.getKey().equals(key))))
					return child;
			}
		}

		return null;
	}

	/**
	 * Gets a child object identified by its ElementDef and composite key
	 * <p>
	 * 
	 * @param id
	 *            A ElementDef defined by the SIFDTD class to uniquely identify
	 *            this field
	 * @param compKey
	 *            The key values in sequential order
	 * @return The child that was requested, or null
	 */
	public SIFElement getChild(ElementDef id, String[] compKey) {
		StringBuilder b = new StringBuilder(compKey[0]);
		for (int i = 1; i < compKey.length; i++) {
			b.append(".");
			b.append(compKey[i]);
		}

		return getChild(id, b.toString());
	}

	/**
	 * Adds a child SIFElement
	 * <p>
	 * 
	 * @param id
	 *            The ElementDef for the child
	 * @param element
	 *            The child element
	 * @throws IllegalArgumentException
	 *             Thrown if the child being added is already a child of a
	 *             different parent
	 */
	public void addChild(ElementDef id, SIFElement element) {
		safeAddChild(id, element);
	}

	/**
	 * A safe, final implementation of AddChild that can be called from a
	 * constructor
	 * 
	 * @param element
	 *            The SIFElement to be added as a child to this SIFElement
	 * @throws IllegalArgumentException
	 *             if the element is already a child of another element
	 * @return The SIFElement that was added as a child
	 */
	private final SIFElement safeAddChild(SIFElement element) {
		if (element == null || element.fParent == this)
			return element;

		if (element.fParent != null) {
			throw new IllegalStateException("Element \"" + element.fElementDef.name() + "\" is already a child of another element");
		}

		restoreImplementationDef(element);
		element.fParent = this;
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			v.add(element);
		}
		return element;
	}

	/**
	 * A safe, final implementation of AddChild that can be called from a
	 * constructor
	 * 
	 * @param element
	 *            The SIFElement to be added as a child to this SIFElement
	 * @throws IllegalArgumentException
	 *             if the element is already a child of another element
	 */
	protected final void safeAddChild(ElementDef def, SIFElement element) {
		if (element == null || element.fParent == this)
			return;

		if (element.fParent != null) {
			throw new IllegalStateException("Element \"" + element.fElementDef.name() + "\" is already a child of another element");
		}
		element.fParent = this;
		element.fElementDef = def;

		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			v.add(element);
		}
	}

	/**
	 * Adds a child SIFElement.
	 * 
	 * @param element
	 * @return The SIFElement that was actually added as a child to this object.
	 * @throws IllegalArgumentException
	 *             Thrown if the child being added is already a child of a
	 *             different parent
	 */
	@SuppressWarnings("unchecked")
	public SIFElement addChild(SIFElement element) {
		return safeAddChild(element);
	}

	/**
	 * Return the actual ElementRef of the child. For example, if this were a
	 * StudentPersonal and the child were a Name, we would reassign its
	 * ElementRef to be ADK.DTD.STUDENTPERSONAL_NAME
	 * <p>
	 * We also need to redefine the ElementRef to be
	 * ADK.DTD.STUDENTPERSONAL_NAME. This will allow it to be written in the
	 * proper sequence in versions of SIF in which it is collapsed
	 * 
	 * @param candidate
	 */
	public void restoreImplementationDef(Element candidate) {

		ElementDef candidateDef = candidate.fElementDef;
		ElementDef parentDef = candidateDef.getParent();
		SIFVersion adkVersion = ADK.getSIFVersion();
		if (fElementDef != parentDef && candidateDef.isSupported(adkVersion)) {
			// Fixup the ElementRef of the child. For example, if this were a
			// StudentPersonal and the child were a Name, we would reassign its
			// ElementRef to be ADK.DTD.STUDENTPERSONAL_NAME

			// We also need to redefine the ElementRef to be
			// ADK.DTD.STUDENTPERSONAL_NAME.
			// This will allow it to be written in the proper sequence in
			// versions of SIF
			// in which it is collapsed

			// NOTE: Eric Petersen: Prior to build 1.1.0.31, the following line
			// used
			// to use o.fElementDef.name(...) instead of o.fElementDef.tag(...)
			// However, a customer discovered that it did not result in proper
			// sequencing of StudentSchoolEnrollment/HomeRoom elements, because
			// the tag name of this element is "Homeroom" but the name is
			// "HomeRoom",
			// with a capital R. Thus, the line below would never find
			// "StudentSchoolEnrollment_Homeroom". So this line was changed to
			// use the tag() instead of the name(). So far no side-effects have
			// been noticed.
			String tag = candidateDef.tag(adkVersion);
			ElementDef implDef = ADK.DTD().lookupElementDef(fElementDef, tag);
			if (implDef != null) {
				candidate.fElementDef = implDef;
			}
		}

	}

	/**
	 * Adds the specified children as an array to the SIFElement object.
	 * <p>
	 * All existing children that are defined as the same type as the ElementDef
	 * parameter are removed and replaced with this list. Calling this method
	 * with the Element[] parameter set to null removes all children.
	 * 
	 * @param id
	 *            The type of element that is being added
	 * @param children
	 *            The elements that are being added, all of which are defined
	 *            with the same ElementDef
	 * @exception IllegalArgumentException
	 *                thrown if either parameter is null
	 */
	public void setChildren(ElementDef id, SIFElement[] children) {
		if (id == null) {
			throw new IllegalArgumentException("Parameters cannot be null: id=" + id + ", children={1}" + children);
		}

		// First, remove all children of this type from the list
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			// Go through the vector in reverse order, removing any children of
			// this type
			for (int i = v.size() - 1; i >= 0; i--) {
				SIFElement child = v.get(i);
				if (((ElementDefImpl) child.fElementDef).internalName().equals(((ElementDefImpl) id).internalName())) {
					v.remove(i);
				}
			}
			// Successfully cleared the list
			if (children == null) {
				return;
			}

			// Add any children that were passed in
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null)
					continue;

				if (children[i].fParent != null && children[i].fParent != this) {
					throw new IllegalStateException("Element \"" + children[i].fElementDef.name() + "\" is already a child of another element");
				}

				children[i].fParent = this;
				children[i].fElementDef = id;
				v.add(children[i]);
			}
		}
	}

	// /**
	// * Asserts that all specified parameter arguments are not null
	// * @param params
	// * @param paramNames
	// */
	// private void assertParametersNotNull( Object[] params, String[]
	// paramNames ) {
	// boolean hasNulls = false;
	// // Do a quick check
	// for( int a= 0; a< params.length; ){
	// if( params[a] == null ){
	// hasNulls = true;
	// break;
	// }
	// }
	// if( !hasNulls ){
	// return;
	// }
	//
	// // The code only reaches this point in exceptional cases. We're going to
	// // throw an exception because one or more parameters is null;
	// // Not sure if this helps Java optimize this method or not, but I'm
	// putting
	// // the work into an If block so that none of the variables are seen or
	// initialized
	// // unless this case is true;
	// if( hasNulls ){
	// StringBuilder errorMessage = new StringBuilder();
	// errorMessage.append( "Parameter(s) cannot be null: " );
	// for( int a= 0; a< params.length; ){
	// if( params[a] == null ){
	// // Guard against invalid paramNames argument
	// if( paramNames != null && paramNames.length >= a+1 ){
	//
	// } else {
	//
	// }
	// }
	// }
	// throw new IllegalArgumentException( "Parameters cannot be null: id=" + id
	// + ", children={1}" + children );
	// }
	// }

	/**
	 * Removes a child object
	 * <p>
	 * 
	 * @param child
	 *            The child element
	 * @return True if the child was removed from this element, otherwise false
	 */
	public Boolean removeChild(SIFElement child) {
		if (child != null) {
			List<SIFElement> v = _childList();
			synchronized (fSyncLock) {
				if (v.remove(child)) {
					child.fParent = null;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes a child object identified by its ElementDef and key
	 * <p>
	 * This method removes only the first element it finds that matches the
	 * ElementDef and key.
	 * <p>
	 * 
	 * @param id
	 *            The ElementDef constant that identifies the type of child to
	 *            remove
	 * @param key
	 *            The element key value that identifies the specific child to
	 *            remove
	 * @return True if the child was removed from this element, otherwise false
	 */
	public boolean removeChild(ElementDef id, String key) {
		if (id == null) {
			return false;
		}
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			for (SIFElement child : v) {
				if (((ElementDefImpl) child.fElementDef).internalName().equals(((ElementDefImpl) id).internalName()) && (key == null || (child.getKey() != null && child.getKey().equals(key)))) {
					boolean removed = v.remove(child);
					if (removed) {
						child.fParent = null;
					}
					return removed;
				}
			}
		}
		return false;
	}

	/**
	 * Removes a child object identified by its ElementDef
	 * <p>
	 * This method removes only the first element it finds that matches the
	 * ElementDef
	 * <p>
	 * 
	 * @param id
	 *            The ElementDef constant that identifies the type of child to
	 *            remove
	 * @return True if the child was removed from this element, otherwise false
	 */
	public boolean removeChild(ElementDef id) {
		if (id == null) {
			return false;
		}
		return removeChild(id, (String) null);
	}

	/**
	 * Removes a child object identified by its ElementDef and complex key This
	 * method removes only the first element it finds that matches the
	 * ElementDef and key.
	 * <p>
	 * 
	 * @param id
	 *            The ElementDef constant that identifies the type of child to
	 *            remove
	 * @param complexKey
	 *            The complex key values that, taken together, identify the
	 *            specific child to remove
	 * @return True if the child was removed from this element, otherwise false
	 */
	public Boolean removeChild(ElementDef id, String[] complexKey) {
		if (id == null || complexKey == null) {
			return false;
		}

		StringBuilder b = new StringBuilder(complexKey[0]);
		for (int i = 1; i < complexKey.length; i++) {
			b.append(".");
			b.append(complexKey[i]);
		}

		return removeChild(id, b.toString());
	}

	/**
	 * Gets all child objects
	 * <p>
	 * 
	 * @return An array of all SIFElement children
	 */
	public SIFElement[] getChildren() {
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			SIFElement[] arr = new SIFElement[v.size()];
			return v.toArray(arr);
		}
	}

	/**
	 * Gets all child objects as an unmodifiable list
	 * <p>
	 * 
	 * @return An array of all SIFElement children
	 */
	public List<SIFElement> getChildList() {
		return Collections.unmodifiableList(_childList());
	}

	/**
	 * Gets all child objects with a matching ElementDef
	 * <p>
	 * 
	 * @param id
	 *            An ElementDef defined by the SIFDTD class to uniquely identify
	 *            this field
	 * @return An unsorted array of the SIFElements that have a matching
	 *         ElementDef
	 */
	public SIFElement[] getChildren(ElementDef id) {
		List<? extends SIFElement> match = getChildList(id);
		SIFElement[] children = new SIFElement[match.size()];
		match.toArray(children);
		return children;
	}

	/**
	 * Gets all child objects with a matching ElementDef
	 * <p>
	 * 
	 * @param id
	 *            An ElementDef defined by the SIFDTD class to uniquely identify
	 *            this field
	 * @return A Vector of the SIFElements that have a matching ElementDef
	 */
	public List<SIFElement> getChildList(ElementDef id) {
		List<SIFElement> v = _childList();
		List<SIFElement> match = new ArrayList<SIFElement>();
		synchronized (fSyncLock) {
			for (SIFElement child : v) {
				if (((ElementDefImpl) child.fElementDef).internalName().equals(((ElementDefImpl) id).internalName()))
					match.add(child);
			}
		}
		return match;
	}

	/**
	 * Gets all child objects with a matching version-independent element name
	 * <p>
	 * 
	 * @param name
	 *            The version-independent name of an element. Note the name is
	 *            not necessarily the same as the element tag
	 * @return A Vector of the SIFElements that have a matching element name
	 */
	public List<SIFElement> getChildList(String name) {
		List<SIFElement> v = _childList();
		List<SIFElement> match = new ArrayList<SIFElement>();
		synchronized (fSyncLock) {
			for (SIFElement o : v) {
				if (((ElementDefImpl) o.fElementDef).internalName().equals(name))
					match.add(o);
			}
		}
		return match;
	}

	/**
	 * Gets the child object with the matching ElementDef
	 * <p>
	 * 
	 * @param id
	 *            A ElementDef defined by the SIFDTD class to uniquely identify
	 *            this field
	 * @return The SIFElement that has a matching ElementDef, or null if none
	 *         found
	 */
	public SIFElement getChild(ElementDef id) {
		return getChild(id, (String) null);
	}

	/**
	 * Gets the child object with the matching ElementDef and key
	 * <p>
	 * 
	 * @param id
	 *            A ElementDef defined by the SIFDTD class to uniquely identify
	 *            this field
	 * @param key
	 *            The key to match
	 * @return The SIFElement that has a matching ElementDef and key, or null if
	 *         no matches found
	 */
	public SIFElement getChild(ElementDef id, String key) {
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			for (SIFElement o : v) {
				if (((ElementDefImpl) o.fElementDef).internalName().equals(((ElementDefImpl) id).internalName()) && (key == null || (key.equals(o.getKey()))))
					return o;
			}
		}
		return null;
	}

	/**
	 * Gets the child object with the matching ElementDef
	 * <p>
	 * 
	 * @param tag
	 *            The SIF 2.0 XML Tag name of the child
	 * @return The SIFElement that has a matching ElementDef, or null if none
	 *         found
	 */
	public SIFElement getChild(String tag) {
		List<SIFElement> v = _childList();
		synchronized (fSyncLock) {
			for (SIFElement o : v) {
				// this is a special case. We're searching for the child element
				// that has an version-independent internal name equal to "tag",
				// which is a version-dependent string, so we need to compare on
				// the ElementDef differently than usual. Note this method is
				// currently only used by the SIFDTD._xpath method, which uses
				// version-dependent tag names specified in the XPath query
				// string to match SIFElements in memory.
				//
				String cmp = null;
				ElementDef def = o.fElementDef;
				if (def instanceof ElementDefAlias)
					cmp = ((ElementDefAlias) def).internalName();
				else
					cmp = def.name();

				if (cmp.equals(tag))
					return o;
			}
		}

		return null;
	}

	/**
	 * Returns the number of children
	 * <p>
	 * 
	 * @return The count of all children
	 */
	public int getChildCount() {
		List v = _childList();
		synchronized (fSyncLock) {
			return v.size();
		}
	}

	/**
	 * Sets the text value of this element if applicable. The text value will be
	 * parsed into the native datatype of the the element.
	 * <p>
	 * The formatter used for parsing by default is the SIF 1.x formatter, which
	 * means this value must be able to be parsed using SIF 1.x formatting
	 * rules. To change the format used for Text values on elements, set the
	 * {@link ADK#setTextFormatter(SIFFormatter)} property
	 * 
	 * @param value
	 *            The text value of this element (e.g.
	 *            <code>&lt;element&gt;<i>text</i>&lt;element&gt;</code>)
	 * @throws NumberFormatException
	 *             if the value being set cannot be parsed into the datatype
	 *             being stored for this field
	 */
	public void setTextValue(String value) {

		if (value == null) {
			removeField(this.fElementDef);
		}

		SIFTypeConverter converter = getTextTypeConverter();
		SIFSimpleType typedValue = null;
		try {
			typedValue = converter.parse(ADK.getTextFormatter(), value);
		} catch (ADKParsingException adkpe) {
			throw new NumberFormatException(adkpe.getMessage());
		}
		setField(typedValue.createField(this, this.fElementDef));
	}

	/**
	 * Gets the TypeConverter to use for text values for this element
	 * 
	 * @return The TypeConverter that is used for this SIFElement. If none is
	 *         defined, the ADK automatically assumes it is a string type
	 */
	private SIFTypeConverter getTextTypeConverter() {
		SIFTypeConverter converter = this.fElementDef.getTypeConverter();
		if (converter == null) {
			// TODO: Should we not allows this in "Strict" mode?
			converter = SIFTypeConverters.STRING;
		}
		return converter;
	}

	/**
	 * Sets the SIF strongly-typed value of this element
	 * 
	 * @param value
	 *            The value of this element as a <c>SIFSimpleType</c>
	 */
	public void setSIFValue(SIFSimpleType value) {
		setField(value.createField(this, this.fElementDef));
	}

	/**
	 * Gets the SIF strongly-typed value of this element
	 * 
	 * @return The value of this element as a <c>SIFSimpleType</c>
	 */
	public SIFSimpleType getSIFValue() {
		SimpleField value = getField(this.fElementDef);
		if (value != null) {
			return value.getSIFValue();
		}
		return null;
	}

	/**
	 * Gets the text value of this element if applicable. The default format of
	 * the text value is the SIF 1.x format
	 * <p>
	 * To change the format used for Text values on elements, set the
	 * {@link ADK#setTextFormatter(SIFFormatter)} property
	 * 
	 * @return The text value of this element (e.g.
	 *         <code>&lt;element&gt;<b><i>text</i></b>&lt;/element&gt;</code>)
	 */
	public String getTextValue() {
		return getFieldValue(this.fElementDef);
	}

	/**
	 * Does this element have a text value?
	 * <p>
	 * 
	 * @return true if this element has a text value (e.g.
	 *         <code>&lt;element&gt;<b><i>text</i></b>&lt;/element&gt;</code>)
	 */
	public boolean hasTextValue() {
		return getField(fElementDef) != null;
	}

	/**
	 * Gets a field's value and sequence number
	 * <p>
	 * 
	 * @param id
	 *            The field's ElementDef object defined by the SIFDTD class.
	 * @return A SimpleField object containing the field's value and sequence
	 *         number, or null if the field has no value
	 * @see #getFieldValue
	 * @see #getField(String)
	 */
	public SimpleField getField(ElementDef id) {
		if (fFields != null) {
			synchronized (fFields) {
				return fFields.get(id.name());
			}
		}
		return null;
	}

	/**
	 * Gets a field's value and sequence number
	 * <p>
	 * 
	 * @param name
	 *            The name of the field's ElementDef object defined by the
	 *            SIFDTD class.
	 * @return A SimpleField object containing the field's value and sequence
	 *         number, or null if the field has no value
	 * @see #getFieldValue
	 * @see #getField(ElementDef)
	 */
	public SimpleField getField(String name) {
		if (fFields != null) {
			synchronized (fFields) {
				return fFields.get(name);
			}
		}

		return null;
	}

	/**
	 * Gets a field's value as a String
	 * <p>
	 * 
	 * @param id
	 *            The field's ElementDef object defined by the SIFDTD class.
	 * @return The current value of the field as a String, or null if the field
	 *         has no value
	 * @see #getField(ElementDef)
	 */
	public String getFieldValue(ElementDef id) {
		SimpleField v = getField(id);
		if (v != null) {
			return v.getTextValue();
		}
		return null;
	}

	protected Object getSIFSimpleFieldValue(ElementDef id) {
		SimpleField v = getField(id);
		if (v != null) {
			return v.getValue();
		}
		return null;
	}

	/**
	 * Sets an integer field's value
	 * <p>
	 * 
	 * @param id
	 *            The field definition object
	 * @param value
	 *            The value to assign to the field
	 * @return The internal field object, returned as a convenience so the
	 *         caller can mark the field as dirty or empty by calling its
	 *         setDirty and setEmpty methods.
	 * @deprecated Please call {@link #setField(ElementDef, SIFSimpleType)}
	 */
	public SimpleField setField(ElementDef id, int value) {
		return setField(id, String.valueOf(value));
	}

	/**
	 * Sets a field's value
	 * <p>
	 * 
	 * @param id
	 *            The field definition object
	 * @param value
	 *            The value to assign to the field
	 * @return The internal field object, returned as a convenience so the
	 *         caller can mark the field as dirty or empty by calling its
	 *         setDirty and setEmpty methods.
	 */
	public SimpleField setField(ElementDef id, SIFSimpleType value) {
		assertElementDef(id);
		if (value == null) {
			removeField(id);
			return null;
		} else {
			SimpleField field = value.createField(this, id);
			setField(field);
			return field;
		}
	}

	/**
	 * Sets a field's value, after evaluating the raw data type
	 * <p>
	 * This method is a convenience method that can be used by property set
	 * methods.
	 * 
	 * @param id
	 *            The field definition object
	 * @param wrapped
	 *            The SIFSimpleType value to assign to the field
	 * @param unwrappedValue
	 *            The raw, java value that was set. If this value is null, the
	 *            field will be removed, rather than added
	 * @return The internal field object, returned as a convenience so the
	 *         caller can mark the field as dirty or empty by calling its
	 *         setDirty and setEmpty methods.
	 */
	protected SimpleField setFieldValue(ElementDef id, SIFSimpleType wrappedValue, Object unwrappedValue) {
		if (unwrappedValue == null) {
			removeField(id);
			return null;
		}
		return setField(id, wrappedValue);
	}

	/**
	 * Asserts the ElementDef argument for methods that accept it as a parameter
	 * 
	 * @param id
	 *            The ElementDef to assert
	 */
	protected void assertElementDef(ElementDef id) {

		if (!ADK.isInitialized())
			throw new InternalError("The ADK is not initialized");

		if (id == null) {
			throw new IllegalArgumentException("ElementDef cannot be null");
		}

	}

	/**
	 * Sets a field's value
	 * <p>
	 * 
	 * @param id
	 *            The field definition object
	 * @param value
	 *            The value to assign to the field
	 * @return The internal field object, returned as a convenience so the
	 *         caller can mark the field as dirty or empty by calling its
	 *         setDirty and setEmpty methods.
	 */
	public SimpleField setField(ElementDef id, String value) {
		return setFieldValue(id, new SIFString(value), value);
	}

	/**
	 * Sets the value of an attribute or simple text element on this SIFElement
	 * 
	 * @param field
	 */
	public void setField(SimpleField field) {
		if (fFields == null) {
			// TODO: Tune the size of the field hashmap, based on metadata
			fFields = new HashMap<String, SimpleField>(2);
		}
		synchronized (fFields) {
			fFields.put(field.getElementDef().name(), field);
			field.setParent(this);
		}
	}

	/**
	 * Removes the field with the specified ID
	 * 
	 * @param id
	 */
	protected void removeField(ElementDef id) {
		if (fFields == null) {
			return;
		}
		synchronized (fFields) {
			fFields.remove(id.name());
		}
	}

	/**
	 * Gets all fields for this object. The returned list will be a copy of the
	 * underlying list of fields
	 * <p>
	 * 
	 * @return An array of unsorted SimpleField objects
	 */
	public List<SimpleField> getFields() {
		List<SimpleField> arr = new ArrayList<SimpleField>();
		if (fFields != null) {
			synchronized (fFields) {
				arr.addAll(fFields.values());
			}
		}
		return arr;
	}

	/**
	 * Gets the number of fields for this object
	 * 
	 * @return The number of fields that are currently set on this object
	 */
	public int getFieldCount() {
		if (fFields != null) {
			synchronized (fFields) {
				return fFields.size();
			}
		}

		return 0;
	}

	/**
	 * Gets an ordered list of all child elements and fields that are in a
	 * changed state and do not have a null value. Attributes are not included.
	 * Elements are ordered by sequence number according to the version of SIF
	 * effective for this object.
	 * <p>
	 * 
	 * @return An Element array comprised of all child SIFElement and
	 *         SimpleField objects ordered according to sequence number. An
	 *         empty array is returned if there are no child SIFElements or
	 *         fields (e.g. if this SIFElement has a text value as its content.)
	 * 
	 * @see #effectiveSIFVersion
	 */
	public List<Element> getContent() {
		return getContent(effectiveSIFVersion());
	}

	/**
	 * Gets an ordered list of all child elements and fields that are in a
	 * changed state and do not have a null value. Attributes are not included.
	 * Elements are ordered by sequence number according to the specified
	 * version of SIF.
	 * <p>
	 * 
	 * @param version
	 *            The version of SIF to use when ordering the elements.
	 * 
	 * @return An Element array comprised of all child SIFElement and
	 *         SimpleField objects ordered according to sequence number. An
	 *         empty array is returned if there are no child SIFElements or
	 *         fields (e.g. if this SIFElement has a text value as its content.)
	 */
	public List<Element> getContent(SIFVersion version) {
		return ADK.DTD().getFormatter(version).getContent(this, version);
	}

	/**
	 * Sets this element and each of its children to the specified empty state.
	 * An object in the empty state will be written to an XML stream as an empty
	 * element with no children.
	 * <p>
	 * 
	 * @param empty
	 *            true to set the empty state, false to clear it
	 */
	public void setEmpty(boolean empty) {
		super.setEmpty(empty);
		List<SIFElement> children = _childList();
		synchronized (fSyncLock) {
			for (SIFElement e : children) {
				e.setEmpty(empty);
			}
		}
	}

	/**
	 * Sets this element and each of its children to the specified changed
	 * state. An object in the changed state will be written to the XML stream
	 * when a SIF message is rendered; objects that are not changed will be
	 * excluded.
	 * <p>
	 * 
	 * @param changed
	 *            true to set the changed state, false to clear it
	 */
	public void setChanged(boolean changed) {
		super.setChanged(changed);
		synchronized (fSyncLock) {
			if (fChildren != null) {
				for (SIFElement e : fChildren) {
					e.setChanged(changed);
				}
			}
			if (fFields != null) {
				for (SimpleField field : fFields.values()) {
					field.setChanged(changed);
				}
			}
		}
	}

	/**
	 * Compares all child elements and attributes of this Element with that of
	 * another Element. Any attributes or elements of the target that have a
	 * different text value from the corresponding attribute or element in this
	 * object, or that appear in one graph but not in the other, are returned in
	 * an array. Repeatable elements are considered the same object only if
	 * their <i>keys</i> match.
	 * <p>
	 * 
	 * The comparision is exclusive of the source and target objects; that is,
	 * their text values are not included in the comparision. This method is
	 * typically called on top-level SIF Data Objects such as StudentPersonal,
	 * LibraryPatronStatus, CircTx, BusInfo, etc.
	 * <p>
	 * 
	 * The result of the comparision is returned as a dual-dimensioned array,
	 * where the first element in the array represents the source object and the
	 * second element represents the target object. Each of these arrays will
	 * contain the same number of entries. Within each array, each slot consists
	 * of a <code>SIFElement</code> or <code>SimpleField</code> object. In the
	 * ADK, the SIFElement class encapsulates complex elements such as Name,
	 * PhoneNumber, and Demographics while the SimpleField class encapsulates
	 * elements with no children such as Name/FirstName and Name/LastName, or
	 * attributes such as StudentPersonal/@RefId. If a given element or
	 * attribute appears in one graph but not in the other, its slot in the
	 * array will contain a null value.
	 * <p>
	 * 
	 * For example, the arrays returned by this method might consist of the
	 * following:
	 * <p>
	 * 
	 * <table>
	 * 
	 * <tr>
	 * <td><code><b>Array[0][0..5]</b></td>
	 * <td><code><b>Array[1][0..5]</b></td>
	 * 
	 * <tr>
	 * <td><code>@RefId='AB123...'</code></td>
	 * <td><code>@RefId='CCD91...'</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>&lt;LastName&gt;Johnson&lt;/LastName></code></td>
	 * <td><code>&lt;LastName>Johnsen&lt;/LastName&gt;</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>&lt;OtherId Type='06'&gt;1004&lt;/OtherId></code></td>
	 * <td><code>null</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>&lt;OtherId Type='ZZ'&gt;SCHOOL:997&lt;/OtherId></code></td>
	 * <td><code>null</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>null</code></td>
	 * <td><code>&lt;OtherId Type='ZZ'&gt;BARCODE:P12345&lt;/OtherId&gt;</code></td>
	 * </tr>
	 * 
	 * </table>
	 * <p>
	 * 
	 * In this example, the RefId attribute (a SimpleField instance) has a
	 * different value in the source than in the target, as does the LastName
	 * element (a SIFElement instance). Two OtherId elements of type '06' and
	 * 'ZZ' appeared in the source but not in the target, so they are included
	 * in Array[0][2] and Array[0][3] but have a corresponding null value in
	 * Array[1][2] and Array[1][3]. Finally, one OtherId element of type 'ZZ'
	 * appeared in the target but not in the source, so it appears in
	 * Array[1][5] but has a corresponding null value in Array[0][5].
	 * <p>
	 * 
	 * For examples of how this method is used, please consult the SIFDiff and
	 * SchoolInfoProvider ADK Example agents.
	 * <p>
	 * 
	 * @param target
	 *            The Element to be compared
	 * 
	 * @return An dual-dimensioned array of Elements constituting the
	 *         differences between this Element and the comparision Element. The
	 *         size of these arrays will always be equals. If there are no
	 *         elements in either array, this Element and the comparision
	 *         Element have identical content.
	 * 
	 * @exception IllegalArgumentException
	 *                is thrown if this Element and the target are not of the
	 *                same type; that is, they do not have the same ElementDef
	 *                value. For example, trying to compare a
	 *                <code>SIFDTD.STUDENTPERSONAL</code> element with a
	 *                <code>SIFDTD.BUSINFO</code> element will result in an
	 *                exception.
	 */
	public Element[][] compareGraphTo(SIFElement target) {
		if (target.fElementDef != fElementDef)
			throw new IllegalArgumentException("Element types differ (cannot compare " + fElementDef.getSQPPath(getSIFVersion()) + " to " + target.fElementDef.getSQPPath(target.getSIFVersion()));

		List<Element> srcDiffs = new ArrayList<Element>();
		List<Element> dstDiffs = new ArrayList<Element>();

		_compareGraphTo(effectiveSIFVersion(), target, srcDiffs, dstDiffs, false);
		target._compareGraphTo(effectiveSIFVersion(), this, srcDiffs, dstDiffs, true);

		Element[] src = new Element[srcDiffs.size()];
		srcDiffs.toArray(src);

		Element[] dst = new Element[dstDiffs.size()];
		dstDiffs.toArray(dst);

		return new Element[][] { src, dst };
	}

	private void _compareGraphTo(SIFVersion ver, SIFElement target, List<Element> srcMap, List<Element> dstMap, boolean isDst) {
		List<Element> diffs = isDst ? dstMap : srcMap;
		List<Element> odiffs = isDst ? srcMap : dstMap;

		List<SIFElement> v = _childList();

		if (fFields != null) {
			synchronized (fFields) {
				for (Iterator it = fFields.keySet().iterator(); it.hasNext();) {
					String name = (String) it.next();
					if (name.length() == 0)
						continue;

					SimpleField fld = fFields.get(name);
					SimpleField fldComp = target.getField(name);
					if (fldComp != null) {
						if (fld.compareTo(fldComp) != 0) {
							// System.out.println("Field differs: " +
							// fld.fElementDef.getSDOPath() + " = " +
							// fld.getTextValue() );
							if (!diffs.contains(fld)) {
								diffs.add(fld);
								odiffs.add(fldComp);
							}
						}
					} else if (!isDst) {
						if (!diffs.contains(fld)) {
							diffs.add(fld);
							odiffs.add(null);
						}
					}
				}
			}
		}

		// Compare child elements
		synchronized (fSyncLock) {
			for (SIFElement xCh : v) {
				SIFElement xComp = xCh.fElementDef.isRepeatable(ver) ? target.getChild(xCh.fElementDef, xCh.getKey()) : target.getChild(xCh.fElementDef);

				if (xComp != null) {
					if (xCh.compareTo(xComp) != 0) {
						// System.out.println("Element differs: " +
						// xCh.fElementDef.name() + " - " + xCh.getClass() );
						if (!diffs.contains(xCh)) {
							diffs.add(xCh);
							odiffs.add(xComp);
						}
					}
					// TODO: Is there anything that can be done with this
					// warning?
					xCh._compareGraphTo(ver, xComp, srcMap, dstMap, false);
				} else if (!isDst) {
					// System.out.println("Element in "+(isDst?"target":"source")+" but not in "+(isDst?"source":"target")+": "
					// + xCh.fElementDef.name() + " (srcKey=" + xCh.getKey() +
					// ")" );
					if (!diffs.contains(xCh)) {
						diffs.add(xCh);
						odiffs.add(null);
					}
				}
			}
		}
	}

	/**
	 * Creates an instance of a SIFElement from its ID
	 * 
	 * @param id
	 *            The metadata element that identifies
	 * @param parent
	 *            The parent Metadata element
	 * @return The newly-created child
	 * @throws ADKSchemaException
	 */
	public static SIFElement create(SIFElement parent, ElementDef id) throws ADKSchemaException {
		SIFElement element = null;
		try {
			Class clazz = null;
			if ( id == null ) {
				//null pointer exceptions thrown in a catch statement don't seem to generate any stack trace.  I'd rather do it here.
				throw new NullPointerException("ElementDef in SIFElement.create cannot be null, parent: " + parent);
			}
			try {
				clazz = Class.forName(id.getFQClassName());
			} catch (Exception e) {
				// ignore
			}
			if (clazz == null) {
				// JEN added - try parent package
				String packageName = parent.getClass().getPackage().getName();
				String fullClassName = packageName + "." + id.getClassName();
				clazz = Class.forName(fullClassName);

				if (clazz == null) {
					throw new ADKSchemaException("Could not create an instance of " + id.getFQClassName() + " to wrap a " + id.name() + " object because that class doesn't exist", null);
				}
			}
			element = (SIFElement) clazz.newInstance();
		} catch (Exception e) {
			throw new ADKSchemaException("Could not create an instance of " + id.getFQClassName() + " to wrap a " + id.name() + ":" + e, null, e);
		}

		element.setElementDef(id);
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			SIFElement elementCopy = null;

			// Most SIFElement subclasses should have a constructor with no
			// arguments that
			// sets the ElementDef automatically. If not, we need to find the
			// constructor with the ElementDef argument
			for (Constructor c : this.getClass().getConstructors()) {
				Class[] parameterTypes = c.getParameterTypes();
				if (parameterTypes.length == 0) {
					// A Zero-Parameter constructor
					elementCopy = (SIFElement) c.newInstance();
					break;
				} else if (parameterTypes.length == 1 && parameterTypes[0] == ElementDef.class) {
					elementCopy = (SIFElement) c.newInstance(fElementDef);
					break;
				}
			}

			if (elementCopy == null) {
				throw new CloneNotSupportedException("Unable to find constructor suitable for cloning");
			}

			elementCopy.fId = this.fId;
			if (fFields != null) {
				for (Map.Entry<String, SimpleField> entry : fFields.entrySet()) {
					SimpleField fieldCopy = (SimpleField) entry.getValue().clone();
					elementCopy.setField(fieldCopy);
				}
			}

			if (fChildren != null) {
				for (SIFElement childElement : fChildren) {
					elementCopy.addChild((SIFElement) childElement.clone());
				}
			}

			return elementCopy;
		} catch (CloneNotSupportedException re) {
			throw re;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception iae) {
			throw new CloneNotSupportedException(iae.getMessage());
		}

	}

	public String toString() {
		String retval = "";
		if (fElementDef != null)
			retval = fElementDef.tag(ADK.getSIFVersion());
		if (getTextValue() != null)
			retval += ":" + getTextValue();
		if (getFields() != null)
			retval += appendFields(this.getFields());

		// if (getChildren() != null)
		// retval += appendElements(getChildren());

		if (getChildCount() > 0)
			retval += appendElements(getChildren());

		return retval;
	}

	private String appendElements(SIFElement[] elements) {
		String retval = "";
		for (int i = 0; i < elements.length; ++i) {
			SIFElement element = elements[i];
			// retval += "-" + element.fElementDef.tag(ADK.getSIFVersion());
			retval += "-" + element.fElementDef.name();
			if (element.getFields() != null)
				retval += appendFields(element.getFields());

			// if (element.getChildren() != null)
			// retval += appendElements(element.getChildren());

			if (element.getChildCount() > 0)
				retval += appendElements(element.getChildren());
		}
		return retval;
	}

	private String appendFields(List<SimpleField> fields) {
		int count = 1;
		String retval = "(";
		for (SimpleField field : fields) {
			retval += field.getElementDef().tag(ADK.getSIFVersion());
			retval += "=";
			retval += field.getTextValue();
			if (count < fields.size())
				retval += ",";
			++count;
		}
		retval += ")";
		return retval;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		//
		// Write the path to the ElementDef
		//
		String path = fElementDef.getSDOPath();
		out.writeUTF(path);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		// Reset the transient fields ( They are not initialized )
		fSyncLock = new Object();

		//
		// Read the path to the ElementDef
		//
		String path = in.readUTF();
		ElementDef foundElementDef = null;
		if (path.length() > 0) {
			foundElementDef = ADK.DTD().lookupElementDef(path);
		}
		if (foundElementDef == null) {
			// TODO: MLW - I consider this a hack. On deserialization, the
			// no-arguments constructor is
			// not called. Also, SIFElements that were serialized without a
			// parent but normally do have a parent
			// are not returned by the lookupElementDef() call above. To fix
			// this, I instantiate
			// a new object of this type, and then see what the elementdef of
			// that object is.
			try {
				SIFElement instanceOfThisType = getClass().newInstance();
				foundElementDef = instanceOfThisType.getElementDef();
			} catch (InstantiationException ex) {
				throw new RuntimeException("Deserialization failed: " + ex.getMessage(), ex);
			} catch (IllegalAccessException ex) {
				throw new RuntimeException("Deserialization failed" + ex.getMessage(), ex);
			}
		}
		fElementDef = foundElementDef;
	}

}
