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


package test.camel.support;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.xml.stream.XMLEventReader;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

/**
 * @author okrook
 *
 */
@Converter
public final class ZipFileEntryTypeConverter {

    @Converter
    public static InputStream toInputStream(ZipFileEntry zipFileEntry, Exchange exchange) throws IOException {
        ZipArchiveInputStream zis = null;
        InputStream fileInputStream = null;

        try {
            // Create input stream
            zis = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFileEntry.getZipFilePath())));

            ArchiveEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(zipFileEntry.getFileEntry())) {
                    fileInputStream = zis;
                    break;
                }
            }
        } finally {
            if (fileInputStream == null) {
                IOUtils.closeQuietly(zis);
            }
        }

        if (fileInputStream == null) {
            String msg = MessageFormat.format("No file entry is found for {0} withing the {0} archive", zipFileEntry.getFileEntry(), zipFileEntry.getZipFilePath());
            throw new FileNotFoundException(msg);
        }

        return fileInputStream;
    }

    @Converter
    public static XMLEventReader toXMLEventReader(ZipFileEntry zipFileEntry, Exchange exchange) {
        return null;
    }
}
