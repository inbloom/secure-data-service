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

package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.JobSource;
import org.slc.sli.ingestion.reporting.SimpleReportStats;
import org.slc.sli.ingestion.reporting.Source;

/**
 *
 * Driver/Controller of the Offline Validation Tool
 *
 * @author tke
 *
 */
public class OfflineTool {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");

        OfflineTool main = context.getBean(OfflineTool.class);

        main.start(args);
    }

    private ValidationController controller;

    private AbstractMessageReport messageReport;

    // Name of the validation tool
    String appName;

    // Number of arguments
    int inputArgumentCount;

    private Source source = null;

    private AbstractReportStats reportStats = null;

    private void start(String[] args) {
        LoggerUtil.logToConsole();
        File file = null;

        source = new JobSource(null, null, null);
        reportStats = new SimpleReportStats(null, null, null);

        if ((args.length != inputArgumentCount)) {
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0011, appName);
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0012, appName);
            return;
        } else {
            file = new File(args[0]);
            if (!file.exists()) {
                messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0014, args[0]);
                return;
            }
            if (file.isDirectory()) {
                messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0013);
                messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0012, appName);
                return;
            }
        }
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        String logFilePath = file.getAbsoluteFile().getParent() + File.separator + file.getName() + "-"
                + time.getTime() + ".log";
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

    public void setMessageReport(AbstractMessageReport messageReport) {
        this.messageReport = messageReport;
    }
}
