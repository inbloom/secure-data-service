/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.util.transform;

/**
 * 
 * @author dwilliams
 *
 */
public class Session implements MongoDataEmitter {
    private String schoolYear = null;
    private String semester = null;
    private String beginDate = null;
    private String endDate = null;
    private int days = 0;
    
    private String generatedUuid = null;
    
    public Session(String schoolYear, String semester, String beginDate, String endDate, int days) {
        this.schoolYear = schoolYear;
        this.semester = semester;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.days = days;
        
        generatedUuid = Base64.nextUuid("aacb");
    }
    
    @Override
    public String emit() {
        // {"_id":{"$binary":"zk4WoMB66rtWHSX8sUcbuw==","$type":"03"},"type":"session","body":{"schoolYear":"Year_2010_2011","sessionName":"Spring 2011",
        // "term":"Spring_Semester","endDate":"2011-06-31","beginDate":"2011-01-01","totalInstructionalDays":90},
        // "tenantId":"Zork"}
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"session\",\"body\":{\"schoolYear\":\"").append(schoolYear)
                .append("\",\"term\":\"").append(semester).append("\",\"endDate\":\"").append(endDate)
                .append("\",\"beginDate\":\"").append(beginDate).append("\",\"totalInstructionalDays\":").append(days)
                .append("},\"tenantId\":\"Zork\"}\n");
        
        return answer.toString();
    }
    
    public String getUuid() {
        return generatedUuid;
    }
}
