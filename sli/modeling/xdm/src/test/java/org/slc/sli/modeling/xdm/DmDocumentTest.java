package org.slc.sli.modeling.xdm;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DmDocumentTest {

	@Test
	public void testConstructorAndGetters() {
		
		List<DmNode> dmNodes = new ArrayList<DmNode>();
		dmNodes.add(null);
		dmNodes.add(null);
		
		DmDocument dmDocument = new DmDocument(dmNodes);
		
		assertTrue(dmDocument.getChildAxis().equals(dmNodes));
		assertTrue(dmDocument.getName() == DmDocument.NO_NAME);
	}

	@Test (expected = NullPointerException.class)
	public void testNullListForConstructorThrowsException() {
		new DmDocument(null);
	}
	
	@Test (expected = UnsupportedOperationException.class)
	public void testGetStringValue() {
		
		DmDocument dmDocument = new DmDocument(new ArrayList<DmNode>());
		dmDocument.getStringValue();
	}

}
