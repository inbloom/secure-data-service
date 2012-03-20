package org.slc.sli.api.perf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Logging aspect for API calls
 * @author srupasinghe
 *
 */
public aspect ResourceLoggingAspect {
    protected Logger apiLogger = LoggerFactory.getLogger("APICallLogger");
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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
}
