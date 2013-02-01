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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.common.Language;
import openadk.library.common.LanguageCode;
import openadk.library.common.LanguageList;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * AddressListConverter unit tests
 */
public class LanguageListConverterTest extends AdkTest {
    private final LanguageListConverter converter = new LanguageListConverter();
    private Map<LanguageCode, String> map = new HashMap<LanguageCode, String>();

    @Test
    public void testNullObject() {
        List<String> result = converter.convert(null);
        Assert.assertNull("Address list should be null", result);
    }

    @Test
    public void testEmptyList() {
        List<String> result = converter.convert(new LanguageList());
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyLanguage() {
        LanguageList list = new LanguageList();
        openadk.library.common.Language original = new openadk.library.common.Language();
        list.add(original);
        List<String> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        String language = result.get(0);
        Assert.assertEquals("Other", language);
    }

    @Test
    public void testConversion() {
        map.clear();
        map.put(LanguageCode.CHECHEN, "Other languages");
        map.put(LanguageCode.CHEROKEE, "Cherokee");
        map.put(LanguageCode.CHINESE, "Mandarin (Chinese)");
        map.put(LanguageCode.ENGLISH, "English");
        map.put(LanguageCode.FRENCH, "French");
        map.put(LanguageCode.GERMAN, "German");
        map.put(LanguageCode.HAWAIIAN, "Other languages");
        map.put(LanguageCode.HEBREW, "Hebrew");
        map.put(LanguageCode.ITALIAN, "Italian");
        map.put(LanguageCode.JAPANESE, "Japanese");
        map.put(LanguageCode.KOREAN, "Korean");
        map.put(LanguageCode.MOHAWK, "Other languages");
        map.put(LanguageCode.MULTIPLE, "Other languages");
        map.put(LanguageCode.SPANISH, "Spanish");
        map.put(LanguageCode.wrap("something else"), "Other");

        //use map to get a list of LanguageCode
        List<LanguageCode> languageCodes = new ArrayList<LanguageCode>();
        languageCodes.addAll(map.keySet());
        //use map to come up with a LanguageList
        LanguageList originalList = new LanguageList();
        for (LanguageCode languageCode : languageCodes) {
            originalList.add(new Language(languageCode));
        }

        List<String> convertedList = converter.convert(originalList);
        Assert.assertEquals(originalList.size(), convertedList.size());
        int i = 0;
        for (String converted : convertedList) {
            LanguageCode original = languageCodes.get(i++);
            Assert.assertEquals(converted, map.get(original));
        }
    }
}
