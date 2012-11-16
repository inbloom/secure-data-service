

package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides alternative references for grades during interchange. Use XML IDREF to reference a course record that is included in the interchange
 *
 * <p>Java class for GradeReferenceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GradeReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ReferenceType">
 *       &lt;sequence>
 *         &lt;element name="GradeIdentity" type="{http://ed-fi.org/0100}GradeIdentityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GradeReferenceType", propOrder = {
    "gradeIdentity"
})
public class GradeReferenceType
    extends ReferenceType
{

    @XmlElement(name = "GradeIdentity")
    protected GradeIdentityType gradeIdentity;

    /**
     * Gets the value of the gradeIdentity property.
     *
     * @return
     *     possible object is
     *     {@link GradeIdentityType }
     *
     */
    public GradeIdentityType getGradeIdentity() {
        return gradeIdentity;
    }

    /**
     * Sets the value of the gradeIdentity property.
     *
     * @param value
     *     allowed object is
     *     {@link GradeIdentityType }
     *
     */
    public void setGradeIdentity(GradeIdentityType value) {
        this.gradeIdentity = value;
    }

}
