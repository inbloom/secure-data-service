//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.31 at 09:35:49 AM EDT 
//


package org.slc.sli.test.edfi.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This educational entity represents a setting in which organized instruction of course content is provided to one or more students for a given period of time.  A course may be offered to more than one class/section.  Instruction, provided by one or more teachers or other staff members, may be delivered in person or via a different medium. 
 * 
 * 
 * <p>Java class for Section complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Section">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ComplexObjectType">
 *       &lt;sequence>
 *         &lt;element name="UniqueSectionCode" type="{http://ed-fi.org/0100}UniqueSectionCode"/>
 *         &lt;element name="SequenceOfCourse">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *               &lt;maxInclusive value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="EducationalEnvironment" type="{http://ed-fi.org/0100}EducationalEnvironmentType" minOccurs="0"/>
 *         &lt;element name="MediumOfInstruction" type="{http://ed-fi.org/0100}MediumOfInstructionType" minOccurs="0"/>
 *         &lt;element name="PopulationServed" type="{http://ed-fi.org/0100}PopulationServedType" minOccurs="0"/>
 *         &lt;element name="AvailableCredit" type="{http://ed-fi.org/0100}Credits" minOccurs="0"/>
 *         &lt;element name="CourseOfferingReference" type="{http://ed-fi.org/0100}CourseOfferingReferenceType"/>
 *         &lt;element name="SchoolReference" type="{http://ed-fi.org/0100}EducationalOrgReferenceType"/>
 *         &lt;element name="SessionReference" type="{http://ed-fi.org/0100}SessionReferenceType" minOccurs="0"/>
 *         &lt;element name="LocationReference" type="{http://ed-fi.org/0100}LocationReferenceType" minOccurs="0"/>
 *         &lt;element name="ClassPeriodReference" type="{http://ed-fi.org/0100}ClassPeriodReferenceType" minOccurs="0"/>
 *         &lt;element name="ProgramReference" type="{http://ed-fi.org/0100}ProgramReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Section", propOrder = {
    "uniqueSectionCode",
    "sequenceOfCourse",
    "educationalEnvironment",
    "mediumOfInstruction",
    "populationServed",
    "availableCredit",
    "courseOfferingReference",
    "schoolReference",
    "sessionReference",
    "locationReference",
    "classPeriodReference",
    "programReference"
})
public class Section
    extends ComplexObjectType
{

    @XmlElement(name = "UniqueSectionCode", required = true)
    protected String uniqueSectionCode;
    @XmlElement(name = "SequenceOfCourse")
    protected int sequenceOfCourse;
    @XmlElement(name = "EducationalEnvironment")
    protected EducationalEnvironmentType educationalEnvironment;
    @XmlElement(name = "MediumOfInstruction")
    protected MediumOfInstructionType mediumOfInstruction;
    @XmlElement(name = "PopulationServed")
    protected PopulationServedType populationServed;
    @XmlElement(name = "AvailableCredit")
    protected Credits availableCredit;
    @XmlElement(name = "CourseOfferingReference", required = true)
    protected CourseOfferingReferenceType courseOfferingReference;
    @XmlElement(name = "SchoolReference", required = true)
    protected EducationalOrgReferenceType schoolReference;
    @XmlElement(name = "SessionReference")
    protected SessionReferenceType sessionReference;
    @XmlElement(name = "LocationReference")
    protected LocationReferenceType locationReference;
    @XmlElement(name = "ClassPeriodReference")
    protected ClassPeriodReferenceType classPeriodReference;
    @XmlElement(name = "ProgramReference")
    protected List<ProgramReferenceType> programReference;

    /**
     * Gets the value of the uniqueSectionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueSectionCode() {
        return uniqueSectionCode;
    }

    /**
     * Sets the value of the uniqueSectionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueSectionCode(String value) {
        this.uniqueSectionCode = value;
    }

    /**
     * Gets the value of the sequenceOfCourse property.
     * 
     */
    public int getSequenceOfCourse() {
        return sequenceOfCourse;
    }

    /**
     * Sets the value of the sequenceOfCourse property.
     * 
     */
    public void setSequenceOfCourse(int value) {
        this.sequenceOfCourse = value;
    }

    /**
     * Gets the value of the educationalEnvironment property.
     * 
     * @return
     *     possible object is
     *     {@link EducationalEnvironmentType }
     *     
     */
    public EducationalEnvironmentType getEducationalEnvironment() {
        return educationalEnvironment;
    }

    /**
     * Sets the value of the educationalEnvironment property.
     * 
     * @param value
     *     allowed object is
     *     {@link EducationalEnvironmentType }
     *     
     */
    public void setEducationalEnvironment(EducationalEnvironmentType value) {
        this.educationalEnvironment = value;
    }

    /**
     * Gets the value of the mediumOfInstruction property.
     * 
     * @return
     *     possible object is
     *     {@link MediumOfInstructionType }
     *     
     */
    public MediumOfInstructionType getMediumOfInstruction() {
        return mediumOfInstruction;
    }

    /**
     * Sets the value of the mediumOfInstruction property.
     * 
     * @param value
     *     allowed object is
     *     {@link MediumOfInstructionType }
     *     
     */
    public void setMediumOfInstruction(MediumOfInstructionType value) {
        this.mediumOfInstruction = value;
    }

    /**
     * Gets the value of the populationServed property.
     * 
     * @return
     *     possible object is
     *     {@link PopulationServedType }
     *     
     */
    public PopulationServedType getPopulationServed() {
        return populationServed;
    }

    /**
     * Sets the value of the populationServed property.
     * 
     * @param value
     *     allowed object is
     *     {@link PopulationServedType }
     *     
     */
    public void setPopulationServed(PopulationServedType value) {
        this.populationServed = value;
    }

    /**
     * Gets the value of the availableCredit property.
     * 
     * @return
     *     possible object is
     *     {@link Credits }
     *     
     */
    public Credits getAvailableCredit() {
        return availableCredit;
    }

    /**
     * Sets the value of the availableCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credits }
     *     
     */
    public void setAvailableCredit(Credits value) {
        this.availableCredit = value;
    }

    /**
     * Gets the value of the courseOfferingReference property.
     * 
     * @return
     *     possible object is
     *     {@link CourseOfferingReferenceType }
     *     
     */
    public CourseOfferingReferenceType getCourseOfferingReference() {
        return courseOfferingReference;
    }

    /**
     * Sets the value of the courseOfferingReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link CourseOfferingReferenceType }
     *     
     */
    public void setCourseOfferingReference(CourseOfferingReferenceType value) {
        this.courseOfferingReference = value;
    }

    /**
     * Gets the value of the schoolReference property.
     * 
     * @return
     *     possible object is
     *     {@link EducationalOrgReferenceType }
     *     
     */
    public EducationalOrgReferenceType getSchoolReference() {
        return schoolReference;
    }

    /**
     * Sets the value of the schoolReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link EducationalOrgReferenceType }
     *     
     */
    public void setSchoolReference(EducationalOrgReferenceType value) {
        this.schoolReference = value;
    }

    /**
     * Gets the value of the sessionReference property.
     * 
     * @return
     *     possible object is
     *     {@link SessionReferenceType }
     *     
     */
    public SessionReferenceType getSessionReference() {
        return sessionReference;
    }

    /**
     * Sets the value of the sessionReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link SessionReferenceType }
     *     
     */
    public void setSessionReference(SessionReferenceType value) {
        this.sessionReference = value;
    }

    /**
     * Gets the value of the locationReference property.
     * 
     * @return
     *     possible object is
     *     {@link LocationReferenceType }
     *     
     */
    public LocationReferenceType getLocationReference() {
        return locationReference;
    }

    /**
     * Sets the value of the locationReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationReferenceType }
     *     
     */
    public void setLocationReference(LocationReferenceType value) {
        this.locationReference = value;
    }

    /**
     * Gets the value of the classPeriodReference property.
     * 
     * @return
     *     possible object is
     *     {@link ClassPeriodReferenceType }
     *     
     */
    public ClassPeriodReferenceType getClassPeriodReference() {
        return classPeriodReference;
    }

    /**
     * Sets the value of the classPeriodReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassPeriodReferenceType }
     *     
     */
    public void setClassPeriodReference(ClassPeriodReferenceType value) {
        this.classPeriodReference = value;
    }

    /**
     * Gets the value of the programReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the programReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProgramReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProgramReferenceType }
     * 
     * 
     */
    public List<ProgramReferenceType> getProgramReference() {
        if (programReference == null) {
            programReference = new ArrayList<ProgramReferenceType>();
        }
        return this.programReference;
    }

}
