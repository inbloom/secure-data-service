package org.slc.sli.common.domain;


/**
 * Represents what to do when a duplicate key insertion should instead result in
 * the consolidation of an array data field between the original and duplicating
 * records.
 * 
 * Used in attendance, so that a "duplicate year/school/student" record results in
 * consolidating attendance events (as opposed to overwriting or duplication).
 * 
 * @author kmyers
 *
 */
public class ArrayConsolidation {
    
    //path to array whose entries should be consolidated
    private final String arrayPath;
    
    //how to detect a "duplicate" inside the array
    private final String arrayPkFieldName;
    
    public ArrayConsolidation(String arrayPath, String arrayPkFieldName) {
        this.arrayPath = arrayPath;
        this.arrayPkFieldName = arrayPkFieldName;
    }
    
    public String getArrayPath() {
        return this.arrayPath;
    }
    
    public String getArrayPkFieldName() {
        return this.arrayPkFieldName;
    }
}

