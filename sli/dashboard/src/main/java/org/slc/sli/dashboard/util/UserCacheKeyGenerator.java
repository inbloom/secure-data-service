/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.dashboard.util;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * Generates a cache key using the user's security token, class/method name, and method parameters.
 * The majority of the dashboard caches are user-specific, so we include the security token by default.
 *
 * @author dwu
 *
 */
public class UserCacheKeyGenerator implements KeyGenerator {

    public static final int NO_PARAM_KEY = 0;
    public static final int NULL_PARAM_KEY = 53;

    @Override
    public Object generate(Object target, Method method, Object... params) {

        String token = SecurityUtil.getToken();

        int hashCode = 17;
        hashCode = 31 * hashCode + (token == null ? NULL_PARAM_KEY : token.hashCode());
        hashCode = 31 * hashCode + method.getDeclaringClass().hashCode();
        hashCode = 31 * hashCode + method.getName().hashCode();

        for (Object object : params) {
            hashCode = 31 * hashCode + (object == null ? NULL_PARAM_KEY : object.hashCode());
        }
        return Integer.valueOf(hashCode);
    }
}
