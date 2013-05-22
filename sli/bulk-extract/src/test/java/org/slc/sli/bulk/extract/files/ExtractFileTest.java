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

import junit.framework.Assert;
import junitx.util.PrivateAccessor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.files.metadata.ManifestFile;
import org.slc.sli.bulk.extract.files.writer.JsonFileWriter;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * JUnit tests for ArchivedExtractFile class.
 * @author npandey
 *
 */
public class ExtractFileTest {

    ExtractFile archiveFile;

    private static final String FILE_NAME = "Test";
    private static final String FILE_EXT = ".tar";

    private static final String testApp = "testApp";

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw1KLTcuf8OpvbHfwMJks\n" +
            "UAXbeaoVqZiK/CRhttWDmlMEs8AubXiSgZCekXeaUqefK544BOgeuNgQmMmo0pLy\n" +
            "j/GoGhf/bSZH2tsx1uKneCUm9Oq1g+juw5HmBa14H914tslvriFpJvN0b7q53Zey\n" +
            "AOxuD06l94UMj7wnMiNypEhowIMyVMMCRR9485hC8YsRtGB+f607bB440+d5zjG8\n" +
            "HGofzWZoCWGR70gJkkOZhwtLw+njpIhmnjDyknngUsOaX1Gza5Fzuz0QtVc/iVHg\n" +
            "iSFSz068XR5+zUmTI3cns6QbGnbsajuaTNQiZUHmQ8LOCddAfZz/7incsD9D9Jfb\n" +
            "YwIDAQAB\n";
    private static final String PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDDUotNy5/w6m9sd/AwmSxQBdt5qhWpmIr8JGG21YOaUwSzwC5teJKBkJ6Rd5pSp58rnjgE6B642BCYyajSkvKP8agaF/9tJkfa2zHW4qd4JSb06rWD6O7DkeYFrXgf3Xi2yW+uIWkm83Rvurndl7IA7G4PTqX3hQyPvCcyI3KkSGjAgzJUwwJFH3jzmELxixG0YH5/rTtsHjjT53nOMbwcah/NZmgJYZHvSAmSQ5mHC0vD6eOkiGaeMPKSeeBSw5pfUbNrkXO7PRC1Vz+JUeCJIVLPTrxdHn7NSZMjdyezpBsaduxqO5pM1CJlQeZDws4J10B9nP/uKdywP0P0l9tjAgMBAAECggEACXTko7aZHsvq6yB/c4rm91ThRGm0tMpa6ExGotiBj6Y3UxCZ7tjolvdOhhJ5WUkeTrlRUwN+AUsMuqkA0Hkm30s+7Ux+JGW3EuSL7DB7FTkPQspeUW2kqblVnq7AYyKQ5qCoFJEviyA8YfBzcUQX7S2FQp53MJ2zdv4QE8Bdm5CEPiTjA8F0eOeA8awQfPK3W4JPPZkhVErb9ie0Tj18xARpmI8llI7s6kAU51qmFHvi51l8nqTNCbVxxfRPACT5NUr4qkD2fhaGaFqMekJz8aKvIEUBc37BBe1PmaRvQKZGc+GgpPkJc9xqVEhfihm2HHcfhsA7HvrMeYFd12tDCQKBgQDo2vwqOdYn/L8z9if9B1+qYw/ETJ6OMwXN+1yajpZm7RT+tj4uvsoqo88G/VYIb5ZzaXT7xtLwINEgl8G1PdIKFwScv9fxVOgjuZpPKdJuvplSzqJXSP7Ok/u1SUGmtN3oGbBG8r+N72dvDB1gt4daaZka6VuK8NOJn6BTHxefrQKBgQDWvIf8rWdOwbjJ22P/KB2px8gTCVDCkb+RP63uVxoARJ4tMeIZ2YRTHEZUhKqlvDWPZh+1l2bnzhjIQ/iOHwfiO4oBg6gFqceIdwWQs335z842JKV6lHhlN2vrAdIIc46uwTFs3HEfQKHalVrA4/4eAmmNv4UokWSRfL7xaoKJTwKBgQCAQQV9OIf1VGf39dAGtQYDMjbf9xep2P6MerOByaGbpV/X/4b2dk2h+MGx5t15HgUvIlm1x8gtTNYC7rNZ4WgL+Kuorp4BJbQK4VLV4YIvTznh+0A9dU4reCS+sE/Bw4MqMOP/3/qT8dX1uyV/PPcHXHxg70FloMnS1qIWxlxbrQKBgQCYYUz2p26J2rpws7iwFh2Gn3iA2blveNHCFrgsS67txcOhOqbBxTM7bvMRgts9pOM1ETkrOXcSw5OeeW1mHOsRRULXdD/FVQd89UkDt/uLTEV+8l5jL/yHht6T88TBro7vv7R9FalIjirM2/N8sc1gKkIRDnlFoncFLsqosfZTzQKBgQDhkt/sWJsnMQ4TlcIFDgzmAE3D5YePJW3oN+FBye+6ukB4OZAsF9I7OAF3ibbeVSQ8CXD7BuJFJenFazguD3zydreCsRmuEIkswg2mROsBnci3Jq3omnKsfR8V014PCTaRX39VDCvmTuKSk39zFibioWb74r+jAF6IRUVtu0A4hQ==";

    private Map<String, PublicKey> clientKeys = new HashMap<String, PublicKey>();
    /**
     * Runs before JUnit tests and does the initiation work for the tests.
     * @throws IOException
     *          if an I/O error occurred
     * @throws NoSuchFieldException
     *          if a field is not found
     */
    @Before
    public void init() throws Exception {
        Map<String, PublicKey> appKey = new HashMap<String, PublicKey>();
		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(PUBLIC_KEY));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey publicKey = kf.generatePublic(spec);

        SecurityEventUtil securityEventUtil = Mockito.mock(SecurityEventUtil.class);
        SecurityEvent event = createSE();
        Mockito.when(securityEventUtil.createSecurityEvent(Mockito.anyString(), Mockito.anyString(), Mockito.eq(LogLevelType.TYPE_INFO), Mockito.anyString(), Mockito.any(BEMessageCode.class), Mockito.anyString())).thenReturn(event);
        Mockito.when(securityEventUtil.createSecurityEvent(Mockito.anyString(), Mockito.anyString(), Mockito.eq(LogLevelType.TYPE_ERROR), Mockito.any(BEMessageCode.class))).thenReturn(event);

        appKey.put(testApp, publicKey);
        archiveFile = new ExtractFile(new File("./"), FILE_NAME, clientKeys, securityEventUtil);
        archiveFile.setClientKeys(appKey);

        File parentDir = (File) PrivateAccessor.getField(archiveFile, "tempDir");

        ManifestFile metaFile = new ManifestFile(parentDir);
        metaFile.generateMetaFile(new DateTime());
        Assert.assertTrue(metaFile.getFile() != null);
        Assert.assertTrue(metaFile.getFile().getName() != null);
        PrivateAccessor.setField(archiveFile, "manifestFile", metaFile);

        Map<String, JsonFileWriter> files = new HashMap<String, JsonFileWriter>();
        File studentFile = File.createTempFile("student", ".json.gz", parentDir);
        String fileNamePrefix = studentFile.getName().substring(0, studentFile.getName().indexOf(".json.gz"));
        JsonFileWriter studentExtractFile = new JsonFileWriter(parentDir, fileNamePrefix);
        PrivateAccessor.setField(studentExtractFile, "file", studentFile);


        files.put("student", studentExtractFile);
        PrivateAccessor.setField(archiveFile, "dataFiles", files);
    }

    /**
     * Performs cleanup after test run.
     */
    @After
    public void cleanup() {
        FileUtils.deleteQuietly(new File(FILE_NAME + FILE_EXT));
        String fileName = archiveFile.getFileName(testApp);
        FileUtils.deleteQuietly(new File(fileName));
    }

    /**
     * Test generation of archive file.
     * @throws Exception
     */
    @Test
    public void generateArchiveTest() throws Exception {
        String fileName = archiveFile.getFileName(testApp);
        archiveFile.generateArchive();

        TarArchiveInputStream tarInputStream = null;
        List<String> names = new ArrayList<String>();

        File decryptedFile = null;
        try {
            decryptedFile = decrypt(new File(fileName));
            tarInputStream = new TarArchiveInputStream(new FileInputStream(decryptedFile));


            TarArchiveEntry entry = null;
            while((entry = tarInputStream.getNextTarEntry())!= null) {
                names.add(entry.getName());
            }
        } finally {
            IOUtils.closeQuietly(tarInputStream);
        }

        Assert.assertEquals(2, names.size());
        Assert.assertTrue("Student extract file not found", names.get(1).contains("student"));
        Assert.assertTrue("Metadata file not found", names.get(0).contains("metadata"));
        FileUtils.deleteQuietly(decryptedFile);
    }

    private static File decrypt(File file) throws Exception {
        byte[] responseData = FileUtils.readFileToByteArray(file);

        byte[] encodediv = Arrays.copyOfRange(responseData, 0, 256);
        byte[] encodedsecret = Arrays.copyOfRange(responseData, 256, 512);
        byte[] message = Arrays.copyOfRange(responseData, 512, responseData.length);

        Cipher decrypt = Cipher.getInstance("RSA");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(PRIVATE_KEY.getBytes()));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        decrypt.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] iv = decrypt.doFinal(encodediv);
        byte[] secret = decrypt.doFinal(encodedsecret);

        decrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(secret, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        decrypt.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] unencryptedMessage = decrypt.doFinal(message);

        File decryptedTar = new File("decrypted-" + FILE_NAME + FILE_EXT);

        FileUtils.writeByteArrayToFile(decryptedTar, unencryptedMessage);

        return decryptedTar;

    }

    private SecurityEvent createSE() {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setClassName(this.getClass().getName());
        securityEvent.setLogMessage("Test Message");
        securityEvent.setLogLevel(LogLevelType.TYPE_TRACE);

        return securityEvent;
    }

}
