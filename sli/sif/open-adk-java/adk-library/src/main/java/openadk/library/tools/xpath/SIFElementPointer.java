//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import openadk.library.*;
import openadk.library.impl.surrogates.RenderSurrogate;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.NamespaceResolver;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;


/**
 * Represents a NodePointer for a SIFElement
 *
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
public class SIFElementPointer extends ADKElementPointer {
	/**
	 *
	 */
	private static final long serialVersionUID = -2805505553878070975L;

	protected SIFElement fSIFElement;

	/**
	 * @param parentpointer
	 * @param element
	 * @param version
	 */
	public SIFElementPointer(NodePointer parentpointer, SIFElement element,
			SIFVersion version) {
		super(parentpointer, element, version);
		fSIFElement = element;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getDefaultNamespaceURI()
	 */
	@Override
	protected String getDefaultNamespaceURI() {
		String sifNSURI = getVersion().getXmlns();
		return sifNSURI;
	}

	/**
	 * @param parentPointer
	 * @param element
	 * @param version
	 * @return A NodePointer pointing to a SIFElement
	 */
	public static NodePointer create( NodePointer parentPointer, SIFElement element, SIFVersion version )
	{
		if( version.getMajor() < 2 ){
			ElementDef fieldDef = element.getElementDef();
			RenderSurrogate rs = fieldDef.getVersionInfo( version ).getSurrogate();
			if( rs != null ){
				return rs.createNodePointer( parentPointer, element, version );
			}
		}
		
		if ( "SIF_ExtendedElement".equals(element.tag()) )
			return new SIFExtendedElementPointer( parentPointer, (openadk.library.common.SIF_ExtendedElement)element, version );
		else 
			return new SIFElementPointer( parentPointer, element, version );
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#isCollection()
	 */
	@Override
	public boolean isCollection() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getLength()
	 */
	@Override
	public int getLength() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.Pointer#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object rawValue) {
		SIFSimpleType sst = getSIFSimpleTypeValue( fElementDef, rawValue );
		fSIFElement.setSIFValue( sst );

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#attributeIterator(org.apache.commons.jxpath.ri.QName)
	 */
	@Override
	public NodeIterator attributeIterator(QName name) {
//		SIFVersion version = getVersion();
//		if( version.getMajor() < 2 ){
//			// Look for a surrogate pointer if the SIF Version < 2
//			ElementDef def = ADK.DTD().lookupElementDef( fElementDef, name.getName() );
//			if( def != null ){
//				ElementVersionInfo evi = def.getVersionInfo( version );
//				if( evi != null ){
//					// Determine if a RenderSurrogate handles this path
//					RenderSurrogate surrogate = evi.getSurrogate();
//					if( surrogate != null ){
//						return surrogate.childIterator( this, name );
//					}
//				}
//			}
//		}
		return new ADKAttributeIterator(this, name);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#childIterator(org.apache.commons.jxpath.ri.compiler.NodeTest,
	 *      boolean, org.apache.commons.jxpath.ri.model.NodePointer)
	 */
	@Override
	public NodeIterator childIterator(NodeTest test, boolean reverse,
			NodePointer startWith) {

		ArrayList<Element> alist = new ArrayList<Element>();
		SIFVersion version = getVersion();
		// Do a cursory speed check
		if (test != null && test instanceof NodeNameTest) {
			NodeNameTest nnt = (NodeNameTest) test;
			if (!nnt.isWildcard()) {
				QName name = nnt.getNodeName();
				String localName = name.getName();
				// Look for a surrogate pointer if the SIF Version < 2
				ElementDef def = ADK.DTD().lookupElementDef( fElementDef, localName );
				if( def != null ){

					// Check to see if this ElementDef represents a field or complex type (SIFElement)
					if( def.isField() ){
						SimpleField sf = this.fSIFElement.getField(localName);
						if( sf != null ) {
							return new SingleNodeIterator( SimpleFieldPointer.create(this, sf) );
						}
					}
					else {
						List<SIFElement> children = this.fSIFElement.getChildList(localName);
						if (children.size() > 0) {
							alist.addAll(children);
						}
					}
				}
			}
		}

		if (alist.size() == 0) {

			// Get all of the Element fields and children that match the
			// NodeTest into a list
			SIFFormatter formatter = ADK.DTD().getFormatter( version );
			List<Element> elements = formatter.getContent(fSIFElement, version);
			for (Element e : elements) {
				if (testElement(e, test)) {
					alist.add(e);
				}
			}
		}

		if (alist.size() == 1) {
			Element singleChild = alist.get( 0 );
			if( singleChild instanceof SimpleField ){
				return new SingleNodeIterator( SimpleFieldPointer.create( this, (SimpleField)singleChild ) );
			} else {
				return new SingleNodeIterator( SIFElementPointer.create( this, (SIFElement)singleChild, version ) );
			}

		}

		int start = -1;
		if (startWith != null) {
			start = alist.indexOf(startWith.getNode());
		}

		if (start == -1) {
			if (reverse) {
				start = alist.size() - 1;
			} else {
				start = 0;
			}
		}

		if (reverse) {
			Collections.reverse(alist);
		}

		return new ADKElementIterator(this, alist);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#createChild(org.apache.commons.jxpath.JXPathContext,
	 *      org.apache.commons.jxpath.ri.QName, int)
	 */
	@Override
	public NodePointer createChild(JXPathContext context, QName name,
			int childIndex) {

		ElementDef subEleDef = getChildDef(name);
		SIFVersion version = getVersion();
		SIFFormatter formatter = ADK.DTD().getFormatter(version);

		// Check to see if this child has a render surrogate defined
		RenderSurrogate rs = subEleDef.getVersionInfo( version ).getSurrogate();
		if( rs != null ){
			return rs.createChild( this, formatter, version, context );
		}

		return createChildPointer(subEleDef, version, formatter);
	}

	/**
	 * Creates a NodePointer representing the specified child
	 * @param childDef
	 * @param version
	 * @param formatter
	 * @return
	 */
	public NodePointer createChildPointer(ElementDef childDef, SIFVersion version, SIFFormatter formatter) {
		if (childDef.isField()) {
			SIFSimpleType ssf = childDef.getTypeConverter().getSIFSimpleType( null );
			SimpleField sf = formatter.setField(fSIFElement, childDef, ssf, version);// fSIFElement.setField(sf);
			return SimpleFieldPointer.create( this, sf );
		} else {
			try {
				SIFElement newEle = SIFElement.create( this.fSIFElement, childDef );
				formatter.addChild(fSIFElement, newEle, version);
				if ( "SIF_ExtendedElement".equals(newEle.tag()) )
					return new SIFExtendedElementPointer( this, (openadk.library.common.SIF_ExtendedElement)newEle, version );
				else 
					return new SIFElementPointer( this, newEle, version );
			} catch (ADKSchemaException adkse) {
				throw new JXPathException(adkse.getMessage(), adkse);
			}
		}
	}

	/**
	 * Reutrns a child def with the requested name
	 * @param name The name to look up
	 * @return The ElementDef representing the requested name
	 */
	private ElementDef getChildDef(QName name) {
		String localName = name.getName();
		ElementDef subEleDef = ADK.DTD().lookupElementDef( fElementDef, localName );
		if (subEleDef == null) {
			subEleDef = ADK.DTD().lookupElementDef(localName);
			if (subEleDef == null)
				throw new JXPathException(localName
						+ " is not a recognized element of "
						+ fElementDef.tag(getVersion()));
		}
		return subEleDef;
	}

	/**
	 * Use by SIFXPathContext when determining if it can add a child
	 * or not.
	 * @param childName
	 * @return True if a child should be added, false if it already exists
	 */
	public AddChildDirective getAddChildDirective( QName childName ){
		
		
		if( fSIFElement.getChildCount() == 0 ){
			// There are no children. Don't even check for repeatability.
			// This child can be added.
			return AddChildDirective.ADD;
		}
		ElementDef candidate = getChildDef( childName );
		if( candidate.isField()  ){
			// Don't evaluate repeatability
			return AddChildDirective.ADD;
		}
		SIFElement instance = fSIFElement.getChild( candidate );
		if( instance == null ){
			// There are no siblings of this type. This child can be
			// added
			return AddChildDirective.ADD;
		}
		if( candidate.isRepeatable( getVersion() ) ){
			// This element is repeatable. This child can be added
			return AddChildDirective.ADD;
		}

		// A sibling exists, and the element is not repeatable
		return AddChildDirective.DONT_ADD_NOT_REPEATABLE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#createChild(org.apache.commons.jxpath.JXPathContext,
	 *      org.apache.commons.jxpath.ri.QName, int, java.lang.Object)
	 */
	@Override
	public NodePointer createChild(JXPathContext context, QName name,
			int childIndex, Object value) {
		NodePointer ptr = createChild(context, name, childIndex);
		ptr.setValue(value);
		return ptr;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#createAttribute(org.apache.commons.jxpath.JXPathContext,
	 *      org.apache.commons.jxpath.ri.QName)
	 */
	@Override
	public NodePointer createAttribute(JXPathContext context, QName name) {
		ElementDef subEleDef = getChildDef(name);

		if (subEleDef.isField()) {
			SIFSimpleType ssf = subEleDef.getTypeConverter().getSIFSimpleType(
					null);
			SimpleField sf = ssf.createField(fSIFElement, subEleDef);
			fSIFElement.setField(sf);
			return SimpleFieldPointer.create(this, sf);
		}

		throw new JXPathException(
				"Factory could not create a child node for path: " + asPath()
						+ "/" + name + "[" + (index + 1) + "]");

	}


	/**
	 * The set of values that can be returned from the <code>getChildAddDirective<code>
	 * method
	 *
	 */
	public enum AddChildDirective
	{
		/**
		 * It is OK to add this child
		 */
		ADD,
		/**
		 * Don't add this child because the element is not repeatable and
		 * another already exists as a child of this element
		 */
		DONT_ADD_NOT_REPEATABLE
	}

}
