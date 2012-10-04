package org.slc.sli.search;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
    public static final void main(String[] args) throws IOException {
        new ClassPathXmlApplicationContext("application-context.xml");
    }
}
