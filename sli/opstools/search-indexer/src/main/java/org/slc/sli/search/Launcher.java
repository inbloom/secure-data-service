package org.slc.sli.search;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
    
    public static final void main(String[] args) throws IOException {
        final ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        Loader loader = context.getBean(Loader.class);
        loader.load();
    }
    
}
