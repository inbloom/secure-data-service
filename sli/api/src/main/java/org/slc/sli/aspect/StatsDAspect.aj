package org.slc.sli.aspect;

import org.slc.sli.api.statsd.ApiStatsdClient;


public privileged aspect StatsDAspect {
    private static ApiStatsdClient asc;
    static{
        try{
         asc =new ApiStatsdClient("10.71.1.80",8125); 
        }catch(Exception e){}
    }
    
    pointcut entityMethods(): within(org.slc.sli.api.resources.v1.entity.*) && execution(public * *(..));
    pointcut assocMethods(): within(org.slc.sli.api.resources.v1.association.*) && execution(public * *(..));
    pointcut dalMethods(): within(org.slc.sli.dal.*) && execution(public * *(..));
    pointcut securityMethods(): within(org.slc.sli.api.security.*) && execution(public * *(..));
    pointcut validationMethods(): within(org.slc.sli.api.validation.*) && execution(public * *(..));
    pointcut serviceMethods(): within(org.slc.sli.api.service.*) && execution(public * *(..));
    pointcut encryptMethods(): within(org.slc.sli.common.encrypt.*) && execution(public * *(..));
    pointcut domainMethods(): within(org.slc.sli.domain.*) && execution(public * *(..));
    
    pointcut resourceMethods() : 
        cflow((entityMethods() || assocMethods() || encryptMethods()) && (serviceMethods() && dalMethods() && domainMethods() && securityMethods()));
        
   Object around( ) : entityMethods(){
        //get the current time
        long begin = System.currentTimeMillis();
        
        //call the method 
        Object result = proceed();

        //get the current time
        long end = System.currentTimeMillis(); 
        
        //log the method information
       // Logger.getLogger("StatsDAspect").log(Level.SEVERE, String.format("StatsD Info: %s: %d - %d", thisJoinPoint, begin, end));
        //System.out.println(String.format("*****************StatsD Info: %s: %d", thisJoinPoint.getSignature().getDeclaringTypeName(), end-begin));
        String typeName = "";
        String[] packageName= thisJoinPoint.getSignature().getDeclaringTypeName().split("\\.");
        if(packageName.length>1){
        typeName=packageName[packageName.length-1];}
        else {typeName=thisJoinPoint.getSignature().getDeclaringTypeName();}
        if (typeName==null||typeName.equals("")){
            asc.timing("sli.api.allEntity", (int)(end-begin));   
        }else{
        asc.timing("sli.api."+typeName, (int)(end-begin));
        asc.timing("sli.api.allEntity", (int)(end-begin));}
        return result;        
    }
   
}