package org.slc.sli.ingestion.transformation.normalization;

import org.junit.Assert;
import org.junit.Test;

/**
 * Extended Exception unit tests.
 *
 * @author kmyers
 *
 */
public class IdResolutionExceptionTest {

    @Test
    public void testConstructorAndGetters() {
        String errorMessage = "errorMessage";
        String key = "key";
        String value = "value";
        IdResolutionException idResolutionException = new IdResolutionException(errorMessage, key, value);
        Assert.assertEquals(key, idResolutionException.getKey());
        Assert.assertEquals(value, idResolutionException.getValue());
    }
}
