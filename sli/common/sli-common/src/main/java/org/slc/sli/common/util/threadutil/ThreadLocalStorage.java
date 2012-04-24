package org.slc.sli.common.util.threadutil;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal storage
 *
 * @author ifaybyshev
 *
 */

public class ThreadLocalStorage {

    private final ThreadLocal<Map<Object, Object>> threadStore;

    public ThreadLocalStorage() {
        this.threadStore = new ThreadLocal<Map<Object, Object>>();
        this.threadStore.set(new HashMap<Object, Object>());
    }

    public synchronized Object get(Object key) {
        return this.threadStore.get().get(key);
    }

    public synchronized Object put(Object key, Object value) {
        return this.threadStore.get().put(key, value);
    }

}
