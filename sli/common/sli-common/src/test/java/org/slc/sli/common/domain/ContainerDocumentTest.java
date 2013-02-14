package org.slc.sli.common.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * ContainerDocument Tester.
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class ContainerDocumentTest {

    @Test
    public void getContainerDocument() {
        final Map<String, String> parentMap = new HashMap<String, String>();
        parentMap.put("k1", "v1");
        final ContainerDocument testDocument = new ContainerDocument.ContainerDocumentBuilder()
                .forCollection("testCollection")
                .forField("testField")
                .withParent(parentMap).build();

        assertEquals("testCollection", testDocument.getCollectionName());
        assertEquals("testField", testDocument.getFieldToPersist());
        assertEquals("v1", testDocument.getParentNaturalKeyMap().get("k1"));
    }
}
