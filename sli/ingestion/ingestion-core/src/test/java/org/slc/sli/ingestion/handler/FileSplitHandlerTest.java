package org.slc.sli.ingestion.handler;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StopWatch;

/**
 * 
 * @author dkornishev
 * 
 */
@Ignore
public class FileSplitHandlerTest {
    
    private static final String LOC = "/Users/dkornishev/projects/workspace/SLI/sli/acceptance-tests/test/features/ingestion/test_data/DailyAttendance/StudentAttendanceEvents.xml";
    private static final String LOC_ATTENDANCE = "/var/files/in/InterchangeStudentAttendance.xml";
    private static final String LOC_PROGRAM = "/var/files/in/InterchangeStudentProgram.xml";
    
    @Test
    public void restFileSplitting() throws Exception {
        FileSplitHandler split = new FileSplitHandler();
        StopWatch sw = new StopWatch();
        
        sw.start();
        split.split(new File(LOC_ATTENDANCE));
        sw.stop();
        
        System.out.println("DONE: " + sw.getLastTaskTimeMillis());
    }
    
}
