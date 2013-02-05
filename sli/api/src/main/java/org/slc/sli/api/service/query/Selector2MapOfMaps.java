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

package org.slc.sli.api.service.query;

import org.slc.sli.api.selectors.model.SelectorParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Selector2MapOfMaps
 * @author kmyers
 */
public class Selector2MapOfMaps implements SelectionConverter {
    
    public static final String SELECTOR_REGEX_STRING = ":\\((.*)\\)";
    public static final Pattern SELECTOR_PATTERN = Pattern.compile(SELECTOR_REGEX_STRING);
    
    /* Whether a key of "$" should cause an exception to be thrown */
    private boolean errorOnDollarSign;
    
    public Selector2MapOfMaps() {
        this(true);
    }
    
    public Selector2MapOfMaps(boolean errorOnDollarSign) {
        this.errorOnDollarSign = errorOnDollarSign;
    }
    
    public Map<String, Object> convert(String selectorString) throws SelectorParseException {
        Map<String, Object> converted = new HashMap<String, Object>();
        
        Matcher matcher = SELECTOR_PATTERN.matcher(selectorString);
        
        if (matcher.find()) {
            
            int groups = matcher.groupCount();
            
            for (int i = 0; i < groups; i++) {
                String data = matcher.group(i + 1);
                while (!data.isEmpty()) {
                    int indexOfComma = data.indexOf(",");
                    int indexOfParen = data.indexOf("(");
                    if (indexOfComma == -1 && indexOfParen == -1) {
                        parseKeyAndAddValueToMap(data, converted);
                        data = "";
                    } else if (indexOfComma == -1) {
                        String key = data.substring(0, indexOfParen - 1);
                        String value = data.substring(indexOfParen - 1);
                        addKeyValueToMap(key, convert(value), converted);
                        data = "";
                    } else if (indexOfParen == -1) {
                        String value = data.substring(0, indexOfComma);
                        parseKeyAndAddValueToMap(value, converted);
                        data = data.substring(indexOfComma + 1);
                    } else if (indexOfComma < indexOfParen) {
                        String value = data.substring(0, indexOfComma);
                        parseKeyAndAddValueToMap(value, converted);
                        data = data.substring(indexOfComma + 1);
                    } else {
                        int endOfSubMap = getMatchingClosingParenIndex(data, indexOfParen);
                        String key = data.substring(0, indexOfParen - 1);
                        String value = data.substring(indexOfParen - 1, endOfSubMap + 1);
                        addKeyValueToMap(key, convert(value), converted);
                        data = data.substring(endOfSubMap + 1);
                        if (data.startsWith(",")) {
                            data = data.substring(1);
                        }
                    }
                }
            }
        } else {
            throw new SelectorParseException("Invalid selector syntax");
        }
        
        return converted;
    }
    
    protected static int getMatchingClosingParenIndex(String string, int openParenIndex) throws SelectorParseException {
        int balance = 0;
        
        for (int i = openParenIndex; i < string.length(); i++) {
            switch(string.charAt(i)) {
                case '(' : 
                    balance++;
                    break;
                case ')' :
                    balance--;
                    if (balance == 0) {
                        return i;
                    } else if (balance < 0) {
                        throw new SelectorParseException("Invalid parentheses");
                    }
            }
        }
        
        throw new SelectorParseException("Unbalanced parentheses");
    }
    
    /**
     * Checks the value to see if it contains both a key and a value and then
     * adds the determined key and value to the map.
     * 
     * @param keyWithOptionalValue key, key:true, or key:false. key defaults to key:true
     * @param map map to add key and boolean value to
     */
    private void parseKeyAndAddValueToMap(String keyWithOptionalValue, Map<String, Object> map) throws SelectorParseException {
        String key = keyWithOptionalValue;
        Boolean value = true;
        
        //if the key has a colon in it, it contains a value after the colon
        if (keyWithOptionalValue.contains(":")) {
            int indexOfColon = keyWithOptionalValue.indexOf(":");
            key = keyWithOptionalValue.substring(0, indexOfColon);
            String booleanAsString = keyWithOptionalValue.substring(indexOfColon + 1);
            if (booleanAsString.toLowerCase().equals("false")) {
                value = false;
            } else if (!booleanAsString.toLowerCase().equals("true")) {
                throw new SelectorParseException("Invalid boolean value: " + booleanAsString);
            }
        }
        
        addKeyValueToMap(key, value, map);
    }

    private void addKeyValueToMap(String key, Object value, Map<String, Object> map) throws SelectorParseException {
        if (key == null) {
            throw new SelectorParseException("Cannot select null key");
        } else if (value == null) {
            throw new SelectorParseException("Cannot use null in a selection's value. Only true/false/map");
        } else if (map == null) {
            throw new SelectorParseException("Cannot add value to null mapping");
        } else if (key.contains("(") || key.contains(")")) {
            throw new SelectorParseException("Cannot use parentheses in keys");
        } else if (key.isEmpty()) {
            throw new SelectorParseException("Cannot use empty string as a key");
        } else if (this.errorOnDollarSign) {
            if (key.equals("$")) {
                throw new SelectorParseException("Dollar sign not valid selector syntax");
            }
        }
        
        
        map.put(key, value);
    }
    
}
