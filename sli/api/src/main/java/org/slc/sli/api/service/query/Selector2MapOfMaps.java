package org.slc.sli.api.service.query;

import org.slc.sli.api.selectors.model.SelectorParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selector2MapOfMaps implements SelectionConverter {
    
    public static final String SELECTOR_REGEX_STRING = ":\\((.*)\\)";
    public static final Pattern SELECTOR_PATTERN = Pattern.compile(SELECTOR_REGEX_STRING);
    
    public Map<String, Object> convert(String selectorString) throws SelectorParseException {
        Map<String, Object> converted = new HashMap<String, Object>();
        
        Matcher matcher = SELECTOR_PATTERN.matcher(selectorString);
        
        if (matcher.find()) {
            
            int groups = matcher.groupCount();
            
            for (int i=0; i<groups; i++) {
                String data = matcher.group(i+1);
                System.out.println(data);
                while(data.isEmpty() == false) {
                    int indexOfComma = data.indexOf(",");
                    int indexOfParen = data.indexOf("(");
                    if (indexOfComma == -1 && indexOfParen == -1) {
                        addValueToMap(data, converted);
                        data = "";
                    } else if (indexOfComma == -1) {
                        String key = data.substring(0, indexOfParen - 1);
                        String value = data.substring(indexOfParen - 1);
                        addKeyValueToMap(key.replaceAll(" ", ""), convert(value), converted);
                        data = "";
                    } else if (indexOfParen == -1) {
                        String value = data.substring(0, indexOfComma);
                        addValueToMap(value, converted);
                        data = data.substring(indexOfComma + 1);
                    } else if (indexOfComma < indexOfParen) {
                        String value = data.substring(0, indexOfComma);
                        addValueToMap(value, converted);
                        data = data.substring(indexOfComma + 1);
                    } else {
                        int endOfSubMap = getMatchingClosingParenIndex(data, indexOfParen);
                        String key = data.substring(0, indexOfParen - 1);
                        String value = data.substring(indexOfParen - 1, endOfSubMap + 1);
                        addKeyValueToMap(key.replaceAll(" ", ""), convert(value), converted);
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
    
    private static void addKeyValueToMap(String key, Object value, Map<String, Object> map) throws SelectorParseException {
        if (key.contains("(") || key.contains(")")) {
            throw new SelectorParseException("Parentheses in key");
        } else if (key.isEmpty()) {
            throw new SelectorParseException("Key was empty string");
        } else if (map == null) {
            throw new SelectorParseException("Cannot add value to mapping");
        }
        
        map.put(key, value);
    }
    
    /**
     * Checks the value to see if it contains both a key and a value and then
     * adds the determined key and value to the map.
     * 
     * @param value key, key:true, or key:false. key defaults to key:true
     * @param map map to add key and boolean value to
     */
    private static void addValueToMap(String value, Map<String, Object> map) throws SelectorParseException {
        if (value.isEmpty()) {
            throw new SelectorParseException("key/value cannot be empty string");
        }
        
        int indexOfColon = value.indexOf(":");
        
        if(indexOfColon != -1) {
            String key = value.substring(0, indexOfColon).replaceAll(" ", "");
            boolean keyValue = Boolean.parseBoolean(value.substring(indexOfColon + 1));
            addKeyValueToMap(key, keyValue, map);
        } else {
            addKeyValueToMap(value, true, map);
        }
    }
    
}
