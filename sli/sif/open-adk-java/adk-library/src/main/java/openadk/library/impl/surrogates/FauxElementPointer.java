//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import openadk.library.Element;
import openadk.library.SIFSimpleType;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;




/**
 * Represents a node pointer that points to a path supported by the ADK from SIF 1.x, but
 * for which there is no actual element. For Example, the GradYear/@Type attribute exists in SIF1.x,
 * but doesn't exist in SIF 2.x. FauxElementPointer could be built based on a SIF 1.x XPath, but
 * will actually point to the in-memory SIF 2.x field.
 * @author Andy Elmhorst
 *
 */
abstract class FauxElementPointer extends NodePointer {

	private String fFieldName;
	protected FauxElementPointer(NodePointer parent, String fauxName ) {
		super(parent);
		fFieldName = fauxName;
	}
	@Override
	public boolean isCollection() {
		return false;
	}
	@Override
	public int getLength() {
		return 1;
	}
	@Override
	public QName getName() {
		return new QName( null, fFieldName );
	}
	@Override
	public int compareChildNodePointers(NodePointer arg0, NodePointer arg1) {
		return 0;
	}

	protected void setFieldValue(Element field, Object value) {
		SIFSimpleType sifValue = null;
		if( value instanceof SIFSimpleType ){
			sifValue = (SIFSimpleType)value;
		} else {
			sifValue = field.getElementDef().getTypeConverter().getSIFSimpleType( value );
		}

		field.setSIFValue( sifValue );
	}

}
