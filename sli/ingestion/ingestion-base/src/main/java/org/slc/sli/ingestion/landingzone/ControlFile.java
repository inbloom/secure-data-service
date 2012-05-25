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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(ControlFile.class);


    protected File file;
    protected List<IngestionFileEntry> fileEntries = new ArrayList<IngestionFileEntry>();
    protected Properties configProperties = new Properties();

    public static ControlFile parse(File file) throws IOException, SubmissionLevelException {
        return parse(file, null, null);
    }

    public static ControlFile parse(File file, LandingZone landingZone, MessageSource messageSource) throws IOException, SubmissionLevelException {

        Scanner scanner = new Scanner(file);
        Pattern fileItemPattern = Pattern.compile("^([^\\s^,]+)\\,([^\\s^,]+)\\,([^,]+)\\,(\\w+)\\s*$");
        Matcher fileItemMatcher;
        Pattern configItemPattern = Pattern.compile("^@(.*)$");
        Matcher configItemMatcher;
        String line;
        FileFormat fileFormat;
        FileType fileType;
        int lineNumber = 0;

        String topLevelLandingZonePath = null;
        if (landingZone != null) {
            topLevelLandingZonePath = landingZone.getLZId();
        }

        Properties configProperties = new Properties();
        ArrayList<IngestionFileEntry> fileEntries = new ArrayList<IngestionFileEntry>();

        LOG.debug("parsing control file: {}", file);

        try {
            while (scanner.hasNextLine()) {

                line = scanner.nextLine();
                LOG.debug("scanned next line: {}", line);

                fileItemMatcher = fileItemPattern.matcher(line);
                if (fileItemMatcher.matches()) {
                    fileFormat = FileFormat.findByCode(fileItemMatcher.group(1));
                    fileType = FileType.findByNameAndFormat(fileItemMatcher.group(2), fileFormat);
                    fileEntries.add(new IngestionFileEntry(fileFormat, fileType, fileItemMatcher.group(3),
                            fileItemMatcher.group(4), topLevelLandingZonePath));
                    lineNumber += 1;
                    continue;
                }

                configItemMatcher = configItemPattern.matcher(line);
                if (configItemMatcher.matches()) {
                    configProperties.load(new ByteArrayInputStream(configItemMatcher.group(1).trim().getBytes()));
                    // System.err.println("found configItem: ["+configItemMatcher.group(1)+"]");
                    lineNumber += 1;
                    continue;
                }

                // blank lines are ignored silently, but stray marks are not
                if (line.trim().length() > 0) {
                    // line was not parseable
                    lineNumber += 1;
                    String errorMessage;
                    if (messageSource != null) {
                         errorMessage = MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG16", lineNumber, line);
                    } else {
                        errorMessage = "Invalid control file entry at line number [" + lineNumber + "] Line:" + line;
                    }
                    throw new SubmissionLevelException(errorMessage);
                }
            }

            return new ControlFile(file, fileEntries, configProperties);

        } finally {
            scanner.close();
        }
    }

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
