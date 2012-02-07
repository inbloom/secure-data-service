package org.slc.sli.util.threadutil;

import java.util.Hashtable;

/**
 * ThreadLocal storage
 *
 * @author ifaybyshev
 *
 */
public class ThreadLocalStorage extends Hashtable {

    /** Class version. */
    private static final long serialVersionUID = 1L;

    private ThreadLocal holder = new ThreadLocal();

    private String specialKey;

    public ThreadLocalStorage(String specialKey) {
        super();
        this.holder.set(null);
        this.specialKey = specialKey;
    }

    public synchronized Object get(Object key) {
        if ((specialKey != null) && specialKey.equals(key)) {
                return holder.get();
        }
        return super.get(key);
    }

    public synchronized Object put(Object key, Object value) {
        if ((specialKey != null) && specialKey.equals(key)) {
                holder.set(value);
        }
        return super.put(key, value);
    }

}
