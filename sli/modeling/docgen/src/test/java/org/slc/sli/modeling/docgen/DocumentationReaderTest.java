package org.slc.sli.modeling.docgen;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public class DocumentationReaderTest {
	
	private final static String XMI_FILENAME = "src/test/resources/SLI.xmi";
	private final static String DOMAIN_FILENAME = "src/test/resources/domains.xml";
	
	
	private final static File XMI_FILE = new File(XMI_FILENAME);
	private final static File DOMAIN_FILE = new File(DOMAIN_FILENAME);
	
	private ModelIndex modelIndex;
	
	@Before
	public void before() throws FileNotFoundException {
		this.modelIndex = new DefaultModelIndex(XmiReader.readModel(XMI_FILE));
	}
	
	@Test
	public void test() throws FileNotFoundException {
		Documentation<Type> resultsByFile = DocumentationReader.readDocumentation(DOMAIN_FILE, this.modelIndex);
		Documentation<Type> resultsByFilename = DocumentationReader.readDocumentation(DOMAIN_FILENAME, this.modelIndex);
		
		assertEquals(resultsByFile.getDomains().toString(), resultsByFilename.getDomains().toString());
	}
	
	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new DocumentationReader();
	}
}
