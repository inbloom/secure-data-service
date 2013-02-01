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


package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public class GradingPeriodMeta {
    String beginData;
    String endDate;
    public String id;
    private int gradingPeriodNum;
 
    public int getGradingPeriodNum() {
        return gradingPeriodNum;
    }

    public void setGradingPeriodNum(int gradingPeriodNum) {
        this.gradingPeriodNum = gradingPeriodNum;
    }
    
    public List<CalendarMeta> calendars = new ArrayList<CalendarMeta>();
    
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
