//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.06 at 10:00:50 AM EST 
//


package org.slc.sli.test.edfi.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The one or more categories of school. For example: High School, Middle School, and/or Elementary School.
 * 
 * <p>Java class for SchoolCategoriesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SchoolCategoriesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SchoolCategory" type="{http://ed-fi.org/0100}SchoolCategoryItemType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchoolCategoriesType", propOrder = {
    "schoolCategory"
})
public class SchoolCategoriesType {

    @XmlElement(name = "SchoolCategory")
    protected List<SchoolCategoryItemType> schoolCategory;

    /**
     * Gets the value of the schoolCategory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the schoolCategory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSchoolCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SchoolCategoryItemType }
     * 
     * 
     */
    public List<SchoolCategoryItemType> getSchoolCategory() {
        if (schoolCategory == null) {
            schoolCategory = new ArrayList<SchoolCategoryItemType>();
        }
        return this.schoolCategory;
    }

}
