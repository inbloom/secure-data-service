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

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.Name;

/**
 * Converts a SIF Name to an SLI Name
 *
 * @author jtully
 *
 */
@Component
public class NameConverter {

    private static final Set<String> SLI_PREFIXES = new HashSet<String>();
    private static final Set<String> SLI_SUFFIXES = new HashSet<String>();
    static {
        SLI_PREFIXES.add("Colonel");
        SLI_PREFIXES.add("Dr");
        SLI_PREFIXES.add("Mr");
        SLI_PREFIXES.add("Mrs");
        SLI_PREFIXES.add("Reverend");
        SLI_PREFIXES.add("Sr");
        SLI_PREFIXES.add("Sister");

        SLI_SUFFIXES.add("Jr");
        SLI_SUFFIXES.add("Sr");
        SLI_SUFFIXES.add("II");
        SLI_SUFFIXES.add("III");
        SLI_SUFFIXES.add("IV");
        SLI_SUFFIXES.add("V");
        SLI_SUFFIXES.add("VI");
        SLI_SUFFIXES.add("VII");
        SLI_SUFFIXES.add("VIII");
    }
    private static final String DEFAULT_NAME = "Unknown";

    public Name convert(openadk.library.common.Name sifName) {
        if (sifName == null) {
            return null;
        }

        Name sliName = new Name();

        mapSifNameIntoSliName(sifName, sliName);

        return sliName;
    }

    public void mapSifNameIntoSliName(openadk.library.common.Name sifName, Name sliName) {
        if (sifName.getFirstName() != null) {
            sliName.setFirstName(sifName.getFirstName());
        } else {
            sliName.setFirstName(DEFAULT_NAME);
        }

        if (sifName.getLastName() != null) {
            sliName.setLastSurname(sifName.getLastName());
        } else {
            sliName.setLastSurname(DEFAULT_NAME);
        }

        sliName.setMiddleName(sifName.getMiddleName());

        if (sifName.getPrefix() != null && SLI_PREFIXES.contains(sifName.getPrefix())) {
            sliName.setPersonalTitlePrefix(sifName.getPrefix());
        }

        if (sifName.getSuffix() != null && SLI_SUFFIXES.contains(sifName.getSuffix())) {
            sliName.setGenerationCodeSuffix(sifName.getSuffix());
        }
    }

}
