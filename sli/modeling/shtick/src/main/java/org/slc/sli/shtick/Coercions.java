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

package org.slc.sli.shtick;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public final class Coercions {
    
    public static final BigInteger toBigInteger(final Object obj) {
        if (obj != null) {
            return new BigInteger(obj.toString());
        } else {
            return null;
        }
    }
    
    public static final Boolean toBoolean(final Object obj) {
        if (obj != null) {
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            } else {
                return Boolean.valueOf(obj.toString());
            }
        } else {
            return null;
        }
    }
    
    public static final Double toDouble(final Object obj) {
        if (obj != null) {
            if (obj instanceof Double) {
                return (Double) obj;
            } else {
                return Double.valueOf(obj.toString());
            }
        } else {
            return null;
        }
    }
    
    public static final Integer toInteger(final Object obj) {
        if (obj != null) {
            return Integer.valueOf(obj.toString());
        } else {
            return null;
        }
    }
    
    public static final Map<String, Object> toMap(final Object obj) {
        if (obj != null) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> map = (Map<String, Object>) obj;
                return map;
            } else {
                throw new IllegalArgumentException("obj");
            }
        } else {
            return null;
        }
    }
    
    public static final List<Object> toList(final Object obj) {
        if (obj != null) {
            if (obj instanceof List) {
                // Creating a temporary variable allows us to localize the warning suppression.
                @SuppressWarnings("unchecked")
                final List<Object> temp = (List<Object>) obj;
                return temp;
            } else {
                throw new IllegalArgumentException("obj");
            }
        } else {
            return null;
        }
    }
    
    public static final String toString(final Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                return (String) obj;
            } else {
                return obj.toString();
            }
        } else {
            return null;
        }
    }
}
