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
package org.slc.sli.bulk.extract.File;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.util.IOUtils;

import org.slc.sli.bulk.extract.metadata.ManifestFile;

/**
 * Extract's archive file class.
 *
 * @author npandey
 *
 */
public class ArchivedExtractFile {

    private String parentDirName;
    private File archiveFile;
    private List<File> filesToArchive = new ArrayList<File>();

    private static final String FILE_EXT = ".tar";

    /**
     *Parameterized constructor.
     *
     * @param parentDirName
     *          parent directory name
     * @param archiveName
     *          name of the archive file
     * @throws IOException
     */
    public ArchivedExtractFile(String parentDirName, String archiveName) throws IOException {
        this.parentDirName = parentDirName;
        archiveFile = new File(parentDirName, archiveName + FILE_EXT);
        archiveFile.createNewFile();
    }

    /**
     *Get a data file for the extract.
     *
     * @param fileName
     *          name of the data file
     * @return
     *          DataExtractFile object
     * @throws FileNotFoundException
     * @throws IOException
     */
    public DataExtractFile getDataFileEntry(String fileName) throws FileNotFoundException, IOException {
        DataExtractFile compressedFile = new DataExtractFile(parentDirName, fileName);
       filesToArchive.add(compressedFile.getFile());
       return compressedFile;
    }

    /**
     * Get a metadata file for the extract.
     *
     * @return
     *      ManifestFile object
     * @throws IOException
     */
    public ManifestFile getManifestFile() throws IOException {
        ManifestFile manifestFile = new ManifestFile(parentDirName);
        filesToArchive.add(manifestFile.getFile());
        return manifestFile;
    }

    /**
     * Generates the archive file for the extract.
     *
     * @throws IOException
     */
    public void generateArchive() throws IOException {
        TarArchiveOutputStream tarArchiveOutputStream = null;
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(
                    new FileOutputStream(archiveFile));

            for (File file : filesToArchive) {
                tarArchiveOutputStream.putArchiveEntry(tarArchiveOutputStream
                        .createArchiveEntry(file, file.getName()));
                FileUtils.copyFile(file, tarArchiveOutputStream);
                tarArchiveOutputStream.closeArchiveEntry();
                file.delete();
            }
        } finally {
            IOUtils.close(tarArchiveOutputStream);
        }
    }

    /**
     * Getter method for archive file.
     * @return
     *      returns a File object
     */
    public File getArchiveFile() {
        return archiveFile;
    }

}
