/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.idrefresolver;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author ifaybyshev
 *
 */
public class OfflineIdRefResolverTool  {
    
    private static Logger logger = LoggerUtil.getLogger();
    
    int inputArgumentCount;
    String appName;
    
    private IdRefResolverController controller;
    
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext-idrefresolver.xml");

        OfflineIdRefResolverTool main = context.getBean(OfflineIdRefResolverTool.class);

        main.start(args);
    }
    
    private void start(String[] args) {
        LoggerUtil.logToConsole();
        File file = null;

        if ((args.length != inputArgumentCount)) {
            logger.error(appName + ":Illegal options");
            logger.error("Usage: " + appName + " [Xml File]");
            return;
        } else {
            file = new File(args[0]);
            if (!file.exists()) {
                logger.error(args[0] + " does not exist");
                return;
            }
            if (file.isDirectory()) {
                logger.error("Illegal option - directory path. Expecting a Zip or a Ctl file");
                logger.error("Usage: " + appName + " [Xml File]");
                return;
            }
        }
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        String logFilePath = file.getAbsoluteFile().getParent() + File.separator + file.getName() + "-" + time.getTime() + ".log";
        LoggerUtil.logToFile(logFilePath);

        controller.doIdRefResolution(file);
    }
    
    public void setController(IdRefResolverController controller) {
        this.controller = controller;
    }

    public IdRefResolverController getControler() {
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
