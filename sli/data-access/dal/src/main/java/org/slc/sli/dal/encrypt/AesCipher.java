package org.slc.sli.dal.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Encrypts/Decrypts data using AES
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class AesCipher implements org.slc.sli.dal.encrypt.Cipher {

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private static final Logger LOG = LoggerFactory.getLogger(AesCipher.class);

    public AesCipher() {
    }

    public AesCipher(SecretKey key, String initializationVector) {
        this.setKeyAndInitializationVector(key, initializationVector);
    }

    @Override
    public String encrypt(Object data) {
        if (data instanceof String) {
            return "ESTRING:"
                    + encryptFromBytes(StringUtils.getBytesUtf8((String) data));
        } else {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(byteOutputStream);
            String type;
            try {
                if (data instanceof Boolean) {
                    dos.writeBoolean((Boolean) data);
                    type = "EBOOL:";
                } else if (data instanceof Integer) {
                    dos.writeInt((Integer) data);
                    type = "EINT:";
                } else if (data instanceof Long) {
                    dos.writeLong((Long) data);
                    type = "ELONG:";
                } else if (data instanceof Double) {
                    dos.writeDouble((Double) data);
                    type = "EDOUBLE:";
                } else {
                    throw new RuntimeException("Unsupported type: "
                            + data.getClass().getCanonicalName());
                }
                dos.flush();
                dos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte[] bytes = byteOutputStream.toByteArray();
            return type + encryptFromBytes(bytes);
        }
    }

    @Override
    public Object decrypt(String data) {
        String[] splitData = org.apache.commons.lang3.StringUtils.split(data,
                ':');
        // String[] splitData = data.split(":");
        if (splitData.length != 2) {
            return null;
        } else {
            if (splitData[0].equals("ESTRING")) {
                return StringUtils.newStringUtf8(decryptToBytes(splitData[1]));
            } else if (splitData[0].equals("EBOOL")) {
                return decryptBinary(splitData[1], Boolean.class);
            } else if (splitData[0].equals("EINT")) {
                return decryptBinary(splitData[1], Integer.class);
            } else if (splitData[0].equals("ELONG")) {
                return decryptBinary(splitData[1], Long.class);
            } else if (splitData[0].equals("EDOUBLE")) {
                return decryptBinary(splitData[1], Double.class);
            } else {
                return null;
            }
        }
    }

    private Object decryptBinary(String data, Class<?> expectedType) {
        byte[] decoded = decryptToBytes(data);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
                decoded));
        try {
            if (Boolean.class.equals(expectedType)) {
                return dis.readBoolean();
            } else if (Integer.class.equals(expectedType)) {
                return dis.readInt();
            } else if (Long.class.equals(expectedType)) {
                return dis.readLong();
            } else if (Double.class.equals(expectedType)) {
                return dis.readDouble();
            } else {
                throw new RuntimeException("Unsupported type: "
                        + expectedType.getCanonicalName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                dis.close();
            } catch (IOException e) {
                LOG.error("Unable to close DataInputStream!");
            }
        }
    }

    private String encryptFromBytes(byte[] data) {
        try {

            byte[] encrypted = this.encryptCipher.doFinal(data);
            String encodedData = Base64.encodeBase64String(encrypted);
            return encodedData;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decryptToBytes(String encodedData) {
        byte[] data = Base64.decodeBase64(encodedData);
        try {
            byte[] decrypted = this.decryptCipher.doFinal(data);
            return decrypted;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    // Support for DI
    String keyStore;
    String propertiesFile;

    @Autowired
    ApplicationContext springContext;

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public void setPropertiesFile(String propFile) {
        this.propertiesFile = propFile;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, UnrecoverableKeyException {

        String keyStorePass, keyAlias, keyPass, initializationVector;

        FileInputStream propStream = null;
        try {
            propStream = new FileInputStream(new File(propertiesFile));
            Properties props = new Properties();
            props.load(propStream);
            keyStorePass = props.getProperty("sli.encryption.keyStorePass");
            keyAlias = props.getProperty("sli.encryption.dalKeyAlias");
            keyPass = props.getProperty("sli.encryption.dalKeyPass");
            initializationVector = props
                    .getProperty("sli.encryption.dalInitializationVector");
        } finally {
            if (propStream != null) {
                propStream.close();
            }
        }

        if (keyStorePass == null) {
            throw new RuntimeException(
                    "No key store password found in properties file.");
        }
        if (keyAlias == null) {
            throw new RuntimeException("No key alias found in properties file");
        }
        if (keyPass == null) {
            throw new RuntimeException(
                    "No key password found in properties file");
        }
        if (initializationVector == null) {
            throw new RuntimeException("No initialization vector provided");
        }

        KeyStore ks = KeyStore.getInstance("JCEKS");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(keyStore));
            ks.load(fis, keyStorePass.toCharArray());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        Key key = ks.getKey(keyAlias, keyPass.toCharArray());
        if (!(key instanceof SecretKey)) {
            throw new RuntimeException("Expected key of type SecretKey, got "
                    + key.getClass());
        }
        this.setKeyAndInitializationVector((SecretKey) key,
                initializationVector);
    }

    protected void setKeyAndInitializationVector(SecretKey key,
            String initializationVector) {

        byte[] ivBytes;
        try {
            ivBytes = Hex.decodeHex(initializationVector.toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        IvParameterSpec ivspec = new IvParameterSpec(ivBytes);

        try {
            this.encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivspec);

            this.decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.decryptCipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
