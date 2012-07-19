//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import openadk.library.*;
import openadk.library.impl.surrogates.RenderSurrogate;

import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * Represents a NodePointer for a SimpleField
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
class SimpleFieldPointer extends ADKElementPointer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3881828686990309752L;


	/**
	 * Creates an instance of SimpleFieldPointer
	 * 
	 * @param pointerParent
	 *            The parent SIFElement pointer
	 * @param field
	 *            the field this pointer represents
	 */
	private SimpleFieldPointer(SIFElementPointer pointerParent, SimpleField field) {
		this(pointerParent, field, pointerParent.getVersion());
	}
	
	public static NodePointer create( SIFElementPointer pointerParent, SimpleField field ){
		if( pointerParent.isLegacyVersion() ){
			ElementDef fieldDef = field.getElementDef();
			RenderSurrogate rs = fieldDef.getVersionInfo( pointerParent.getVersion() ).getSurrogate();
			if( rs != null ){
				return rs.createNodePointer( pointerParent, field, pointerParent.getVersion() );
			}
		}
		return new SimpleFieldPointer( pointerParent, field );
	}


	public static NodePointer create( SIFExtendedElementPointer pointerParent, SimpleField field ){
		if( pointerParent.isLegacyVersion() ){
			ElementDef fieldDef = field.getElementDef();
			RenderSurrogate rs = fieldDef.getVersionInfo( pointerParent.getVersion() ).getSurrogate();
			if( rs != null ){
				return rs.createNodePointer( pointerParent, field, pointerParent.getVersion() );
			}
		}
		return new SimpleFieldPointer( pointerParent, field );
	}
	
	
	private SimpleFieldPointer( SIFExtendedElementPointer pointerParent, SimpleField field )
	{
		super( pointerParent, field, pointerParent.getVersion() );
	}
	
	
	/**
	 * Creates an instance of SimpleFieldPointer
	 * 
	 * @param pointerParent
	 *            The parent SIFElement pointer
	 * @param field
	 *            The field this pointer represents
	 * @param version
	 *            The SIFVersion in effect
	 */
	public SimpleFieldPointer(SIFElementPointer pointerParent,
			SimpleField field, SIFVersion version) {
		super(pointerParent, field, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return true;
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
		return 0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.Pointer#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object rawValue) {
		SIFElementPointer immediateParent = (SIFElementPointer) getImmediateParentPointer();
		SIFElement parentElement = immediateParent.fSIFElement;
		SIFSimpleType sst = getSIFSimpleTypeValue( fElementDef, rawValue );
		parentElement.setField(fElementDef, sst);
	}

}
