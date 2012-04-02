package org.slc.sli.ingestion.tool;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ToolMain{

    public static void main(String [] args) throws IOException{
        ApplicationContext context =
                new ClassPathXmlApplicationContext("spring/validatorContext.xml");

    	ToolMain main = context.getBean(ToolMain.class);
    main.start(args);

    }

    private ValidationController controller;
    //Name of the validation tool
    String appName;
    //Number of arguments
    int n_args;

    private void start(String [] args){

        if( (args.length != n_args) ){
            System.out.println(appName + ":Illegal options");
            System.out.println("Usage: " + appName + "[directory]");
            return ;
        }

        String landing_zone = args[0];

        controller.doValidation(landing_zone);
    }

    public void setController(ValidationController controller){
        this.controller = controller;
    }

    public ValidationController getControler(){
        return controller;
    }

    public void setappName(String name){
        this.appName = name;
    }

    public String getappName(){
        return appName;
    }

    public void setn_args(int n){
        this.n_args = n;
    }

    public int getn_args(){
        return n_args;
    }
}


