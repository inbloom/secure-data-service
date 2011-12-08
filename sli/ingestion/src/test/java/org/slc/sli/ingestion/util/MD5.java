package org.slc.sli.ingestion.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import org.slc.sli.ingestion.landingzone.LandingZone;

public class MD5 {
    public static String calculate(String fileName, LandingZone lz) {
        String md5 = "";

        File f = lz.getFile(fileName);

        if (f != null) {
            md5 = calculate(f);
        }

        return md5;
    }

    public static String calculate(File f) {
        String md5 = "";

        DigestInputStream dis = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            dis = new DigestInputStream(new FileInputStream(f), md);

            byte[] buf = new byte[1024];

            while (dis.read(buf, 0, 1024) != -1) {
            }

            md5 = Hex.encodeHexString(dis.getMessageDigest().digest());
        } catch (NoSuchAlgorithmException e) {
            md5 = "";
        } catch (IOException e) {
            md5 = "";
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    dis = null;
                }
            }
        }

        return md5;
    }
}
