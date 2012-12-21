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


package org.slc.sli.ingestion.reporting;

/**
 * This class encapsulates basic information for reporting
 * the location of an element in the document.
 *
 * @author slee
 *
 */
public final class ElementLocationInfo {

    private final String element;
    private final int visitBeforeLineNumber;
    private final int visitBeforeColumnNumber;
    private final int visitAfterLineNumber;
    private final int visitAfterColumnNumber;


    public ElementLocationInfo(String element, int visitBeforeLineNumber, int visitBeforeColumnNumber,
            int visitAfterLineNumber, int visitAfterColumnNumber) {
        this.element = element;
        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
        this.visitAfterLineNumber = visitAfterLineNumber;
        this.visitAfterColumnNumber = visitAfterColumnNumber;
    }

    public String getElement() {
        return this.element;
    }

    public int getVisitBeforeLineNumber() {
        return this.visitBeforeLineNumber;
    }

    public int getVisitBeforeColumnNumber() {
        return this.visitBeforeColumnNumber;
    }

    public int getVisitAfterLineNumber() {
        return this.visitAfterLineNumber;
    }

    public int getVisitAfterColumnNumber() {
        return this.visitAfterColumnNumber;
    }

    @Override
    public String toString() {
        String s = "Element: <" + element + "/> located between";
        s += " line " + visitBeforeLineNumber;
        s += " column " + visitBeforeColumnNumber;
        s += " and";
        s += " line " + visitAfterLineNumber;
        s += " column " + visitAfterColumnNumber;
        s += ".";
        return s;
    }
}
