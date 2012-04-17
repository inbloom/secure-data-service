package org.slc.sli.common.util.threadutil;

import java.util.Hashtable;

/**
 * ThreadLocal storage
 *
 * @author ifaybyshev
 *
 */
public class ThreadLocalStorage extends Hashtable<Object, Object> {

    /** Class version. */
    private static final long serialVersionUID = 1L;

    private ThreadLocal<Object> holder = new ThreadLocal<Object>();

    private String specialKey;

    /**
     * Construct a new thread local storage, accessed by special key
     * @param specialKey key used to lookup thread local storage.
     */
    public ThreadLocalStorage(String specialKey) {
        super();
        this.holder.set(null);
        this.specialKey = specialKey;
    }

    @Override
    public synchronized Object get(Object key) {
        if ((specialKey != null) && specialKey.equals(key)) {
                return holder.get();
        }
        return super.get(key);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        if ((specialKey != null) && specialKey.equals(key)) {
                holder.set(value);
        }
        return super.put(key, value);
    }

}
