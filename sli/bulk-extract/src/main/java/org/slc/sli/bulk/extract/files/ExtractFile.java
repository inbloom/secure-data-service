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
package org.slc.sli.bulk.extract.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.files.metadata.ManifestFile;

/**
 * Extract's archive file class.
 *
 * @author npandey
 *
 */
public class ExtractFile {

    private String parentDirName;
    private File tempDir;
    private String archiveName;
    private File archiveFile;
    private List<DataExtractFile> dataFiles = new ArrayList<DataExtractFile>();
    private ManifestFile manifestFile;

    private static final String FILE_EXT = ".tar";

    private static final Logger LOG = LoggerFactory.getLogger(ExtractFile.class);

    /**
     *Parameterized constructor.
     *
     * @param parentDirName
     *          parent directory name
     * @param archiveName
     *          name of the archive file
     */
    public ExtractFile(String parentDirName, String archiveName) {
        this.parentDirName = parentDirName;
        this.archiveName = archiveName;
        tempDir = new File(parentDirName, UUID.randomUUID().toString());
        tempDir.mkdir();
    }

    /**
     *Get a data file for the extract.
     *
     * @param filePrefix
     *          the prefix string to be used in file name generation
     * @return
     *          DataExtractFile object
     * @throws FileNotFoundException
     *          if the data file is not found
     * @throws IOException
     *          if an I/O error occurred
     */
    public DataExtractFile getDataFileEntry(String filePrefix)
            throws FileNotFoundException, IOException {
        DataExtractFile compressedFile = new DataExtractFile(
                tempDir.getAbsolutePath(), filePrefix);
        dataFiles.add(compressedFile);
        return compressedFile;
    }

    /**
     * Get a metadata file for the extract.
     *
     * @return
     *      ManifestFile object
     * @throws IOException
     *          if an I/O error occurred
     */
    public ManifestFile getManifestFile() throws IOException {
        ManifestFile manifestFile = new ManifestFile(tempDir.getAbsolutePath());
        this.manifestFile = manifestFile;
        return manifestFile;
    }

    /**
     * Generates the archive file for the extract.
     *
     * @throws IOException
     *      if an I/O error occurred
     */
    public void generateArchive() throws IOException {
        createTarFile();

        TarArchiveOutputStream tarArchiveOutputStream = null;
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(archiveFile));

            archiveFile(tarArchiveOutputStream, manifestFile.getFile());
            for (DataExtractFile dataFile : dataFiles) {
                File file = new File(tempDir, dataFile.getFileName());
                archiveFile(tarArchiveOutputStream, file);
            }
        } catch (IOException e) {
            LOG.error("Error writing to tar file: {}" + e.getMessage());
            removeTarFile();
        } finally {
            IOUtils.close(tarArchiveOutputStream);
            FileUtils.forceDelete(tempDir);
        }
    }

    private void archiveFile(TarArchiveOutputStream tarArchiveOutputStream, File fileToArchive) throws IOException {
        tarArchiveOutputStream.putArchiveEntry(tarArchiveOutputStream
                .createArchiveEntry(fileToArchive, fileToArchive.getName()));
        FileUtils.copyFile(fileToArchive, tarArchiveOutputStream);
        tarArchiveOutputStream.closeArchiveEntry();
        fileToArchive.delete();
    }

    private void createTarFile() {
        archiveFile = new File(parentDirName, archiveName + FILE_EXT);
        try {
            archiveFile.createNewFile();
        } catch (IOException e) {
            LOG.error("Error creating a tar file");
        }
    }

    private void removeTarFile() {
        LOG.error("Removing tar file");
        archiveFile.delete();
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
