package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GradebookEntryReferenceType", propOrder = {
    "gradebookEntryType",
    "dateAssigned",
    "sectionReference" })
public class GradebookEntryReferenceType extends ReferenceType {
    
    @XmlElement(name = "GradebookEntryType")
    protected String gradebookEntryType;
    @XmlElement(name = "DateAssigned")
    protected String dateAssigned;
    @XmlElement(name = "SectionReference")
    protected SectionReferenceType sectionReference;
    
    public String getGradebookEntryType() {
        return gradebookEntryType;
    }
    
    public void setGradebookEntryType(String gradebookEntryType) {
        this.gradebookEntryType = gradebookEntryType;
    }
    
    public String getDateAssigned() {
        return dateAssigned;
    }
    
    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
    
    public SectionReferenceType getSectionReference() {
        return sectionReference;
    }
    
    public void setSectionReference(SectionReferenceType sectionReference) { 
        this.sectionReference = sectionReference;
    }
    
    
    
    
}
