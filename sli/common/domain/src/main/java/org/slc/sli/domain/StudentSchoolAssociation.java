package org.slc.sli.domain;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.ExitWithdrawalType;
import org.slc.sli.domain.enums.GradeLevelType;

/**
 * TODO: Convert strings to enums
 * TODO: Add missing associations
 * 
 * @author rfarris@wgen.net
 */
@XmlRootElement
public class StudentSchoolAssociation {
    
    private Integer associationId;
    private Integer studentId;
    private Integer schoolId;
    private Calendar entryDate;
    private GradeLevelType entryGradeLevel;
    private EntryType entryType;
    private Boolean repeatedGrade;
    private Calendar classOf;
    private Boolean schoolChoiceTransfer;
    private Calendar exitWithdrawDate;
    private ExitWithdrawalType exitWithdrawType;
    
    // private List<String> educationalPlans;
    
    public StudentSchoolAssociation() {
        // ensure default constructor for JAXB
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((associationId == null) ? 0 : associationId.hashCode());
        return result;
    }
    
    /**
     * @return the associationId
     */
    public Integer getAssociationId() {
        return associationId;
    }
    
    /**
     * @param associationId
     *            the associationId to set
     */
    public void setAssociationId(Integer associationId) {
        this.associationId = associationId;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StudentSchoolAssociation other = (StudentSchoolAssociation) obj;
        if (associationId == null) {
            if (other.associationId != null)
                return false;
        } else if (!associationId.equals(other.associationId))
            return false;
        return true;
    }
    
    /**
     * @return the entryDate
     */
    public Calendar getEntryDate() {
        return entryDate;
    }
    
    /**
     * @param entryDate
     *            the entryDate to set
     */
    public void setEntryDate(Calendar entryDate) {
        this.entryDate = entryDate;
    }
    
    /**
     * @return the entryGradeLevel
     */
    public GradeLevelType getEntryGradeLevel() {
        return entryGradeLevel;
    }
    
    /**
     * @param entryGradeLevel
     *            the entryGradeLevel to set
     */
    public void setEntryGradeLevel(GradeLevelType entryGradeLevel) {
        this.entryGradeLevel = entryGradeLevel;
    }
    
    /**
     * @return the entryType
     */
    public EntryType getEntryType() {
        return entryType;
    }
    
    /**
     * @param entryType
     *            the entryType to set
     */
    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }
    
    /**
     * @return the repeatedGrade
     */
    public Boolean isRepeatedGrade() {
        return repeatedGrade;
    }
    
    /**
     * @param repeatedGrade
     *            the repeatedGrade to set
     */
    public void setRepeatedGrade(Boolean repeatedGrade) {
        this.repeatedGrade = repeatedGrade;
    }
    
    /**
     * @return the classOf
     */
    public Calendar getClassOf() {
        return classOf;
    }
    
    /**
     * @param classOf
     *            the classOf to set
     */
    public void setClassOf(Calendar classOf) {
        this.classOf = classOf;
    }
    
    /**
     * @return the schoolChoiceTransfer
     */
    public Boolean isSchoolChoiceTransfer() {
        return schoolChoiceTransfer;
    }
    
    /**
     * @param schoolChoiceTransfer
     *            the schoolChoiceTransfer to set
     */
    public void setSchoolChoiceTransfer(Boolean schoolChoiceTransfer) {
        this.schoolChoiceTransfer = schoolChoiceTransfer;
    }
    
    /**
     * @return the exitWithdrawDate
     */
    public Calendar getExitWithdrawDate() {
        return exitWithdrawDate;
    }
    
    /**
     * @param exitWithdrawDate
     *            the exitWithdrawDate to set
     */
    public void setExitWithdrawDate(Calendar exitWithdrawDate) {
        this.exitWithdrawDate = exitWithdrawDate;
    }
    
    /**
     * @return the exitWithdrawType
     */
    public ExitWithdrawalType getExitWithdrawType() {
        return exitWithdrawType;
    }
    
    /**
     * @param exitWithdrawType
     *            the exitWithdrawType to set
     */
    public void setExitWithdrawType(ExitWithdrawalType exitWithdrawType) {
        this.exitWithdrawType = exitWithdrawType;
    }
    
    /**
     * @return the studentId
     */
    public Integer getStudentId() {
        return studentId;
    }
    
    /**
     * @param studentId
     *            the studentId to set
     */
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    /**
     * @return the schoolId
     */
    public Integer getSchoolId() {
        return schoolId;
    }
    
    /**
     * @param schoolId
     *            the schoolId to set
     */
    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }
    
}
