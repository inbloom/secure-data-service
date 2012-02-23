package org.slc.sli.config;

/**
 * Entity to represent student filter configuration
*/
public class StudentFilter {
    String name;
    String label;
    String description;

    public StudentFilter (String name, String label, String description) {
        this.name = name;
        this.label = label;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
