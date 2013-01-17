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

package org.slc.sli.ingestion.reporting.impl;

import java.text.MessageFormat;

/**
 *
 * @author slee
 *
 */
public class NeutralRecordSource extends JobSource {

    private int visitBeforeLineNumber;
    private int visitBeforeColumnNumber;
    private int visitAfterLineNumber;
    private int visitAfterColumnNumber;
    private String neutralRecordType;

    public NeutralRecordSource(String resourceId, String stageName, int visitBeforeLineNumber,
            int visitBeforeColumnNumber) {

        super(resourceId, stageName);
        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
    }

    public NeutralRecordSource(String resourceId, String stageName, String type,
            int visitBeforeLineNumber, int visitBeforeColumnNumber,
            int visitAfterLineNumber, int visitAfterColumnNumber) {

        super(resourceId, stageName);
        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
        this.visitAfterLineNumber = visitAfterLineNumber;
        this.visitAfterColumnNumber = visitAfterColumnNumber;
        this.neutralRecordType = type;
    }

    public void updateSourceLocation(int visitBeforeLineNumber, int visitBeforeColumnNumber, int visitAfterLineNumber,
            int visitAfterColumnNumber) {

        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
        this.visitAfterLineNumber = visitAfterLineNumber;
        this.visitAfterColumnNumber = visitAfterColumnNumber;
    }

    @Override
    public String getUserFriendlyMessage() {
        Object[] arguments = { new Integer(visitBeforeLineNumber), new Integer(visitBeforeColumnNumber),
                new Integer(visitAfterLineNumber), new Integer(visitAfterColumnNumber) };

        return (visitBeforeLineNumber == 0 && visitBeforeColumnNumber == 0) ? "" : MessageFormat.format("Element:"
                + "line-{0,number,integer}," + "column-{1,number,integer}", arguments);
    }

}
