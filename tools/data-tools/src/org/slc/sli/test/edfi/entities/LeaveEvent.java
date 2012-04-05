//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.30 at 01:48:06 PM EDT 
//


package org.slc.sli.test.edfi.entities;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * This event entity represents the recording of the dates of staff leave (e.g., sick leave, personal time, vacation, etc.).
 * 
 * <p>Java class for LeaveEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LeaveEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ComplexObjectType">
 *       &lt;sequence>
 *         &lt;element name="EventDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="LeaveEventCategory" type="{http://ed-fi.org/0100}LeaveEventCategoryType"/>
 *         &lt;element name="LeaveEventReason" type="{http://ed-fi.org/0100}LeaveEventReason" minOccurs="0"/>
 *         &lt;element name="HoursOnLeave" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="SubstituteAssigned" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="StaffReference" type="{http://ed-fi.org/0100}StaffReferenceType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LeaveEvent", propOrder = {
    "eventDate",
    "leaveEventCategory",
    "leaveEventReason",
    "hoursOnLeave",
    "substituteAssigned",
    "staffReference"
})
public class LeaveEvent
    extends ComplexObjectType
{

    @XmlElement(name = "EventDate", required = true)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected String eventDate;
    @XmlElement(name = "LeaveEventCategory", required = true)
    protected LeaveEventCategoryType leaveEventCategory;
    @XmlElement(name = "LeaveEventReason")
    protected String leaveEventReason;
    @XmlElement(name = "HoursOnLeave")
    protected BigDecimal hoursOnLeave;
    @XmlElement(name = "SubstituteAssigned")
    protected Boolean substituteAssigned;
    @XmlElement(name = "StaffReference", required = true)
    protected StaffReferenceType staffReference;

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDate(String value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the leaveEventCategory property.
     * 
     * @return
     *     possible object is
     *     {@link LeaveEventCategoryType }
     *     
     */
    public LeaveEventCategoryType getLeaveEventCategory() {
        return leaveEventCategory;
    }

    /**
     * Sets the value of the leaveEventCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link LeaveEventCategoryType }
     *     
     */
    public void setLeaveEventCategory(LeaveEventCategoryType value) {
        this.leaveEventCategory = value;
    }

    /**
     * Gets the value of the leaveEventReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeaveEventReason() {
        return leaveEventReason;
    }

    /**
     * Sets the value of the leaveEventReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeaveEventReason(String value) {
        this.leaveEventReason = value;
    }

    /**
     * Gets the value of the hoursOnLeave property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHoursOnLeave() {
        return hoursOnLeave;
    }

    /**
     * Sets the value of the hoursOnLeave property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHoursOnLeave(BigDecimal value) {
        this.hoursOnLeave = value;
    }

    /**
     * Gets the value of the substituteAssigned property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubstituteAssigned() {
        return substituteAssigned;
    }

    /**
     * Sets the value of the substituteAssigned property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubstituteAssigned(Boolean value) {
        this.substituteAssigned = value;
    }

    /**
     * Gets the value of the staffReference property.
     * 
     * @return
     *     possible object is
     *     {@link StaffReferenceType }
     *     
     */
    public StaffReferenceType getStaffReference() {
        return staffReference;
    }

    /**
     * Sets the value of the staffReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link StaffReferenceType }
     *     
     */
    public void setStaffReference(StaffReferenceType value) {
        this.staffReference = value;
    }

}
