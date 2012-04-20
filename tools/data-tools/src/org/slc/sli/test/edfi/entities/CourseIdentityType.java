//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.17 at 01:12:00 PM EDT 
//


package org.slc.sli.test.edfi.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Encapsulates the possible attributes that can be used to lookup the identity of courses. 
 * 
 * <p>Java class for CourseIdentityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CourseIdentityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CourseCode" type="{http://ed-fi.org/0100}CourseCode" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CourseIdentityType", propOrder = {
    "courseCode"
})
public class CourseIdentityType {

    @XmlElement(name = "CourseCode", required = true)
    protected List<CourseCode> courseCode;

    /**
     * Gets the value of the courseCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the courseCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCourseCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CourseCode }
     * 
     * 
     */
    public List<CourseCode> getCourseCode() {
        if (courseCode == null) {
            courseCode = new ArrayList<CourseCode>();
        }
        return this.courseCode;
    }

}
