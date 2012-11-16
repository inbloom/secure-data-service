package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Encapsulates the possible attributes that can be used to lookup the identity of grade in the grade.
 *
 * <p>Java class for GradeIdentityType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GradeIdentityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="StateOrganizationId" type="{http://ed-fi.org/0100}IdentificationCode"/>
 *           &lt;element name="EducationOrgIdentificationCode" type="{http://ed-fi.org/0100}EducationOrgIdentificationCode" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gradeIdentityType", propOrder = {
    "studentSectionAssociationReference",
    "gradingPeriodReference"
})
public class GradeIdentityType {


    @XmlElement(name = "StudentSectionAssociationReference", required = true)
    protected SLCStudentSectionAssociationReferenceType studentSectionAssociationReference;
    @XmlElement(name = "GradingPeriodReference")
    protected GradingPeriodReferenceType gradingPeriodReference;

    /**
     * Gets the value of the studentSectionAssociationReference property.
     *
     * @return
     *     possible object is
     *     {@link StudentSectionAssociationReferenceType }
     *
     */
    public SLCStudentSectionAssociationReferenceType getStudentSectionAssociationReference() {
        return studentSectionAssociationReference;
    }

    /**
     * Sets the value of the studentSectionAssociationReference property.
     *
     * @param value
     *     allowed object is
     *     {@link StudentSectionAssociationReferenceType }
     *
     */
    public void setStudentSectionAssociationReference(SLCStudentSectionAssociationReferenceType value) {
        this.studentSectionAssociationReference = value;
    }


    /**
     * Gets the value of the gradingPeriodReference property.
     *
     * @return
     *     possible object is
     *     {@link GradingPeriodReferenceType }
     *
     */
    public GradingPeriodReferenceType getGradingPeriodReference() {
        return gradingPeriodReference;
    }

    /**
     * Sets the value of the gradingPeriodReference property.
     *
     * @param value
     *     allowed object is
     *     {@link GradingPeriodReferenceType }
     *
     */
    public void setGradingPeriodReference(GradingPeriodReferenceType value) {
        this.gradingPeriodReference = value;
    }

}
