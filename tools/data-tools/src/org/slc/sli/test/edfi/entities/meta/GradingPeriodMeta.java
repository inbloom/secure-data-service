package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public class GradingPeriodMeta {
    String beginData;
    String endDate;
    public String id;
    
    public List<String> calendars = new ArrayList<String>();
    
    public GradingPeriodMeta(String id) {
        this.id =  id; 
    }

    public GradingPeriodMeta() {
    	this("dummy");
    }
    
    public String getBeginData() {
        return beginData;
    }
    public void setBeginData(String beginData) {
        this.beginData = beginData;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    
}
