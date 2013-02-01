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

package org.slc.sli.sif.domain.converter;

import junit.framework.Assert;

import openadk.library.common.EnglishProficiencyCode;
import openadk.library.common.EnglishProficiency;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * EnglishProficiencyConverter unit tests
 */
public class EnglishProficiencyConverterTest extends AdkTest {

    private final EnglishProficiencyConverter converter = new EnglishProficiencyConverter();

    @Test
    public void testNullObject() {
        String result = converter.convert(null);
        Assert.assertNull("English Proficiency should be null", result);
    }

    @Test
    public void testNativeEnglish() {
        EnglishProficiency ep = new EnglishProficiency();
        ep.setCode(EnglishProficiencyCode.NATIVE_ENGLISH);
        String result = converter.convert(ep);
        Assert.assertEquals("NotLimited", result);
    }
    @Test
    public void testFluentEnglish() {
        EnglishProficiency ep = new EnglishProficiency();
        ep.setCode(EnglishProficiencyCode.FLUENT_ENGLISH);
        String result = converter.convert(ep);
        Assert.assertEquals("NotLimited", result);
    }
    @Test
    public void testNonEnglishSpeaking() {
        EnglishProficiency ep = new EnglishProficiency();
        ep.setCode(EnglishProficiencyCode.NON_ENGLISH_SPEAKING);
        String result = converter.convert(ep);
        Assert.assertEquals("Limited", result);
    }
    @Test
    public void testRedesignatedAsFluent() {
        EnglishProficiency ep = new EnglishProficiency();
        ep.setCode(EnglishProficiencyCode.REDESIGNATED_AS_FLUENT);
        String result = converter.convert(ep);
        Assert.assertEquals("NotLimited", result);
    }
    @Test
    public void testLimitedEnglish() {
        EnglishProficiency ep = new EnglishProficiency();
        ep.setCode(EnglishProficiencyCode.LIMITED_ENGLISH);
        String result = converter.convert(ep);
        Assert.assertEquals("Limited", result);
    }
    @Test
    public void testUnknown() {
        EnglishProficiency ep = new EnglishProficiency();
        ep.setCode(EnglishProficiencyCode.STATUS_UNKNOWN);
        String result = converter.convert(ep);
        Assert.assertNull(result);
    }


}
