/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Encapsulates the possible attributes that can be used to lookup the identity of reportCards.
 * 
 * <p>Java class for ReportCardIdentityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReportCardIdentityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StudentReference" type="{http://ed-fi.org/0100}SLC-StudentReferenceType" minOccurs="0"/>
 *         &lt;element name="GradingPeriodReference" type="{http://ed-fi.org/0100}SLC-GradingPeriodReferenceType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportCardIdentityType", propOrder = {
    "student",
    "gradingPeriod"
})
public class ReportCardIdentityType {

    @XmlElement(name = "StudentReference")
    protected StudentReferenceType student;
    @XmlElement(name = "GradingPeriodReference")
    protected GradingPeriodReferenceType gradingPeriod;

    /**
     * Gets the value of the student property.
     * 
     * @return
     *     possible object is
     *     {@link StudentReference }
     *     
     */
    public StudentReferenceType getStudentReference() {
        return student;
    }

    /**
     * Sets the value of the student property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudentReference }
     *     
     */
    public void setStudentReference(StudentReferenceType value) {
        this.student = value;
    }

    /**
     * Gets the value of the gradingPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link GradingPeriodReference }
     *     
     */
    public GradingPeriodReferenceType getGradingPeriodReference() {
        return gradingPeriod;
    }

    /**
     * Sets the value of the gradingPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link GradingPeriodReference }
     *     
     */
    public void setGradingPeriodReference(GradingPeriodReferenceType value) {
        this.gradingPeriod = value;
    }


}
