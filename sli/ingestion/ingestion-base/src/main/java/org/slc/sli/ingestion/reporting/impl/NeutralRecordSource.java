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

    private final String neutralRecordType;
    private int visitBeforeLineNumber;
    private int visitBeforeColumnNumber;
    private int visitAfterLineNumber;
    private int visitAfterColumnNumber;

    public NeutralRecordSource(String batchJobId, String resourceId, String stageName, String neutralRecordType) {
        super(batchJobId, resourceId, stageName);
        this.neutralRecordType = neutralRecordType;
    }

    public NeutralRecordSource(String batchJobId, String resourceId, String stageName, String neutralRecordType,
            int visitBeforeLineNumber, int visitBeforeColumnNumber) {

        this(batchJobId, resourceId, stageName, neutralRecordType);
        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
    }

    public NeutralRecordSource(String batchJobId, String resourceId, String stageName, String neutralRecordType,
            int visitBeforeLineNumber, int visitBeforeColumnNumber,
            int visitAfterLineNumber, int visitAfterColumnNumber) {

        this(batchJobId, resourceId, stageName, neutralRecordType);
        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
        this.visitAfterLineNumber = visitAfterLineNumber;
        this.visitAfterColumnNumber = visitAfterColumnNumber;
    }

    public void updateSourceLocation(int visitBeforeLineNumber, int visitBeforeColumnNumber,
            int visitAfterLineNumber, int visitAfterColumnNumber) {

        this.visitBeforeLineNumber = visitBeforeLineNumber;
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
        this.visitAfterLineNumber = visitAfterLineNumber;
        this.visitAfterColumnNumber = visitAfterColumnNumber;
    }

    @Override
    public String getUserFriendlyMessage() {
        Object[] arguments = {
            neutralRecordType,
            new Integer(visitBeforeLineNumber),
            new Integer(visitBeforeColumnNumber),
            new Integer(visitAfterLineNumber),
            new Integer(visitAfterColumnNumber)
        };

        if (visitAfterLineNumber >0 && visitAfterColumnNumber > 0) {
            return MessageFormat.format(
                    "Element: {0} located between " +
                    "Line {1,number,integer}, Column {2,number,integer} and " +
                    "Line {3,number,integer}, Column {4,number,integer}.",
                    arguments);
        } else {
            return MessageFormat.format(
                    "Element: {0} located at " +
                    "Line {1,number,integer}, Column {2,number,integer}.",
                    arguments);
        }
    }


}
