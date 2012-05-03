package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
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

    private static Logger logger = LoggerUtil.getLogger();
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");

        OfflineTool main = context.getBean(OfflineTool.class);

        main.start(args);
    }

    private ValidationController controller;

    // Name of the validation tool
    String appName;

    // Number of arguments
    int inputArgumentCount;

    private void start(String[] args) {
        LoggerUtil.logToConsole();
        File file = null;

        if ((args.length != inputArgumentCount)) {
            logger.error(appName + ":Illegal options");
            logger.error("Usage: " + appName + " [Zip/Ctl File]");
            return;
        } else {
            file = new File(args[0]);
            if (!file.exists()) {
                logger.error(args[0] + " does not exist");
                return;
            }
            if (file.isDirectory()) {
                logger.error("Illegal option - directory path. Expecting a Zip or a Ctl file");
                logger.error("Usage: " + appName + " [Zip/Ctl File]");
                return;
            }
        }
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        String logFilePath = file.getAbsoluteFile().getParent() + File.separator + file.getName() + "-" + time.getTime() + ".log";
        LoggerUtil.logToFile(logFilePath);

        controller.doValidation(file);
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