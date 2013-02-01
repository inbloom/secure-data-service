/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.DirectorySource;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

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

    private ReportStats reportStats = null;

    private void start(String[] args) {
        LoggerUtil.logToConsole();
        File file = null;

        reportStats = new SimpleReportStats();

        if ((args.length != inputArgumentCount)) {
            Source source = new JobSource(StringUtils.join(args, ' '));
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0011, appName);
            messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0012, appName);
            return;
        } else {
            file = new File(args[0]);
            if (!file.exists()) {
                messageReport.error(reportStats, new FileSource(file.getName()), ValidationMessageCode.VALIDATION_0014, args[0]);
                return;
            }
            if (file.isDirectory()) {
                Source source = new DirectorySource(file.getPath(), file.getName());
                messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0013);
                messageReport.error(reportStats, source, ValidationMessageCode.VALIDATION_0016, appName);
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
