/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.ReferenceSchema;
import org.slc.sli.validation.schema.StringSchema;

/**
 * Class to test methods of org.slc.sli.api.config.EntityDefinition.
 *
 * @author vmcglaughlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class EntityDefinitionTest {

    @Autowired
    private DefinitionFactory factory;

    // class under test
    private EntityDefinition entityDefinition;

    @Before
    public void setUp() throws Exception {
        entityDefinition = factory.makeEntity("testEntity").build();
    }

    @Test
    public void testSetSchema() {
        NeutralSchema mockSchema = new ComplexSchema();
        populate(mockSchema);
        entityDefinition.setSchema(mockSchema);

        Map<String, ReferenceSchema> referenceFields = entityDefinition.getReferenceFields();

        assertEquals("Expected map with two entries", 3, referenceFields.size());

        ReferenceSchema stringSchema = referenceFields.get("StringSchema1");
        assertNull("Expected null schema", stringSchema);

        ReferenceSchema referenceSchema = referenceFields.get("ReferenceSchema1");
        assertNotNull("Expected non-null schema", referenceSchema);
        assertEquals("Expected different reference type",
                referenceSchema.getType(), "straightReferenceTest");

        ReferenceSchema listSchema = referenceFields.get("ListSchema1");
        assertNotNull("Expected non-null schema", listSchema);
        assertEquals("Expected different reference type",
                listSchema.getType(), "listReferenceTest");

        assertTrue(entityDefinition.hasArrayField("ListSchema1"));
        assertFalse(entityDefinition.hasArrayField("StringSchema1"));
        assertFalse(entityDefinition.hasArrayField("Foo"));
    }

    private void populate(NeutralSchema schema) {
        schema.addField("StringSchema1", getStringSchema("ignoreStringTest"));
        schema.addField("ReferenceSchema1", getReferenceSchema("straightReferenceTest"));

        List<NeutralSchema> schemaList = new ArrayList<NeutralSchema>();
        schemaList.add(getReferenceSchema("listReferenceTest"));

        ComplexSchema complex = new ComplexSchema();
        complex.addField("complexRef", getReferenceSchema("complexRefXsd"));
        schemaList.add(complex);

        ListSchema listSchema = getListSchema("listSchema");
        listSchema.setList(schemaList);

        schema.addField("ListSchema1", listSchema);
    }

    private StringSchema getStringSchema(String xsdType) {
        return new StringSchema(xsdType);
    }

    private ReferenceSchema getReferenceSchema(String xsdType) {
        return new ReferenceSchema(xsdType, null);
    }

    private ListSchema getListSchema(String xsdType) {
        return new ListSchema(xsdType);
    }
}
