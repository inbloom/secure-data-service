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
    
    public static final String[] SUPPORTED_COMPARISON_OPERATORS = new String[] { ">=", "<=", "!=", "=~", "=", "<", ">" };
    
    public NeutralCriteria(String criteria) {
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
        this.key = newKey;
        this.operator = newOperator;
        this.value = newValue;
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
}
