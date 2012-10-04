package org.slc.sli.search;

import java.io.IOException;

import org.slc.sli.search.util.AppLock;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
    public static final void main(String[] args) throws IOException {
        new AppLock("data/indexer.lock");
        new ClassPathXmlApplicationContext("application-context.xml");
    }
}
