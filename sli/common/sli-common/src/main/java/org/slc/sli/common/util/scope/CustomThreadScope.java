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
package org.slc.sli.common.util.scope;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.core.NamedThreadLocal;

/**
 * This is a modified version of Spring's {@link SimpleThreadScope} that supports a
 * cleanup method that will clear all the TheadLocal data for the current thread.
 */
public class CustomThreadScope implements Scope {
    private static final Log logger = LogFactory.getLog(SimpleThreadScope.class);

    private static final ThreadLocal<Map<String, Object>> threadScope =
            new NamedThreadLocal<Map<String, Object>>("SimpleThreadScope") {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    public Object get(String name, ObjectFactory objectFactory) {
        Map<String, Object> scope = threadScope.get();
        Object object = scope.get(name);
        if (object == null) {
            object = objectFactory.getObject();
            scope.put(name, object);
        }
        return object;
    }

    public Object remove(String name) {
        Map<String, Object> scope = threadScope.get();
        return scope.remove(name);
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        logger.warn("CustomThreadScope does not support descruction callbacks. " +
                "Consider using a RequestScope in a Web environment.");
    }

    public Object resolveContextualObject(String key) {
        return null;
    }

    public String getConversationId() {
        return Thread.currentThread().getName();
    }
    
    public static void cleanup() {
        threadScope.remove();
    }

}
