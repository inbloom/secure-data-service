package org.slc.sli.modeling.docgen;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DocumentationTest {
	
	private enum MyType {
		TYPE_1,
		TYPE_2;
	}
	
	private final static List<Domain<MyType>> DOMAINS = new ArrayList<Domain<MyType>>();
	static {
		DOMAINS.add(new Domain<MyType>("title1", "description1", new ArrayList<Entity<MyType>>(), new ArrayList<Diagram>()));
		DOMAINS.add(new Domain<MyType>("title2", "description2", new ArrayList<Entity<MyType>>(), new ArrayList<Diagram>())); 
	}
	
	private final Documentation<MyType> documentation = new Documentation<MyType>(DOMAINS);
	
	@Test
	public void testGetDomains(){
		assertEquals(DOMAINS, documentation.getDomains());
	}

	@Test (expected = NullPointerException.class)
	public void testNullParam() {
		new Documentation<MyType>(null);
	}

}
