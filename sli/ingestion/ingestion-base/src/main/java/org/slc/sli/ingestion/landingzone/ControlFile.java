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


package org.slc.sli.ingestion.landingzone;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.MessageSource;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;

/**
 * Represents control file information.
 *
 */
public class ControlFile implements Serializable {

    private static final long serialVersionUID = 3231739301361458948L;

    protected File file;
    protected List<IngestionFileEntry> fileEntries = new ArrayList<IngestionFileEntry>();
    protected Properties configProperties = new Properties();

    public List<IngestionFileEntry> getFileEntries() {
        return this.fileEntries;
    }

    /**
     * @param file
     * @param fileEntries
     * @param configProperties
     */
    protected ControlFile(File file, List<IngestionFileEntry> fileEntries, Properties configProperties) {
        this.file = file;
        this.fileEntries = fileEntries;
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public String getFileName() {
        if (file != null) {
            return file.getName();
        }
        return null;
    }

}
