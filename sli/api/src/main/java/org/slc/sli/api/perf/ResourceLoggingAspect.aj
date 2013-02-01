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


package org.slc.sli.api.perf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
/**
 * Logging aspect for API calls
 * @author srupasinghe
 *
 */
public aspect ResourceLoggingAspect {
    protected Logger apiLogger = LoggerFactory.getLogger("APICallLogger");
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Autowired
    @Qualifier("performanceRepo")
    private Repository<Entity> perfRepo;

    //pointcut for all the entity public methods 
    pointcut logEntityMethods(): within(org.slc.sli.api.resources.v1.entity.*) && execution(public * *(..));
    //pointcut for all the association public methods 
    pointcut logAssocMethods(): within(org.slc.sli.api.resources.v1.association.*) && execution(public * *(..));

    /**
     * advices around all the public methods of DefaultCrudEndPoint
     * @return
     */
    Object around() : logEntityMethods() || logAssocMethods() {
        //get the current time
        long enter = System.currentTimeMillis();
        
        //call the method
        Object result = proceed();
        
        //log the method information
        logMessage(thisJoinPoint, enter, System.currentTimeMillis());

        //log the method information in database
        //logApiDataToDb(thisJoinPoint, enter, System.currentTimeMillis());
        
        return result;
    }
    
    /**
     * Logs the api call information to the logger
     * @param jp The JoinPoint
     * @param enter Time when the method started executing
     * @param exit Time when the method finished executing
     */
    protected void logMessage(JoinPoint jp, long enter, long exit) {
       
       apiLogger.info("APICall : method [{}], class [{}], enter [{}], exit [{}], diff [{}ms]", new Object[] { jp.getSignature().getName(), 
               jp.getSignature().getDeclaringType().getName(), formatter.format(new Date(enter)), formatter.format(new Date(exit)), (exit-enter)});
       
    }

    private void logApiDataToDb(JoinPoint jp, long enter, long exit){
        HashMap<String,Object> body = new HashMap<String, Object>();
        body.put("method",jp.getSignature().getName()) ;
        body.put("class",jp.getSignature().getDeclaringType().getName()) ;
        body.put("enter",formatter.format(new Date(enter)));
        body.put("exit",formatter.format(new Date(exit)));
        body.put("responseTime", (exit-enter));
        perfRepo.create("apiResponse",body,"apiResponse");

    }
}
