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


package org.slc.sli.validation.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing a restriction on a value.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public enum Restriction {
    /**
     * regular expression a value must conform to
     */
    PATTERN("pattern"),
    /**
     * exact length of a string or list
     */
    LENGTH("length"),
    /**
     * minimum length of a string or list
     */
    MIN_LENGTH("min-length"),
    /**
     * max length of a string or list
     */
    MAX_LENGTH("max-length"),
    /**
     * minimum value of a number
     */
    MIN_INCLUSIVE("min"),
    /**
     * maximum value of a number
     */
    MAX_INCLUSIVE("max"),
    /**
     * minimum value of a number, exclusive
     */
    MIN_EXCLUSIVE("min-exclusive"),
    /**
     * maximum value of a number, exclusive
     */
    MAX_EXCLUSIVE("max-exclusive");
    
    private final String value;
    
    private static final Map<String, Restriction> LOOKUP = new HashMap<String, Restriction>();
    static {
        for (Restriction r : values()) {
            LOOKUP.put(r.getValue(), r);
        }
    }
    
    private Restriction(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static boolean isRestriction(String value) {
        return LOOKUP.containsKey(value);
    }
    
    public static Restriction fromValue(String value) {
        return LOOKUP.get(value);
    }
}
