package org.slc.sli.manager;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.client.MockAPIClient;
import org.slc.sli.config.ConfigUtil;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.Field;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * AssessmentManager supplies student assessment data to the controllers.
 * Given a configuration object, it will source the correct data, apply
 * the necessary business logic, and return the results. 
 * 
 * @author dwu
 *
 */
public class AssessmentManager {
    
    private static AssessmentManager instance = new AssessmentManager();
    private MockAPIClient apiClient;
    
    protected AssessmentManager() {        

        apiClient = new MockAPIClient();
    
    }
    
    public MockAPIClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(MockAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    public static AssessmentManager getInstance() {
        return instance;
    }

    public List<Assessment> getAssessments(String username, List<String> studentIds, ViewConfig config) {
        
        // extract the studentInfo data fields we need
        List<Field> dataFields = ConfigUtil.getDataFields(config, "assessment");

        
        // TODO: API question: do we make one call and get all assessments, then filter? or make calls for only what we need?
        //       For now, make one call and filter.
        List<Assessment> assmts = Arrays.asList(apiClient.getAssessments(username, studentIds));
        
        // get list of assmt names
        Set<String> assmtNames = getAssmtNames(dataFields);
        assmtNames = getAssmtNames(dataFields);
        
        // filter out unwanted assmts
        List<Assessment> filteredAssmts = new ArrayList<Assessment>();
        filteredAssmts.addAll(assmts);
        /* To do this right, we'll need all the assessments under the assmt family's name, and 
         * we'll require assessment metadata for it 
        for (Assessment assmt : assmts) {
            if (assmtNames.contains(assmt.getAssessmentName()))
                filteredAssmts.add(assmt);
        }
        */
        
        // return the results
        return filteredAssmts;
    }
    
    /*
     * Get names of assessments we need data for
     */
    private Set<String> getAssmtNames(List<Field> dataFields) {
    
        Set<String> assmtNames = new HashSet<String>();
        for (Field field : dataFields) {
            String fieldValue = field.getValue();
            assmtNames.add(fieldValue.substring(0, fieldValue.indexOf('.')));
        }
        return assmtNames;
    }
    
    public List<AssessmentMetaData> getAssessmentMetaData(String username) {
        return Arrays.asList(apiClient.getAssessmentMetaData(username));
    }
}
