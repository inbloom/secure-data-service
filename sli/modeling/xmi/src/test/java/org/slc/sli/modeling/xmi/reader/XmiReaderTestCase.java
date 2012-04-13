package org.slc.sli.modeling.xmi.reader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.xmi.writer.XmiWriter;

/**
 * Tests for {@link XmiReader}.
 */
public final class XmiReaderTestCase extends TestCase {
    
    public void testMinimal() {
        final Map<Identifier, ClassType> classTypes = new HashMap<Identifier, ClassType>();
        final Map<Identifier, DataType> dataTypes = new HashMap<Identifier, DataType>();
        final Map<Identifier, EnumType> enumTypes = new HashMap<Identifier, EnumType>();
        final Map<Identifier, Association> associations = new HashMap<Identifier, Association>();
        final Map<Identifier, Generalization> generalizations = new HashMap<Identifier, Generalization>();
        final Map<Identifier, TagDefinition> tagDefinitions = new HashMap<Identifier, TagDefinition>();
        
        final Model modelIn = new Model("", classTypes, dataTypes, enumTypes, associations, generalizations,
                tagDefinitions);
        
        final String text = serialized(modelIn);
        final InputStream stream;
        try {
            stream = new ByteArrayInputStream(text.getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        final Model modelOut = XmiReader.readModel(stream);
        assertEquals(modelIn, modelOut);
    }
    
    private static final String serialized(final Model model) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmiWriter.writeDocument(model, baos);
        try {
            return new String(baos.toByteArray(), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
