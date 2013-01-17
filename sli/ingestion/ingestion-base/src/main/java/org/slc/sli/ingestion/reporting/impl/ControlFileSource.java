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
import org.slc.sli.ingestion.reporting.Source;

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
    private String parentZipFileOrDirectory;
    private String fileEntryName;

    public ControlFileSource(String resourceId, String stageName) {
        super(resourceId, stageName);
    }

    public ControlFileSource(Source source, ControlFile controlFile) {
        super(source == null ? null : source.getResourceId(), source == null ? null : source.getStageName());
        this.controlFileName = controlFile.getFileName();
        this.parentZipFileOrDirectory = controlFile.getParentZipFileOrDirectory();
    }

    public ControlFileSource(Source source, String fileEntryName) {
        super(source == null ? null : source.getResourceId(), source == null ? null : source.getStageName());
        if (source instanceof ControlFileSource) {
            this.controlFileName = ((ControlFileSource) source).controlFileName;
            this.parentZipFileOrDirectory = ((ControlFileSource) source).parentZipFileOrDirectory;
        }
        this.fileEntryName = fileEntryName;
    }

    public ControlFileSource(String resourceId, String stageName, int lineNumber, String line) {
        super(resourceId, stageName);
        this.lineNumber = lineNumber;
        this.line = line;
    }

    @Override
    public String getUserFriendlyMessage() {
        Object[] arguments = { lineNumber, line };
        return (lineNumber == 0 && line == null) ? "" : MessageFormat.format("At line number {0}: {1}", arguments);
    }

}
