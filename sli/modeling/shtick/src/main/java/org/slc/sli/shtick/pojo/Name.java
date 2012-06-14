package org.slc.sli.shtick.pojo;

public final class Name {

    private final String firstName;
    private final String lastSurname;

    public Name(final String firstName, final String lastSurname) {
        this.firstName = firstName;
        this.lastSurname = lastSurname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastSurname() {
        return lastSurname;
    }
}
