//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.17 at 01:12:00 PM EDT 
//


package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides alternative references for Learning Objective reference during interchange. Use XML IDREF to reference a learning standard record that is included in the interchange
 * 
 * <p>Java class for StudentCompetencyObjectiveReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StudentCompetencyObjectiveReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ReferenceType">
 *       &lt;sequence>
 *         &lt;element name="StudentCompetencyObjectiveIdentity" type="{http://ed-fi.org/0100}StudentCompetencyObjectiveIdentityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudentCompetencyObjectiveReferenceType", propOrder = {
    "studentCompetencyObjectiveIdentity"
})
public class StudentCompetencyObjectiveReferenceType
    extends ReferenceType
{

    @XmlElement(name = "StudentCompetencyObjectiveIdentity")
    protected StudentCompetencyObjectiveIdentityType studentCompetencyObjectiveIdentity;

    /**
     * Gets the value of the studentCompetencyObjectiveIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link StudentCompetencyObjectiveIdentityType }
     *     
     */
    public StudentCompetencyObjectiveIdentityType getStudentCompetencyObjectiveIdentity() {
        return studentCompetencyObjectiveIdentity;
    }

    /**
     * Sets the value of the studentCompetencyObjectiveIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudentCompetencyObjectiveIdentityType }
     *     
     */
    public void setStudentCompetencyObjectiveIdentity(StudentCompetencyObjectiveIdentityType value) {
        this.studentCompetencyObjectiveIdentity = value;
    }

}
