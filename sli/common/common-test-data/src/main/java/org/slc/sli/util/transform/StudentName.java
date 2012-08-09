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
public class StudentName implements MongoDataEmitter {
    private String firstName = null;
    private String middleName = null;
    private String lastSurname = null;
    private String suffix = null;
    private String nameVerification = null;
    
    public StudentName(String first, String middle, String last) {
        firstName = first;
        middleName = middle;
        lastSurname = last;
    }
    
    @Override
    public String emit() {
        // "name":{"firstName":"Gil","middleName":"","lastSurname":"Prince"}
        StringBuffer answer = new StringBuffer();
        answer.append("\"name\":{\"firstName\":\"").append(firstName).append("\",\"middleName\":\"")
                .append(middleName == null ? "" : middleName).append("\",\"verification\":\"")
                .append(nameVerification == null ? "" : nameVerification).append("\",\"lastSurname\":\"")
                .append(lastSurname).append("\",\"generationCodeSuffix\":\"").append(suffix == null ? "" : suffix)
                .append("\"}");
        return answer.toString();
    }
    
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    public void setNameVerification(String nameVerification) {
        this.nameVerification = nameVerification;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastSurname() {
        return lastSurname;
    }
    
    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public String getNameVerification() {
        return nameVerification;
    }
}
