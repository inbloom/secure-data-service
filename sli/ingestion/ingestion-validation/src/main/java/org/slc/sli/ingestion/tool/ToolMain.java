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
    private void start(String[] args){
        controller.doValidation()
    }

    public void setValidationController(ValidationController controller){
        this.controller = controller;
    }

    public ValidationController getValidation(){
        return controller;
    }

}


