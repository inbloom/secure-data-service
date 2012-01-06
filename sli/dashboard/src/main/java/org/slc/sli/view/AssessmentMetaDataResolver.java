package org.slc.sli.view;

import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.entity.assessmentmetadata.PerfLevel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment meta data passed onto
 *  dashboard views. Contains useful tools look up assessment meta data
 * 
 * @author syau
 *
 */
public class AssessmentMetaDataResolver {
    List<AssessmentMetaData> assessmentMetaData;
    
    // short-cut for parent-child relationship
    HashMap<AssessmentMetaData, AssessmentMetaData> parent;
    HashMap<AssessmentMetaData, HashSet<AssessmentMetaData>> children;
    // leaf nodes
    HashSet<AssessmentMetaData> assessments; 
    // all meta data, searchable by name
    HashMap<String, AssessmentMetaData> allMetaData; 
    
    /**
     * Constructor
     */
    public AssessmentMetaDataResolver(List<AssessmentMetaData> a) {
        assessmentMetaData = a;
        parent = new HashMap<AssessmentMetaData, AssessmentMetaData>();
        children = new HashMap<AssessmentMetaData, HashSet<AssessmentMetaData>>();
        assessments = new HashSet<AssessmentMetaData>();
        allMetaData = new HashMap<String, AssessmentMetaData>(); 
        for (AssessmentMetaData metaData : assessmentMetaData) {
            populateStructures(metaData);
        }
    }
    private void populateStructures(AssessmentMetaData metaData) {
        allMetaData.put(metaData.getName(), metaData);
        if (metaData.getChildren() != null) {
            if (!children.containsKey(metaData)) { children.put(metaData, new HashSet<AssessmentMetaData>()); }
            HashSet<AssessmentMetaData> childrenSet = children.get(metaData);
            List<AssessmentMetaData> childrenMetaData = Arrays.asList(metaData.getChildren()); 
            for (AssessmentMetaData c : childrenMetaData) {
                parent.put(c, metaData);
                childrenSet.add(c);
                populateStructures(c);
            }
        } else {
            assessments.add(metaData);
        }
    }
    
    public PerfLevel findPerfLevelLabelForFamily(String name, String perfLevel) {
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null; 
        while (metaData != null) {
            if (metaData.getPerfLevels() != null) {
                for (PerfLevel pl : Arrays.asList(metaData.getPerfLevels())) {
                    if (pl.getName().equals(name)) {
                        return pl;
                    }
                }
            }
            metaData = parent.get(metaData);
        }
        return null;
    }
    
    public String findWindowEndDateForFamily(String name) {
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null; 
        while (metaData != null) {
            if (metaData.getWindowEnd() != null) {
                return metaData.getWindowEnd();
            }
            metaData = parent.get(metaData);
        }
        return null;
    }

    
    public Integer findNumRealPerfLevelsForFamily(String name) {
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null; 
        while (metaData != null) {
            if (metaData.getPerfLevels() != null) {
                int retVal = 0;
                for (PerfLevel pl : Arrays.asList(metaData.getPerfLevels())) {
                    if (pl.getIsReal()) { retVal++; }
                }
                return retVal;
            }
            metaData = parent.get(metaData);
        }
        return null;
    }
}
