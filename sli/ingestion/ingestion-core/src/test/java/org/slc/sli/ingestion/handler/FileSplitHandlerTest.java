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
