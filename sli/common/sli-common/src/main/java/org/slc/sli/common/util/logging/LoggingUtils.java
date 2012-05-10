package org.slc.sli.common.util.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utilities for BatchJob
 *
 * @author bsuzuki
 *
 */
public class LoggingUtils {

    private static InetAddress localhost;
    static {
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHostAddress() {
        return localhost.getHostAddress();
    }

    public static String getHostName() {
        return localhost.getHostName();
    }

    public static String getCanonicalHostName() {
        return localhost.getCanonicalHostName();
    }
}
