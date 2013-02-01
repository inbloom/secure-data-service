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

import openadk.library.common.OtherNames;

import org.slc.sli.sif.domain.slientity.OtherName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts a SIF otherName list to an SLI otherName list.
 *
 * @author jtully
 *
 */
@Component
public class OtherNamesConverter {

    private static final Map<String, String> NAME_TYPE_MAP = new HashMap<String, String>();
    static {
        NAME_TYPE_MAP.put("01", "Previous Legal Name");
        NAME_TYPE_MAP.put("02", "Other Name");
        NAME_TYPE_MAP.put("03", "Alias");
        NAME_TYPE_MAP.put("05", "Previous Legal Name");
        NAME_TYPE_MAP.put("07", "Other Name");
        NAME_TYPE_MAP.put("08", "Other Name");
    }

    @Autowired
    private NameConverter nameConverter;

    public List<OtherName> convert(OtherNames sifOtherNames) {
        if (sifOtherNames == null) {
            return null;
        }

        List<OtherName> sliNames = new ArrayList<OtherName>();

        for (openadk.library.common.Name sifName : sifOtherNames) {
            OtherName sliName = new OtherName();
            nameConverter.mapSifNameIntoSliName(sifName, sliName);
            sliName.setOtherNameType(NAME_TYPE_MAP.get(sifName.getType()));
            sliNames.add(sliName);
        }
        return sliNames;
    }

    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
    }

}
