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

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * A source implementation for control files.
 *
 * @author dduran
 *
 */
public class ControlFileSource extends FileSource {

    private int lineNumber;
    private String line;
    private String controlFileName;
    private String fileEntryName;

    public ControlFileSource(String resourceId) {
        super(resourceId);
    }

    public ControlFileSource(String resourceId, ControlFile controlFile) {
        super(resourceId);
        this.controlFileName = controlFile.getFileName();
    }

    public ControlFileSource(String resourceId, IngestionFileEntry entry) {
        super(resourceId);
        this.fileEntryName = entry.getFileName();
    }

    public ControlFileSource(ControlFile controlFile, int lineNumber, String line) {
        super(controlFile.getFileName());
        this.controlFileName = controlFile.getFileName();
        this.lineNumber = lineNumber;
        this.line = line;
    }

    @Override
    public String getUserFriendlyMessage() {
        Object[] arguments = { getResourceId(), lineNumber, line };
        if (lineNumber > 0 && line != null) {
            return MessageFormat.format("{0}:line-{1}:{2}", arguments);
        } else {
            return super.getUserFriendlyMessage();
        }
    }

}
