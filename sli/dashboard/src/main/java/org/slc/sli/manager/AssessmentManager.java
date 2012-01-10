package org.slc.sli.manager;

import org.slc.sli.entity.Assessment;
import org.slc.sli.client.MockAPIClient;
import org.slc.sli.config.ConfigUtil;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.DataSet;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
        // call the api
        // TODO: mock/real api switch
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
        
        // extract the studentInfo data set configs
        List<DataSet> dataSets = ConfigUtil.getDataSets(config, "assessment");
        

        
        // TODO: API question: do we make one call and get all assessments, then filter? or make calls for only what we need?
        //       For now, make one call and filter.
        List<Assessment> assmts = Arrays.asList(apiClient.getAssessments(username, studentIds));
        
        // get list of assmt names
        List<String> assmtNames = new ArrayList<String>();
        for (DataSet dataSet : dataSets) {
            assmtNames.add(dataSet.getName());
        }
        
        // filter out unwanted assmts
        List<Assessment> filteredAssmts = new ArrayList<Assessment>();
        for (Assessment assmt : assmts) {
            if (assmtNames.contains(assmt.getAssessmentName()))
                filteredAssmts.add(assmt);
        }
        
        // TODO: apply time slot filter
        //List<Assessment> filteredAssmts = 
        
        // return the results
        return filteredAssmts;
    }
    
    
}
