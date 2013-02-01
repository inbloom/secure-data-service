/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 * Represents control file information.
 *
 */
public class ControlFile extends IngestionFileEntry{
    private static final long serialVersionUID = 3231739301361458948L;
    private static final Logger LOG = LoggerFactory.getLogger(ControlFile.class);

    private static final Pattern FE_PATTERN = Pattern.compile("^([^\\s^,]+)\\,([^\\s^,]+)\\,([^,]+)\\,(\\w+)\\s*$");
    private static final Pattern CE_PATTERN = Pattern.compile("^@(.*)$");

    protected List<IngestionFileEntry> fileEntries = new ArrayList<IngestionFileEntry>();
    protected Properties configProperties = new Properties();

    /**
     * Constructor.
     */
    protected ControlFile(String parentZipFileOrDirectory, String fileName) {
        super(parentZipFileOrDirectory, FileFormat.CONTROL_FILE, FileType.CONTROL, fileName, null);
    }

    public static ControlFile parse(String parentZipFileOrDirectory, String controlFileName, AbstractMessageReport report)
            throws IOException, SubmissionLevelException {

        ControlFile cf = new ControlFile(parentZipFileOrDirectory, controlFileName);

        cf.parse(report);

        return cf;
    }

    public void parse(AbstractMessageReport report) throws IOException, SubmissionLevelException {

        InputStream is = null;
        try {
            is = getFileStream();

            List<String> lines = IOUtils.readLines(is);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (StringUtils.isNotBlank(line)) {
                    parse(line, i, report);
                }
            }

        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected void parse(String line, int lineNumber, AbstractMessageReport report) throws IOException, SubmissionLevelException {
        LOG.debug("scanned next line: {}", line);

        IngestionFileEntry fe = parseAsFileEntry(line);
        if (fe != null) {
            fileEntries.add(fe);
            return;
        }

        String ce = parseAsConfigEntry(line);
        if (ce != null) {
            Reader r = new StringReader(ce);
            try {
                configProperties.load(r);
                LOG.info("Control file configuration loaded: {}", ce);
                return;
            } finally {
                IOUtils.closeQuietly(r);
            }
        }

        // line was not parse-able
        report.error(new SimpleReportStats(), new ControlFileSource(this, lineNumber, line), BaseMessageCode.BASE_0016);
        throw new SubmissionLevelException("line was not parseable");
    }

    private IngestionFileEntry parseAsFileEntry(String line) {
        Matcher m = FE_PATTERN.matcher(line);
        if (m.matches()) {
            FileFormat fileFormat = FileFormat.findByCode(m.group(1));
            FileType fileType = FileType.findByNameAndFormat(m.group(2), fileFormat);
            return new IngestionFileEntry(getParentZipFileOrDirectory(), fileFormat, fileType, m.group(3), m.group(4));
        }

        return null;
    }

    private String parseAsConfigEntry(String line) {
        Matcher m = CE_PATTERN.matcher(line);
        if (m.matches()) {
            return m.group(1).trim();
        }

        return null;
    }

    public List<IngestionFileEntry> getFileEntries() {
        return this.fileEntries;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    private synchronized void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        stream.writeObject(configProperties);
        stream.writeObject(fileEntries);
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        configProperties = (Properties) in.readObject();
        fileEntries = (List<IngestionFileEntry>)in.readObject();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((configProperties == null) ? 0 : configProperties.hashCode());
        result = prime * result + ((fileEntries == null) ? 0 : fileEntries.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ControlFile other = (ControlFile) obj;
        if (configProperties == null) {
            if (other.configProperties != null) {
                return false;
            }
        } else if (!configProperties.equals(other.configProperties)) {
            return false;
        }
        if (fileEntries == null) {
            if (other.fileEntries != null) {
                return false;
            }
        } else if (!fileEntries.equals(other.fileEntries)) {
            return false;
        }
        return true;
    }

}
