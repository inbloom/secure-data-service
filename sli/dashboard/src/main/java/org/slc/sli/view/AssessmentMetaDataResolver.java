package org.slc.sli.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.entity.assessmentmetadata.PerfLevel;
import org.slc.sli.entity.assessmentmetadata.Period;
import org.slc.sli.util.Constants;

/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment meta data passed onto
 *  dashboard views. Contains useful tools look up assessment meta data
 *
 * @author syau
 *
 */
public class AssessmentMetaDataResolver {
    /*
    HashSet<AssessmentMetaData> assessmentMetaData;

    // short-cut for parent-child relationship
    HashMap<AssessmentMetaData, AssessmentMetaData> parent;
    HashMap<AssessmentMetaData, HashSet<AssessmentMetaData>> children;
    // leaf nodes
    HashSet<AssessmentMetaData> assessments;
    // all meta data, searchable by name
    HashMap<String, AssessmentMetaData> allMetaData;
*/
    
    List<GenericEntity> assmts;
    
    /**
     * Constructor
     */
    /*
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
    */
    
    public AssessmentMetaDataResolver(List<GenericEntity> assmts) {
        
        this.assmts = assmts;
        /*
        // put assmts into a hash map for easy lookup by id
        assmtMap = new HashMap<String, GenericEntity>();
        
        for (GenericEntity assmt : assmts) {
            assmtMap.put(assmt.getString("assessmentId"), assmt);
        }
        */
    }

    private void populateStructures(AssessmentMetaData metaData) {
        /*
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
        */
    }

    public PerfLevel findPerfLevelForFamily(String name, String perfLevel) {
        /*
        List<PerfLevel> perfLevels = findPerfLevelsForFamily(name);
        if (perfLevels == null) { return null; }
        for (PerfLevel pl : perfLevels) {
            if (pl.getName().equals(name)) {
                return pl;
            }
        }
        */
        return null;
    }

    public List<Period> findPeriodsForFamily(String name) {
        /*
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null;
        // find the possible periods for the assessment family
        while (metaData != null) {
            if (metaData.getPeriods() != null) {
                return new ArrayList<Period>(Arrays.asList(metaData.getPeriods()));
            }
            metaData = parent.get(metaData);
        }
        */
        return null;
    }

    public Period findPeriodForFamily(String name) {
        /*
        List<Period> familyPeriods = findPeriodsForFamily(name);
        if (familyPeriods == null || familyPeriods.isEmpty()) {
            throw new RuntimeException("Malformed assessment meta data: Assessment family " + name + " has no periods data ");
        }
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null;
        // find the possible periods for the assessment family
        while (metaData != null) {
            if (metaData.getPeriod() != null) {
                for (Period p : familyPeriods) {
                    if (metaData.getPeriod().equals(p.getName())) {
                        return p;
                    }
                }
                // if we reached here, the assessment meta data contains a period whose name we don't recognise
                throw new RuntimeException("Malformed assessment meta data: period " + metaData.getPeriod() + " is not recognised for assessment family " + name);
            }
            metaData = parent.get(metaData);
        }
        // if we reached here, the assessment meta data does not contains a period
        // throw new RuntimeException("Malformed assessment meta data: Assessment family " + name + " has no period data associated.");
        */
        return null;
    }

    
    /*
     * Returns a list representing the high cutpoints of the assessment's levels, plus one first
     * element representing the lowest score.
     *
     * Returns null if no cutpoints are found.
     */
    public List<Integer> findCutpointsForFamily(String name) {
    /*
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null;
        // find the possible periods for the assessment family
        while (metaData != null) {
            if (metaData.getCutpoints() != null) {
                List<Integer> retVal = new ArrayList<Integer>();
                Cutpoint[] cutpoints = metaData.getCutpoints();
                for (int i = 0; i < cutpoints.length; i++) {
                    if (i == 0) { retVal.add(cutpoints[i].getRange()[0]); }
                    retVal.add(cutpoints[i].getRange()[1]);
                }
                return retVal;
            }
            metaData = parent.get(metaData);
        }
        */
        
        // get the right assessment
        GenericEntity assmt = getAssmtByName(name);
        
        // loop through the assessment perf level array, finding cutpoints
        List<Integer> cutPoints = new ArrayList<Integer>();
        List<Map> assmtPerfLevels = assmt.getList(Constants.ATTR_ASSESSMENT_PERF_LEVEL);
        int i = 0;
        for (Map assmtPerfLevel : assmtPerfLevels) {
           
            if (assmtPerfLevel.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(Constants.ATTR_SCALE_SCORE)) {
               
               // if it's the first level, add the min of the range
               if (i == 0) {
                  Double min = (Double) (assmtPerfLevel.get(Constants.ATTR_MINIMUM_SCORE));
                  cutPoints.add(new Integer(min.intValue()));
               }
               
               // add the max of the range
               Double max = (Double) (assmtPerfLevel.get(Constants.ATTR_MAXIMUM_SCORE));
                cutPoints.add(new Integer(max.intValue()));
                i++;
            }
        }
        return cutPoints;
    }

    
    /*
     * Returns a list representing the possible performance levels of the assessment family
     *
     * Returns null if no perf levels are found for the family.
     */
    public List<PerfLevel> findPerfLevelsForFamily(String name) {
        /*
        AssessmentMetaData metaData = allMetaData.get(name);
        if (metaData == null) return null;
        // find the possible periods for the assessment family
        while (metaData != null) {
            if (metaData.getPerfLevels() != null) {
                return Arrays.asList(metaData.getPerfLevels());
            }
            metaData = parent.get(metaData);
        }
        */
        return null;
    }

    public Integer findNumRealPerfLevelsForFamily(String name) {
        /*
        List<PerfLevel> perfLevels = findPerfLevelsForFamily(name);
        if (perfLevels == null) { return null; }
        int retVal = 0;
        for (PerfLevel pl : perfLevels) {
            if (pl.getIsReal()) { retVal++; }
        }
        return retVal;
        */
        
        GenericEntity assmt = getAssmtByName(name);
        int numPerfLevels = 0;
        
        // loop through the assessment performance level array. count up the Mastery level records
        List<Map> assmtPerfLevels = assmt.getList(Constants.ATTR_ASSESSMENT_PERF_LEVEL);
        for (Map assmtPerfLevel : assmtPerfLevels) {
            if (assmtPerfLevel.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(Constants.ATTR_SCALE_SCORE)) {
                numPerfLevels++;
            }
        }
        
        return (new Integer(numPerfLevels));
    }
    
    /**
     * Get an assessment object by name
     * 
     * @param name
     * @return
     */
    public GenericEntity getAssmtByName(String name) {
        
        // loop through assessment objects, comparing names
        // this will have to be fixed when the assessment family hierarchy is implemented for real
        for (GenericEntity assmt : assmts) {
            if (assmt.getString(Constants.ATTR_ASSESSMENT_TITLE).contains(name)) {
                return assmt;
            }
        }
        return null;
    }
    
    /**
     * Returns true iff assessment family 1 is ancestor of assessment family 2
     */
    public boolean isAncestor(String assFamilyName1, String assFamilyName2) {
        /*
        AssessmentMetaData metaData1 = allMetaData.get(assFamilyName1);
        AssessmentMetaData metaData2 = allMetaData.get(assFamilyName2);
        if (metaData1 == null) return false;
        if (metaData2 == null) return false;
        AssessmentMetaData metaData = metaData2;
        while (metaData != null) {
            if (metaData == metaData1) { return true; }
            metaData = parent.get(metaData);
        }
        */
        return false;
    }

}
