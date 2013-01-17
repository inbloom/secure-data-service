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

import java.io.File;

import org.slc.sli.ingestion.reporting.Source;

/**
 * 
 * @author slee
 *
 */
public class ZipFileSource extends FileSource
{
    private String zipFilePath;

    public ZipFileSource(Source source, File zipFile)
    {
        super(source == null ? null : source.getResourceId(), source == null ? null : source.getStageName());
        this.zipFilePath = zipFile.getPath();
    }

    public ZipFileSource(String resourceId, String stageName) 
    {
        super(resourceId, stageName);
    }

    @Override
    public String getUserFriendlyMessage() {
        return zipFilePath == null ? super.getUserFriendlyMessage() : zipFilePath;
    }

}
