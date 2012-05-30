package org.slc.sli.security;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;

/**
 * Class used to decrypt properties
 *
 * @author svankina
 *
 */
public class PropertiesDecryptor {

    private String decryptedClientId, decryptedClientSecret;
    private Cipher encryptCipher, decryptCipher;
    private static final String INIT_VECTOR = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final String PADDING_CONSTANT = "AES/CBC/PKCS5Padding";
    private static final String KEYSTORE_TYPE = "JCEKS";

    /**
     * Loads a Keystore, and decrypts the clientId, and clientSecret
     *
     * @param keyStore
     * @param clientId
     * @param clientSecret
     * @param alias
     * @param keyStorePassword
     * @param aliasPassword
     * @throws Exception
     */
    public PropertiesDecryptor(String keyStore, String clientId, String clientSecret, String alias,
            String keyStorePassword, String aliasPassword) throws Exception {

        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());

        SecretKey secretKey = (SecretKey) ks.getKey(alias, aliasPassword.toCharArray());

        byte[] ivBytes = Hex.decodeHex(INIT_VECTOR.toCharArray());
        IvParameterSpec ivspec = new IvParameterSpec(ivBytes);

        encryptCipher = Cipher.getInstance(PADDING_CONSTANT);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

        decryptCipher = Cipher.getInstance(PADDING_CONSTANT);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

        decryptedClientId = getDecryptedStringFromCSByteString(clientId);
        decryptedClientSecret = getDecryptedStringFromCSByteString(clientSecret);
    }

    /**
     * Decodes csv byteString to byte array and decrypts to string
     *
     * @param csString
     * @return
     * @throws Exception
     */
    public String getDecryptedStringFromCSByteString(String csString) throws Exception {
        List<String> items = Arrays.asList(csString.split("\\s*,\\s*"));
        byte[] encodedBytes = new byte[items.size()];
        int i = 0;

        for (String item : items) {
            encodedBytes[i++] = Byte.parseByte(item);
        }
        byte[] decodedBytes = decryptCipher.doFinal(encodedBytes);
        return new String(decodedBytes);
    }

    public String getEncryptedByteCSString(String toEncode) throws Exception {
        byte[] encoded = encryptCipher.doFinal(toEncode.getBytes());

        String s = "";
        String delim = "";

        for (byte byteElement : encoded) {
            s += delim + byteElement;
            delim = ",";
        }

        return s;
    }

    public String getDecryptedClientSecret() {
        return decryptedClientSecret;
    }

    public String getDecryptedClientId() {
        return decryptedClientId;
    }

}
