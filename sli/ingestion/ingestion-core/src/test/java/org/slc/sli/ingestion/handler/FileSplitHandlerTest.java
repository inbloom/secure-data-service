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


package org.slc.sli.ingestion.handler;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 *
 * @author dkornishev
 *
 */
@Ignore
public class FileSplitHandlerTest {
    public static final Logger LOG = LoggerFactory.getLogger(FileSplitHandlerTest.class);

    private static final String LOC = "/Users/dkornishev/projects/workspace/SLI/sli/acceptance-tests/test/features/ingestion/test_data/DailyAttendance/StudentAttendanceEvents.xml";
    private static final String LOC_ATTENDANCE = "/var/files/in/InterchangeStudentAttendance.xml";
    private static final String LOC_PROGRAM = "/var/files/in/InterchangeStudentProgram.xml";

    private FileSplitHandler split = new FileSplitHandler();

    @Test
    public void restFileSplitting() throws Exception {
        StopWatch sw = new StopWatch();

        sw.start();
        split.split(new File(LOC_ATTENDANCE), "/var/files/out/");
        sw.stop();

        LOG.info("DONE Attendance: " + sw.getLastTaskTimeMillis());
    }

    @Test
    public void programFileSplitting() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        split.split(new File(LOC_PROGRAM), "/var/files/out/");
        sw.stop();

        LOG.info("DONE Program: " + sw.getLastTaskTimeMillis());

    }

}
