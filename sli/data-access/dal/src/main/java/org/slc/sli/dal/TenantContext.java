package org.slc.sli.dal;

public class TenantContext {
   
   private static ThreadLocal<String> tenantId = new ThreadLocal<String>() {
       protected synchronized String initialValue() {
           return null;
       }
   };
   
   public static String getTenantId() {
       return (String) tenantId.get();
   }
   
   public static void setTenantId(String value) {
       tenantId.set(value);
   }
   
}
