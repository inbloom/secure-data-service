package org.slc.sli.sif.domain.slientity;

/**
 * Corresponds to the electronicMail complex-type in the SLI schema.
 *
 * @author jtully
 *
 */
public class ElectronicMail {
    private String emailAddress;
    private String emailAddressType;

    public String getEmailAddressType() {
        return emailAddressType;
    }

    public void setEmailAddressType(String emailAddressType) {
        this.emailAddressType = emailAddressType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String electronicMailAddress) {
        this.emailAddress = electronicMailAddress;
    }
}
