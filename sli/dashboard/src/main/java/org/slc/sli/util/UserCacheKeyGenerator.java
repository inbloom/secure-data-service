package org.slc.sli.util;

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
