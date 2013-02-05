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


package org.slc.sli.api.security.oauth;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

/**
 * 
 * @author pwolf
 *
 */
public class TokenGeneratorTest {

    @Test
    public void testIdGeneratorZeroLength() {
        String string = TokenGenerator.generateToken(0);
        assertEquals("", string);
    }

    @Test
    public void testIdGeneratorLargeLength() {
        String string = TokenGenerator.generateToken(1000);
        assertEquals(1000, string.length());
        for (int i = 0; i < string.length(); i++) {
            assertTrue(Character.isDigit(string.charAt(i)) || Character.isLetter(string.charAt(i)));
        }
    }

    @Test
    public void testEdgeCharacters() {
        //can find random As, Zs, 0s, and 9s.
        boolean foundChars = false;
        for (int i = 0; i < 25; i++) {   //try a few times to increase our probability
            String token = TokenGenerator.generateToken(10000);
            foundChars = token.indexOf('a') > -1
                    && token.indexOf('A') > -1
                    && token.indexOf('z') > -1
                    && token.indexOf('Z') > -1
                    && token.indexOf('0') > -1
                    && token.indexOf('9') > -1;
                    if (foundChars)
                        break;
        }
        assertTrue(foundChars);
    }

    /**
     * Not enabled, but would be a quick and dirty way of verifying some amount
     * of entropy in our pseudo-randomness by checking out well it compresses
     */
    public void testEntropy() throws Exception {
        String string = TokenGenerator.generateToken(1000);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(bytes);
        out.write(string.getBytes());
        out.close();
        assertTrue("Entropy was too low..." + bytes.toByteArray().length, bytes.toByteArray().length > 700);
    }


}
