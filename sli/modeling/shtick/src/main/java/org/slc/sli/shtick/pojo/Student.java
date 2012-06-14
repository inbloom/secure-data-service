package org.slc.sli.shtick.pojo;

public final class Student {

    private final String id;
    private final Name name;
    private final String sex;
    private final Boolean economicDisadvantaged;
    private final String studentUniqueStateId;

    public Student(final String id, final Name name, final String sex, final Boolean economicDisadvantaged,
            final String studentUniqueStateId) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.economicDisadvantaged = economicDisadvantaged;
        this.studentUniqueStateId = studentUniqueStateId;
    }

    public String getId() {
        return id;
    }

    public String getSex() {
        return sex;
    }

    public Name getName() {
        return name;
    }

    public Boolean getEconomicDisadvantaged() {
        return economicDisadvantaged;
    }

    public String getStudentUniqueStateId() {
        return studentUniqueStateId;
    }
}
