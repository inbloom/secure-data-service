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

import java.util.HashMap;
import java.util.Map;

import openadk.library.common.EntryType;
import openadk.library.common.EntryTypeCode;

import org.springframework.stereotype.Component;

/**
 * A customized converter to convert SIF EntryType to SLI EntryType enumeration.
 *
 * SLI values:
 *     Transfer from a public school in the same local education agency
 *     Transfer from a public school in a different local education agency in the same state
 *     Transfer from a public school in a different state
 *     Transfer from a private, non-religiously-affiliated school in the same local education agency
 *     Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state
 *     Transfer from a private, non-religiously-affiliated school in a different state
 *     Transfer from a private, religiously-affiliated school in the same local education agency
 *     Transfer from a private, religiously-affiliated school in a different local education agency in the same state
 *     Transfer from a private, religiously-affiliated school in a different state
 *     Transfer from a school outside of the country
 *     Transfer from an institution
 *     Transfer from a charter school
 *     Transfer from home schooling
 *     Re-entry from the same school with no interruption of schooling
 *     Re-entry after a voluntary withdrawal
 *     Re-entry after an involuntary withdrawal
 *     Original entry into a United States school
 *     Original entry into a United States school from a foreign country with no interruption in schooling
 *     Original entry into a United States school from a foreign country with an interruption in schooling
 *     Next year school
 *     Other
 */
@Component
public class EntryTypeConverter {

    private static final Map<EntryTypeCode, String> ENTRY_TYPE_CODE_MAP = new HashMap<EntryTypeCode, String>();
    static {
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1821,
                "Transfer from a public school in the same local education agency");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1822,
                "Transfer from a public school in a different local education agency in the same state");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1823, "Transfer from a public school in a different state");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1824,
                "Transfer from a private, non-religiously-affiliated school in the same local education agency");
        ENTRY_TYPE_CODE_MAP
                .put(EntryTypeCode._0619_1825,
                        "Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1826,
                "Transfer from a private, non-religiously-affiliated school in a different state");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1827,
                "Transfer from a private, religiously-affiliated school in the same local education agency");
        ENTRY_TYPE_CODE_MAP
                .put(EntryTypeCode._0619_1828,
                        "Transfer from a private, religiously-affiliated school in a different local education agency in the same state");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1829,
                "Transfer from a private, religiously-affiliated school in a different state");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1830, "Transfer from a school outside of the country");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1831, "Transfer from an institution");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1832, "Transfer from a charter school");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1833, "Transfer from home schooling");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1835,
                "Re-entry from the same school with no interruption of schooling");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1836, "Re-entry after a voluntary withdrawal");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1837, "Re-entry after an involuntary withdrawal");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1838, "Original entry into a United States school");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1839,
                "Original entry into a United States school from a foreign country with no interruption in schooling");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_1840,
                "Original entry into a United States school from a foreign country with an interruption in schooling");
        ENTRY_TYPE_CODE_MAP.put(EntryTypeCode._0619_9999, "Other");
    }

    public String convert(EntryType entryType) {
        if (entryType == null) {
            return null;
        }
        return toSliEntryType(EntryTypeCode.wrap(entryType.getCode()));
    }

    private String toSliEntryType(EntryTypeCode entryTypeCode) {
        String mapping = ENTRY_TYPE_CODE_MAP.get(entryTypeCode);
        return mapping == null ? "Other" : mapping;
    }
}
