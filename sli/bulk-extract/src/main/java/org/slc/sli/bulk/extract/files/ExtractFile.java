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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.files.metadata.ManifestFile;
import org.slc.sli.bulk.extract.files.writer.JsonFileWriter;

/**
 * Extract's archive file class.
 *
 * @author npandey
 *
 */
public class ExtractFile {

    private File tempDir;
    private Map<String, File> archiveFiles = new HashMap<String, File>();
    private Map<String, JsonFileWriter> dataFiles = new HashMap<String, JsonFileWriter>();
    private ManifestFile manifestFile;

    private File parentDir;
    private String archiveName = "";


    private Map<String, String> clientKeys = null;

    private static final String FILE_EXT = ".tar";

    private static final Logger LOG = LoggerFactory.getLogger(ExtractFile.class);

    /**
     *Parameterized constructor.
     *
     * @param parentDir
     *          parent directory
     * @param archiveName
     *          name of the archive file
     */
    public ExtractFile(File parentDir, String archiveName) {
        this.parentDir = parentDir;
        this.archiveName = archiveName;
        this.tempDir = new File(parentDir, UUID.randomUUID().toString());
        this.tempDir.mkdir();
    }

    /**
     *Get a data file for the extract.
     *
     * @param filePrefix
     *          the prefix string to be used in file name generation
     * @return
     *          DataExtractFile object
     */
    public JsonFileWriter getDataFileEntry(String filePrefix) {
        if (!dataFiles.containsKey(filePrefix)) {
         JsonFileWriter compressedFile = new JsonFileWriter(tempDir, filePrefix);
         dataFiles.put(filePrefix, compressedFile);
        }
        return dataFiles.get(filePrefix);
    }

    /**
     *  Closes the files in the extract.
     */
    public void closeWriters() {
        for (JsonFileWriter file : dataFiles.values()) {
            file.close();
        }
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
        ManifestFile manifestFile = new ManifestFile(tempDir);
        this.manifestFile = manifestFile;
        return manifestFile;
    }

    /**
     * Generates the archive file for the extract.
     *
     */
    public void generateArchive() {
        if(clientKeys == null || clientKeys.isEmpty()) {
            LOG.info("No authorized application to extract data.");
            return;
        }

        TarArchiveOutputStream tarArchiveOutputStream = null;
        MultiOutputStream multiOutputStream = new MultiOutputStream();

        try {
            for(String app : clientKeys.keySet()){
                multiOutputStream.addStream(getAppStream(app));
            }

            tarArchiveOutputStream = new TarArchiveOutputStream(multiOutputStream);

            archiveFile(tarArchiveOutputStream, manifestFile.getFile());
            for (JsonFileWriter dataFile : dataFiles.values()) {
                File df = dataFile.getFile();

                if (df != null && df.exists()) {
                    archiveFile(tarArchiveOutputStream, df);
                }
            }
        } catch (Exception e) {
            LOG.error("Error writing to tar file: {}", e.getMessage());
            for(File archiveFile : archiveFiles.values()){
                FileUtils.deleteQuietly(archiveFile);
            }
        } finally {
            IOUtils.closeQuietly(tarArchiveOutputStream);
            FileUtils.deleteQuietly(tempDir);
        }
    }

    private OutputStream getAppStream(String app) throws Exception {
        File archive = new File(parentDir, getFileName(app));
        FileOutputStream f = new FileOutputStream(archive);

        createTarFile(archive);

        archiveFiles.put(app, archive);

        final Pair<Cipher, SecretKey> cipherSecretKeyPair = getCiphers();

        byte[] ivBytes = cipherSecretKeyPair.getLeft().getIV();
        byte[] secretBytes = cipherSecretKeyPair.getRight().getEncoded();

        PublicKey publicKey = getApplicationPublicKey(app);
        byte[] encryptedIV = encryptDataWithRSAPublicKey(ivBytes, publicKey);
        byte[] encryptedSecret = encryptDataWithRSAPublicKey(secretBytes, publicKey);

        f.write(encryptedIV);
        f.write(encryptedSecret);

        CipherOutputStream stream = new CipherOutputStream(f, cipherSecretKeyPair.getLeft());

        return stream;
    }

    public String getFileName(String app){
        return app + "-" + archiveName + FILE_EXT;
    }

    private PublicKey getApplicationPublicKey(String app) throws IOException {

        PublicKey publicKey = null;
        String key = clientKeys.get(app);
        LOG.info("App : {}, Key: {}", app, key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(key));
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Exception: NoSuchAlgorithmException {}", e);
            throw new IOException(e);
        } catch (InvalidKeySpecException e) {
            LOG.error("Exception: InvalidKeySpecException {}", e);
            throw new IOException(e);
        }

        return publicKey;
}

    private static byte[] encryptDataWithRSAPublicKey(byte[] rawData, PublicKey publicKey) {
        byte[] encryptedData = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedData = cipher.doFinal(rawData);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Exception: NoSuchAlgorithmException {}", e);
        } catch (NoSuchPaddingException e) {
            LOG.error("Exception: NoSuchPaddingException {}", e);
        } catch (InvalidKeyException e) {
            LOG.error("Exception: InvalidKeyException {}", e);
        } catch (BadPaddingException e) {
            LOG.error("Exception: BadPaddingException {}", e);
        } catch (IllegalBlockSizeException e) {
            LOG.error("Exception: IllegalBlockSizeException {}", e);
        }

        return encryptedData;
    }


    private static Pair<Cipher, SecretKey> getCiphers() throws Exception {
        SecretKey secret = KeyGenerator.getInstance("AES").generateKey();

        Cipher encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, secret);

        return Pair.of(encrypt, secret);
    }


    private static void archiveFile(TarArchiveOutputStream tarArchiveOutputStream, File fileToArchive) throws IOException {
        tarArchiveOutputStream.putArchiveEntry(tarArchiveOutputStream
                .createArchiveEntry(fileToArchive, fileToArchive.getName()));
        FileUtils.copyFile(fileToArchive, tarArchiveOutputStream);
        tarArchiveOutputStream.closeArchiveEntry();
        fileToArchive.delete();
    }

    private static void createTarFile(File archiveFile) {
        try {
            archiveFile.createNewFile();
        } catch (IOException e) {
            LOG.error("Error creating a tar file");
        }
    }

    /**
     * Getter method for archive file.
     * @return
     *      returns a File object
     */
    public Map<String, File> getArchiveFiles() {
        return archiveFiles;
    }


    /**
     * @return the clientKeys
     */
    public Map<String, String> getClientKeys() {
        return clientKeys;
    }

    /**
     * @param clientKeys the clientKeys to set
     */
    public void setClientKeys(Map<String, String> clientKeys) {
        this.clientKeys = clientKeys;
    }


}
