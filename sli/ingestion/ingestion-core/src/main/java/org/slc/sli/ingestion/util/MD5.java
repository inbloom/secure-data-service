package org.slc.sli.ingestion.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import org.slc.sli.ingestion.landingzone.LandingZone;

/**
 * Utility class to calculate MD5 on a file.
 *
 * @author okrook
 *
 */
public class MD5 {
    static final short BUF_SIZE = 4 * 1024;

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
        RandomAccessFile raf = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            raf = new RandomAccessFile(f, "r");

            dis = new DigestInputStream(Channels.newInputStream(raf.getChannel()), md);

            byte[] buf = new byte[BUF_SIZE];

            while (dis.read(buf, 0, BUF_SIZE) != -1) {
            }

            md5 = Hex.encodeHexString(dis.getMessageDigest().digest());
        } catch (NoSuchAlgorithmException e) {
            md5 = "";
        } catch (IOException e) {
            md5 = "";
        } finally {
            IOUtils.closeQuietly(dis);
            IOUtils.closeQuietly(raf);
        }

        return md5;
    }
}
