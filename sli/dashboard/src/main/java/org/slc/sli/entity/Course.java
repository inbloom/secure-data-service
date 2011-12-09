package org.slc.sli.entity;

public class Course {

    private Section[] sections;
    private String category;
    
    public Section[] getSections() {
        return sections;
    }
    public void setSections(Section[] sections) {
        this.sections = sections;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    
}
