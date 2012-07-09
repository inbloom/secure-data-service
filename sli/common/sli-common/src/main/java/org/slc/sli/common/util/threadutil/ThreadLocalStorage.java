/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
