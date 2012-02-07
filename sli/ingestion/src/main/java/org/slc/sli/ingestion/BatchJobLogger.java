package org.slc.sli.ingestion;

import java.io.File;
import java.io.IOException;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.LandingZone;


/**
 *
 * @author jsa
 *
 */
public class BatchJobLogger {

    public static Logger createLoggerForJob(BatchJob job, LandingZone lz) throws IOException {

        File logFile = lz.getLogFile(job.getId());

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(lc);
        patternLayout.setPattern("%date %-5level %msg%n");
        patternLayout.start();

        FileAppender appender = new FileAppender();
        appender.setContext(lc);
        appender.setFile(logFile.getAbsolutePath()); // tricky if we're not localFS...
        appender.setLayout(patternLayout);
        appender.start();

        Logger logger = lc.getLogger(job.getId());
        logger.addAppender(appender);

        return logger;
    }

}
