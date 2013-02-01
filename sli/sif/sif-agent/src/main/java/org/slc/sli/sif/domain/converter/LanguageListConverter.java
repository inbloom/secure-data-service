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

import org.springframework.stereotype.Component;

import openadk.library.common.LanguageCode;
import openadk.library.common.LanguageList;

/**
 * A custom converter to convert SIF LanguageList to SLI languages.
 *
 * Valid SLI values:
 * Cherokee
 * Mandarin (Chinese)
 * English
 * French
 * German
 * Hebrew
 * Italian
 * Japanese
 * Korean
 * Spanish
 * Other languages
 * Other
 *
 * @author slee
 *
 */
@Component
public class LanguageListConverter {
    private static final Map<LanguageCode, String> LANGUAGE_TYPE_MAP = new HashMap<LanguageCode, String>();
    static {
        LANGUAGE_TYPE_MAP.put(LanguageCode.CHECHEN, "Other languages");
        LANGUAGE_TYPE_MAP.put(LanguageCode.CHEROKEE, "Cherokee");
        LANGUAGE_TYPE_MAP.put(LanguageCode.CHINESE, "Mandarin (Chinese)");
        LANGUAGE_TYPE_MAP.put(LanguageCode.ENGLISH, "English");
        LANGUAGE_TYPE_MAP.put(LanguageCode.FRENCH, "French");
        LANGUAGE_TYPE_MAP.put(LanguageCode.GERMAN, "German");
        LANGUAGE_TYPE_MAP.put(LanguageCode.HAWAIIAN, "Other languages");
        LANGUAGE_TYPE_MAP.put(LanguageCode.HEBREW, "Hebrew");
        LANGUAGE_TYPE_MAP.put(LanguageCode.ITALIAN, "Italian");
        LANGUAGE_TYPE_MAP.put(LanguageCode.JAPANESE, "Japanese");
        LANGUAGE_TYPE_MAP.put(LanguageCode.KOREAN, "Korean");
        LANGUAGE_TYPE_MAP.put(LanguageCode.MOHAWK, "Other languages");
        LANGUAGE_TYPE_MAP.put(LanguageCode.MULTIPLE, "Other languages");
        LANGUAGE_TYPE_MAP.put(LanguageCode.SPANISH, "Spanish");
    }

    public List<String> convert(LanguageList languageList) {
        if (languageList == null) {
            return null;
        }

        return toSliLanguageList(languageList.getLanguages());
    }

    private List<String> toSliLanguageList(openadk.library.common.Language[] languages) {
        List<String> list = new ArrayList<String>(languages.length);
        for (openadk.library.common.Language language : languages) {
            String mapping = LANGUAGE_TYPE_MAP.get(LanguageCode.wrap(language.getCode()));
            list.add(mapping == null ? "Other" : mapping);
        }
        return list;
    }

}
