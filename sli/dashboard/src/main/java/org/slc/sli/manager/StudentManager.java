package org.slc.sli.manager;

import org.slc.sli.entity.Student;
import org.slc.sli.config.ConfigManager;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.DataSet;
import org.slc.sli.config.ConfigUtil;
import org.slc.sli.client.MockAPIClient;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * StudentManager supplies student data to the controllers.
 * Given a configuration object, it will source the correct data, apply
 * the necessary business logic, and return the results. 
 * 
 * @author dwu
 *
 */
public class StudentManager {
    
    private static StudentManager instance = null;
    
    protected StudentManager() {        
    }
    
    public static StudentManager getInstance() {
        if (instance == null) {
            instance = new StudentManager();
        }
        return instance;
    }
    
    public List<Student> getStudentInfo(String username, List<String> studentIds, ViewConfig config) {
        
        // extract the studentInfo data set configs
        List<DataSet> dataSets = ConfigUtil.getDataSets(config, "studentInfo");
        
        // call the api
        // TODO: mock/real api switch
        // TODO: do we need more logic to grab the correct data sets and fields? right now, not using info in DataSet.
        List<Student> studentInfo = new ArrayList<Student>();
        if (dataSets.size() > 0) {
            MockAPIClient apiClient = new MockAPIClient();
            studentInfo.addAll(Arrays.asList(apiClient.getStudents(username, studentIds)));
        }
        
        // return the results
        return studentInfo;
    }
    
    
}
