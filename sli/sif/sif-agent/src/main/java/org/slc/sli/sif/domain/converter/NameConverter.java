package org.slc.sli.sif.domain.converter;

import java.util.HashSet;
import java.util.Set;

import org.slc.sli.sif.domain.slientity.Name;

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

        if (sifName.getFirstName() != null) {
            sliName.setFirstName(sifName.getFirstName());
        } else {
            sliName.setFirstName(DEFAULT_NAME);
        }

        if (sifName.getLastName() != null) {
            sliName.setLastName(sifName.getLastName());
        } else {
            sliName.setLastName(DEFAULT_NAME);
        }

        sliName.setMiddleName(sifName.getMiddleName());

        if (sifName.getPrefix() != null && SLI_PREFIXES.contains(sifName.getPrefix())) {
            sliName.setPersonalTitlePrefix(sifName.getPrefix());
        }

        if (sifName.getSuffix() != null && SLI_SUFFIXES.contains(sifName.getSuffix())) {
            sliName.setGenerationCodeSuffix(sifName.getSuffix());
        }

        return sliName;
    }

}
