package org.slc.sli.ingestion.tool;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * Driver/Controller of the Offline Validation Tool
 * 
 * @author tke
 *
 */
public class OfflineTool {
    
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/validatorContext.xml");
        
        OfflineTool main = context.getBean(OfflineTool.class);
        main.start(args);
        
    }
    
    private ValidationController controller;
    
    // Name of the validation tool
    String appName;
    
    // Number of arguments
    int inputArgumentCount;
    
    private void start(String[] args) {
        if ((args.length != inputArgumentCount)) {
            System.out.println(appName + ":Illegal options");
            System.out.println("Usage: " + appName + "[directory]");
            return;
        }
        
        controller.doValidation(args[0]);
    }
    
    public void setController(ValidationController controller) {
        this.controller = controller;
    }
    
    public ValidationController getControler() {
        return controller;
    }
    
    public void setAppName(String name) {
        this.appName = name;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setInputArgumentCount(int n) {
        this.inputArgumentCount = n;
    }
    
    public int getInputArgumentCount() {
        return inputArgumentCount;
    }
}
