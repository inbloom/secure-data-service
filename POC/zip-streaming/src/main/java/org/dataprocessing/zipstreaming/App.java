package org.dataprocessing.zipstreaming;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author ifaybyshev
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Starting Zip Streaming POC");
        
        if (args.length < 2 || args.length > 2) {
            System.out.println("INVALID NUMBER OF INPUTS");
            System.out.println("1. NUMBER OF CONCURRENT PROCESSORS");
            System.out.println("2. ZIP FULL PATH");
            System.exit(0);
        }
        
        ConfigurableApplicationContext context = null;
        context = new ClassPathXmlApplicationContext("META-INF/spring/bootstrap.xml");
        context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
        
        int numberOfProcessors = new Integer(args[0]).intValue();
        String zipPath = args[1];
        
        System.out.println("NUMBER OF PROCESSORS = " + numberOfProcessors);
        System.out.println("PATH TO ZIP ARCHIVE = " + zipPath);
        
        long startTime = System.currentTimeMillis();
        
        ZipStreamingProcessor<Boolean> mongoProcessor = context.getBean(ZipStreamingProcessor.class);
        mongoProcessor.run(numberOfProcessors, zipPath);
        mongoProcessor.writeStatistics();
        
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        
        System.out.println();
        System.out.println("START TIME = " + startTime +
                "           END TIME = " + endTime +
                "           ELAPSED TIME MS = " + elapsed);

        System.out.println("-------------");
        
        System.exit(0);
    }
}
