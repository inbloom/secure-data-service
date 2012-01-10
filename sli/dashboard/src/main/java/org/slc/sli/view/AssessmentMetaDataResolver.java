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
    HashSet<AssessmentMetaData> assessmentMetaData;
    
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
        assessmentMetaData = new HashSet<AssessmentMetaData>(a);
        parent = new HashMap<AssessmentMetaData, AssessmentMetaData>();
        children = new HashMap<AssessmentMetaData, HashSet<AssessmentMetaData>>();
        assessments = new HashSet<AssessmentMetaData>();
        allMetaData = new HashMap<String, AssessmentMetaData>(); 
        for (AssessmentMetaData metaData : assessmentMetaData) {
            populateStructures(metaData);
        }
    }
    private void populateStructures(AssessmentMetaData metaData) {
        if (allMetaData.containsKey(metaData.getName())) {
            throw new RuntimeException("Assessment name repeated in assessment metadata. ");
        }
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

    /**
     * Returns true iff assessment family 1 is ancestor of assessment family 2
     */
    public boolean isAncestor(String assFamilyName1, String assFamilyName2) {
        AssessmentMetaData metaData1 = allMetaData.get(assFamilyName1);
        AssessmentMetaData metaData2 = allMetaData.get(assFamilyName2);
        if (metaData1 == null) return false; 
        if (metaData2 == null) return false; 
        AssessmentMetaData metaData = metaData2;
        while (metaData != null) {
            if (metaData == metaData1) { return true; }
            metaData = parent.get(metaData);
        }
        return false;
    }
}
