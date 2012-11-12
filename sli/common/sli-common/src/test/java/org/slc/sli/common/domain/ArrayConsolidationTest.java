package org.slc.sli.common.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArrayConsolidationTest {
    @Test
    public void test() {

        String arrayPath1 = "array.path";
        String arrayPkFieldName1 = "someField";

        ArrayConsolidation arrayConsolidation = new ArrayConsolidation(arrayPath1, arrayPkFieldName1);

        assertTrue(arrayConsolidation.getArrayPath().equals(arrayPath1));
        assertTrue(arrayConsolidation.getArrayPkFieldName().equals(arrayPkFieldName1));
    }
}
