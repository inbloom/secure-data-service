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
import openadk.library.common.EntryType;
import openadk.library.common.EntryTypeCode;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * Test class for EntryTypeConverter
 */
public class EntryTypeConverterTest extends AdkTest {

    private final EntryTypeConverter converter = new EntryTypeConverter();

    @Test
    public void testNullObject() {
        String result = converter.convert(null);
        Assert.assertNull("Entry Type should be null", result);
    }

    @Test
    public void testMappings() {
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1821)), "Transfer from a public school in the same local education agency");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1822)), "Transfer from a public school in a different local education agency in the same state");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1823)), "Transfer from a public school in a different state");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1824)), "Transfer from a private, non-religiously-affiliated school in the same local education agency");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1825)), "Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1826)), "Transfer from a private, non-religiously-affiliated school in a different state");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1827)), "Transfer from a private, religiously-affiliated school in the same local education agency");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1828)), "Transfer from a private, religiously-affiliated school in a different local education agency in the same state");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1829)), "Transfer from a private, religiously-affiliated school in a different state");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1830)), "Transfer from a school outside of the country");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1831)), "Transfer from an institution");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1832)), "Transfer from a charter school");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1833)), "Transfer from home schooling");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1835)), "Re-entry from the same school with no interruption of schooling");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1836)), "Re-entry after a voluntary withdrawal");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1837)), "Re-entry after an involuntary withdrawal");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1838)), "Original entry into a United States school");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1839)), "Original entry into a United States school from a foreign country with no interruption in schooling");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_1840)), "Original entry into a United States school from a foreign country with an interruption in schooling");
        Assert.assertEquals(converter.convert(getEntryType(EntryTypeCode._0619_9999)), "Other");
    }

    private EntryType getEntryType(EntryTypeCode code) {
        EntryType et = new EntryType();
        et.setCode(code);
        return et;
    }

}
