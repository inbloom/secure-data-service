package org.slc.sli.ingestion.tool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class.
 *
 * @author nupur
 *
 */
public class ToolMain {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/validatorContext.xml");

        ToolMain main = context.getBean(ToolMain.class);

        main.start(args);
    }

    private Validation validation;

    private void start(String[] args) {
        // XsdValidator xsd = (XsdValidator) validators.get(0);
        validation.validate(args);
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Validation getValidation() {
        return validation;
    }

}
