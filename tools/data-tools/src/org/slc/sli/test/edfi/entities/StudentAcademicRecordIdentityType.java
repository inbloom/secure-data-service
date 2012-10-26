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
 * Encapsulates the possible attributes that can be used to lookup the studentAcademicRecord.
 * 
 * <p>
 * Java class for StudentAcademicRecordIdentityType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StudentAcademicRecordIdentityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StudentReference" type="{http://ed-fi.org/0100}SLC-StudentReferenceType"/>
 *         &lt;element name="SessionReference" type="{http://ed-fi.org/0100}SLC-SessionReferenceType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudentAcademicRecordIdentityType", propOrder = { "studentReference", "sessionReference" })
public class StudentAcademicRecordIdentityType {

    @XmlElement(name = "StudentReference", required = true)
    protected StudentReferenceType studentReference;
    @XmlElement(name = "SessionReference", required = true)
    protected SessionReferenceType sessionReference;

    /**
     * Gets the value of the studentReference property.
     * 
     * @return
     *         possible object is {@link StudentReferenceType }
     * 
     */
    public StudentReferenceType getStudentReference() {
        return studentReference;
    }

    /**
     * Sets the value of the studentReference property.
     * 
     * @param value
     *            allowed object is {@link StudentReferenceType }
     * 
     */
    public void setStudentReference(StudentReferenceType value) {
        this.studentReference = value;
    }

    /**
     * Gets the value of the sessionReference property.
     * 
     * @return
     *         possible object is {@link SessionReferenceType }
     * 
     */
    public SessionReferenceType getSessionReference() {
        return sessionReference;
    }

    /**
     * Sets the value of the sessionReference property.
     * 
     * @param value
     *            allowed object is {@link SessionReferenceType }
     * 
     */
    public void setSessionReferenceType(SessionReferenceType value) {
        this.sessionReference = value;
    }

}