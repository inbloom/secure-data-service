package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import org.slf4j.LoggerFactory;
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
    private static Logger logger = null;
    private static LoggerContext loggerContext;
    private static ThreadLocal<Logger> threadLocal = new ThreadLocal<Logger>();

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");

        OfflineTool main = context.getBean(OfflineTool.class);

        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.getLogger("OfflineTool.class");
        main.start(args);
    }

    private ValidationController controller;

    // Name of the validation tool
    String appName;

    // Number of arguments
    int inputArgumentCount;

    private void start(String[] args) {
        if ((args.length != inputArgumentCount)) {

            logger.error(appName + ":Illegal options");
            logger.error("Usage: " + appName + "[directory]");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            logger.error(args[0] + " doesn not exist");
            return;
        }

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("ToolLog");
        fileAppender.setFile(file.getParentFile() + "/" + file.getName() + "-" + time.getTime() + ".log");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%date %-5level %msg%n");
        encoder.start();

        fileAppender.setEncoder(encoder);
        fileAppender.start();
        logger.addAppender(fileAppender);
        getThreadLocal().set(logger);
        controller.doValidation(file);
    }

    public static ThreadLocal<Logger> getThreadLocal() {
        return threadLocal;
    }

    public static void setThreadLocal(ThreadLocal<Logger> threadLocal) {
        OfflineTool.threadLocal = threadLocal;
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