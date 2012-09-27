package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

public class QNameComparatorTest {
	
	@Test
	public void test() {
		for (QNameComparator qNameComparator : QNameComparator.values()) {
			assertEquals(qNameComparator, QNameComparator.valueOf(qNameComparator.toString()));
		}
	}
	
	@Test
	public void testSingletonCompare() {
		String firstNamespace = "FIRST_NAMESPACE";
		String secondNamespace = "SECOND_NAMESPACE";
		String prefix = "PREFIX";

		QName absolutelyFirstQName = new QName(firstNamespace, "zzzzz", prefix);
		QName firstQName = new QName(secondNamespace, "first", prefix);
		QName secondQName = new QName(secondNamespace, "second", prefix);

		assertTrue(QNameComparator.SINGLETON.compare(absolutelyFirstQName, firstQName) < 0);
		assertTrue(QNameComparator.SINGLETON.compare(absolutelyFirstQName, secondQName) < 0);
		assertTrue(QNameComparator.SINGLETON.compare(firstQName, secondQName) < 0);
		assertTrue(QNameComparator.SINGLETON.compare(firstQName, firstQName) == 0);
		assertTrue(QNameComparator.SINGLETON.compare(secondQName, firstQName) > 0);
	}
}
