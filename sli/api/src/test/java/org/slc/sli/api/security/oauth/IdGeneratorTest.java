package org.slc.sli.api.security.oauth;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

public class IdGeneratorTest {
    
    @Test
    public void testIdGeneratorZeroLength() {
        String string = IdGenerator.generateId(0);
        assertEquals("", string);
    }
    
    @Test
    public void testIdGeneratorLargeLength() {
        String string = IdGenerator.generateId(1000);
        assertEquals(1000, string.length());
        for (int i = 0; i < string.length(); i++) {
            assertTrue(Character.isDigit(string.charAt(i)) || Character.isLetter(string.charAt(i)));
        }
    }
    
    /**
     * Not enabled, but would be a quick and dirty way of verifying some amount 
     * of entropy in our pseudo-randomness by checking out well it compresses
     */
    @Test
    public void testEntropy() throws Exception {
        String string = IdGenerator.generateId(1000);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(bytes);
        out.write(string.getBytes());
        out.close();
        assertTrue("Entropy was too low..." + bytes.toByteArray().length, bytes.toByteArray().length > 700);
    }


}
