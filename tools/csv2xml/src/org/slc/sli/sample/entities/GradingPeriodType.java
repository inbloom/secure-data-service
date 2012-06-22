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


//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.20 at 03:09:04 PM EDT 
//


package org.slc.sli.sample.entities;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GradingPeriodType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GradingPeriodType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="First Six Weeks"/>
 *     &lt;enumeration value="Second Six Weeks"/>
 *     &lt;enumeration value="Third Six Weeks"/>
 *     &lt;enumeration value="Fourth Six Weeks"/>
 *     &lt;enumeration value="Fifth Six Weeks"/>
 *     &lt;enumeration value="Sixth Six Weeks"/>
 *     &lt;enumeration value="First Semester"/>
 *     &lt;enumeration value="Second Semester"/>
 *     &lt;enumeration value="First Summer Session"/>
 *     &lt;enumeration value="Second Summer Session"/>
 *     &lt;enumeration value="Third Summer Session"/>
 *     &lt;enumeration value="Summer Semester"/>
 *     &lt;enumeration value="First Nine Weeks"/>
 *     &lt;enumeration value="Second Nine Weeks"/>
 *     &lt;enumeration value="Third Nine Weeks"/>
 *     &lt;enumeration value="Fourth Nine Weeks"/>
 *     &lt;enumeration value="First Trimester"/>
 *     &lt;enumeration value="Second Trimester"/>
 *     &lt;enumeration value="Third Trimester"/>
 *     &lt;enumeration value="End of Year"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GradingPeriodType")
@XmlEnum
public enum GradingPeriodType {

    @XmlEnumValue("First Six Weeks")
    FIRST_SIX_WEEKS("First Six Weeks"),
    @XmlEnumValue("Second Six Weeks")
    SECOND_SIX_WEEKS("Second Six Weeks"),
    @XmlEnumValue("Third Six Weeks")
    THIRD_SIX_WEEKS("Third Six Weeks"),
    @XmlEnumValue("Fourth Six Weeks")
    FOURTH_SIX_WEEKS("Fourth Six Weeks"),
    @XmlEnumValue("Fifth Six Weeks")
    FIFTH_SIX_WEEKS("Fifth Six Weeks"),
    @XmlEnumValue("Sixth Six Weeks")
    SIXTH_SIX_WEEKS("Sixth Six Weeks"),
    @XmlEnumValue("First Semester")
    FIRST_SEMESTER("First Semester"),
    @XmlEnumValue("Second Semester")
    SECOND_SEMESTER("Second Semester"),
    @XmlEnumValue("First Summer Session")
    FIRST_SUMMER_SESSION("First Summer Session"),
    @XmlEnumValue("Second Summer Session")
    SECOND_SUMMER_SESSION("Second Summer Session"),
    @XmlEnumValue("Third Summer Session")
    THIRD_SUMMER_SESSION("Third Summer Session"),
    @XmlEnumValue("Summer Semester")
    SUMMER_SEMESTER("Summer Semester"),
    @XmlEnumValue("First Nine Weeks")
    FIRST_NINE_WEEKS("First Nine Weeks"),
    @XmlEnumValue("Second Nine Weeks")
    SECOND_NINE_WEEKS("Second Nine Weeks"),
    @XmlEnumValue("Third Nine Weeks")
    THIRD_NINE_WEEKS("Third Nine Weeks"),
    @XmlEnumValue("Fourth Nine Weeks")
    FOURTH_NINE_WEEKS("Fourth Nine Weeks"),
    @XmlEnumValue("First Trimester")
    FIRST_TRIMESTER("First Trimester"),
    @XmlEnumValue("Second Trimester")
    SECOND_TRIMESTER("Second Trimester"),
    @XmlEnumValue("Third Trimester")
    THIRD_TRIMESTER("Third Trimester"),
    @XmlEnumValue("End of Year")
    END_OF_YEAR("End of Year");
    private final String value;

    GradingPeriodType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GradingPeriodType fromValue(String v) {
        for (GradingPeriodType c: GradingPeriodType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
