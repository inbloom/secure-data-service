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


package org.slc.sli.domain;


/**
 * A single criteria that is independent of any specific database implementation.
 * Criteria must be met for a record to be returned by a query.
 *
 * @author kmyers
 *
 */
public class NeutralCriteria {

    private String key;
    private String operator;
    private Object value;
    private boolean canBePrefixed;

    public static final String CRITERIA_IN = "in";
    public static final String CRITERIA_REGEX = "=~";

    public static final String CRITERIA_GT = ">";
    public static final String CRITERIA_GTE = ">=";
    public static final String CRITERIA_LT = "<";
    public static final String CRITERIA_LTE = "<=";
    public static final String OPERATOR_EQUAL = "=";
    public static final String CRITERIA_EXISTS = "exists";

    static final String[] SUPPORTED_COMPARISON_OPERATORS = new String[] { ">=", "<=", "!=", "=~", "=", "<", ">" };
    
    public NeutralCriteria(String criteria) {
        this.canBePrefixed = true;
        for (String comparisonOperator : NeutralCriteria.SUPPORTED_COMPARISON_OPERATORS) {
            if (criteria.contains(comparisonOperator)) {
                String[] keyAndValue = criteria.split(comparisonOperator);
                this.key = keyAndValue[0];
                this.operator = comparisonOperator;
                this.value = keyAndValue[1];
                if (this.key.equals("")) {
                    throw new RuntimeException("Key is blank: " + criteria);
                }
                return;
            }
        }

        //looped through all known operators and couldn't find a match
        throw new RuntimeException("Unknown query operation: " + criteria);
    }

    public NeutralCriteria(String newKey, String newOperator, Object newValue) {
        this(newKey, newOperator, newValue, true);
    }

    public NeutralCriteria(String newKey, String newOperator, Object newValue, boolean canBePrefixed) {
        this.key = newKey;
        this.operator = newOperator;
        this.value = newValue;
        this.canBePrefixed = canBePrefixed;
    }

    @SuppressWarnings("PMD.UselessOverridingMethod")  // this is overridden because equals is also overridden
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getKey() {
        return this.key;
    }

    public String getOperator() {
        return this.operator;
    }

    public Object getValue() {
        return this.value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean canBePrefixed() {
        return this.canBePrefixed;
    }

    public void setCanBePrefixed(boolean canBePrefixed) {
        this.canBePrefixed = canBePrefixed; 
    }
    
    @Override
    public String toString() {
        return this.key + " " + this.operator + " " + this.value
                + "(can" + (this.canBePrefixed ? " " : " not ") + "be prefixed)";
    }

    private boolean valuesMatch(Object value1, Object value2) {

        //both null? they match
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return value1.equals(value2);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NeutralCriteria) {
            NeutralCriteria nc = (NeutralCriteria) o;
            boolean keysMatch = this.valuesMatch(this.key, nc.key);
            boolean operatorsMatch = this.valuesMatch(this.operator, nc.operator);
            boolean valuesMatch = this.valuesMatch(this.value, nc.value);
            boolean prefixesMatch = (this.canBePrefixed == nc.canBePrefixed);

            return (keysMatch && operatorsMatch && valuesMatch && prefixesMatch);
        }

        return false;
    }
}
