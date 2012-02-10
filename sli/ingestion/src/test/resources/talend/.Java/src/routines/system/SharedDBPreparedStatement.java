package routines.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SharedDBPreparedStatement {

    private static boolean DEBUG = false;

    private static SharedDBPreparedStatement instance = null;

    private Map<String, Object> locks = new HashMap<String, Object>();

    private Map<String, Boolean> needToWait = new HashMap<String, Boolean>();

    private Map<String, PreparedStatement> sharedPreparedStatements = new HashMap<String, java.sql.PreparedStatement>();

    private SharedDBPreparedStatement() {

    }

    private static synchronized SharedDBPreparedStatement getInstance() {
        if (instance == null) {
            instance = new SharedDBPreparedStatement();
        }
        return instance;
    }

    private PreparedStatement getPreparedStatement(Connection con, String sql, String key, Object lock)
            throws ClassNotFoundException, SQLException {
        synchronized (lock) {
            while (needToWait.get(key) != null && needToWait.get(key)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (DEBUG) {
                Set<String> keySet = sharedPreparedStatements.keySet();
                System.out.print("sharedPreparedStatements, current shared preparedStatement list is:"); //$NON-NLS-1$
                for (String k : keySet) {
                    System.out.print(" " + k); //$NON-NLS-1$
                }
                System.out.println();
            }

            PreparedStatement preStmt = sharedPreparedStatements.get(key);
            if (preStmt == null) {
                if (DEBUG) {
                    System.out.println("sharedPreparedStatements, can't find the key:" + key + " " //$NON-NLS-1$ //$NON-NLS-2$
                            + "so create a new one and share it."); //$NON-NLS-1$
                }
                preStmt = con.prepareStatement(sql);
                sharedPreparedStatements.put(key, preStmt);
                // } else if (preStmt.isClosed()) {
                // if (DEBUG) {
                //                    System.out.println("sharedPreparedStatements, find the key: " + key + " " //$NON-NLS-1$ //$NON-NLS-2$
                //                            + "But it is closed. So create a new one and share it."); //$NON-NLS-1$
                // }
                // preStmt = con.prepareStatement(sql);
                // sharedPreparedStatements.put(key, preStmt);
            } else {
                if (DEBUG) {
                    System.out.println("sharedPreparedStatements, find the key: " + key + " " + "it is OK."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
            needToWait.put(key, true);
            return preStmt;
        }
    }

    private Object putIfAbsentLock(String key) {
        synchronized (locks) {
            if (locks.get(key) == null) {
                locks.put(key, new Object());
            }
            return locks.get(key);
        }
    }

    public static PreparedStatement getSharedPreparedStatement(Connection con, String sql, String key)
            throws ClassNotFoundException, SQLException {
        SharedDBPreparedStatement instanceLocal = getInstance();
        Object lock = instanceLocal.putIfAbsentLock(key);
        PreparedStatement preparedStatement = instanceLocal.getPreparedStatement(con, sql, key, lock);
        return preparedStatement;
    }

    private void doReleasePreparedStatement(String key, Object lock) {
        synchronized (lock) {
            needToWait.put(key, false);
            lock.notify();
        }
    }

    public static void releasePreparedStatement(String key) {
        SharedDBPreparedStatement instanceLocal = getInstance();
        Object lock = instanceLocal.putIfAbsentLock(key);
        instanceLocal.doReleasePreparedStatement(key, lock);
        if (DEBUG) {
            System.out.println(Thread.currentThread().getId() + "release lock end");
        }
    }

    /**
     * Set the buffer as null, make it recyclable.
     */
    public static void clear() {
        instance = null;
    }

    public static void setDebugMode(boolean debug) {
        DEBUG = debug;
    }
}
