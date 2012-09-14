package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.Type;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PsmClassTypeComparatorTest {
	
	@Test
	public void testEnumValues() {
		for (PsmDocumentComparator psmDocumentComparator : PsmDocumentComparator.values()) {
			assertEquals(PsmDocumentComparator.valueOf(psmDocumentComparator.toString()), psmDocumentComparator);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSingletonComparison() {
		Type type1 = mock(Type.class);
		Type type2 = mock(Type.class);

		when(type1.getName()).thenReturn("early");
		when(type2.getName()).thenReturn("late");
		
		PsmDocument<Type> psmDocument1 = mock(PsmDocument.class);
		PsmDocument<Type> psmDocument2 = mock(PsmDocument.class);
		
		when(psmDocument1.getType()).thenReturn(type1);
		when(psmDocument2.getType()).thenReturn(type2);
		
		PsmDocumentComparator comparator = PsmDocumentComparator.SINGLETON;

		assertTrue(comparator.compare(psmDocument1, psmDocument2) < 0);
		assertTrue(comparator.compare(psmDocument1, psmDocument1) == 0);
		assertTrue(comparator.compare(psmDocument2, psmDocument1) > 0);
	}
}
