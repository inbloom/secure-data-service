package org.slc.sli.ingestion.tool;

import java.util.List;

import org.slc.sli.ingestion.validation.XsdValidator;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ToolMain{

    public static void main(String [] args){
    	ApplicationContext context =
                new ClassPathXmlApplicationContext("spring/applicationContext.xml");

    	ToolMain main = context.getBean(ToolMain.class);
    	main.start(args);

    }

    @Autowired
	private List<SimpleValidatorSpring> validators;
	private void start(String[] args){
		XsdValidator xsd = (XsdValidator) validators.get(0);
	}

	public void setValidators(List<SimpleValidatorSpring> validators){
		this.validators = validators;
	}

	public List<SimpleValidatorSpring> getValidators(){
		return validators;
	}

}


