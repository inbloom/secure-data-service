package org.slc.sli.shtick.pojomanual;

import java.util.*;
import org.slc.sli.shtick.pojo.*;

/**
 * The set of elements that comprise a person's legal name.
 */
public final class NameManual {
    private final Map<String, Object> data;

    public NameManual(final Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getUnderlying() {
        return this.data;
    }

    public String getId() {
        return (String) data.get("id");
    }

    /**
     * A prefix used to denote the title, degree, position or seniority of the person.
     */
    public PersonalTitlePrefixType getPersonalTitlePrefix() {
        return PersonalTitlePrefixType.valueOfName((String) data.get("personalTitlePrefix"));
    }

    /**
     * A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.
     */
    public SimpleName getFirstName() {
        return new SimpleName((String) data.get("firstName"));
    }

    public void setFirstName(SimpleName firstName) {
        getUnderlying().put("firstName", firstName.getValue());
    }

    /**
     * A secondary name given to an individual at birth, baptism, or during another naming ceremony.
     */
    public SimpleName getMiddleName() {
        return new SimpleName((String) data.get("middleName"));
    }

    public void setMiddleName(SimpleName middleName) {
        this.data.put("middleName", middleName.getValue());
    }

    /**
     * The name borne in common by members of a family.
     */
    public SimpleName getLastSurname() {
        return new SimpleName((String) data.get("lastSurname"));
    }

    public void setLastSurname(SimpleName lastSurname) {
        this.data.put("lastSurname", lastSurname.getValue());
    }

    /**
     * An appendage, if any, used to denote an individual's generation in his family (e.g., Jr., Sr., III).
     */
    public GenerationCodeSuffixType getGenerationCodeSuffix() {
        return GenerationCodeSuffixType.valueOfName((String) data.get("generationCodeSuffix"));
    }

    /**
     * The person's maiden name.
     */
    public SimpleName getMaidenName() {
        return new SimpleName((String) data.get("maidenName"));
    }

    /**
     * The evidence presented to verify one's personal identity; for example: drivers license, passport, birth certificate, etc.
     */
    public PersonalInformationVerificationType getVerification() {
        return PersonalInformationVerificationType.valueOfName((String) data.get("verification"));
    }
}
