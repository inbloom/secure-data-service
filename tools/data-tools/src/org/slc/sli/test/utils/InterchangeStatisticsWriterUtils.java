package org.slc.sli.test.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Interchange writer
 *
 * @author bsuzuki
 *
 */
public class InterchangeStatisticsWriterUtils {

    public static final String REPORT_INDENTATION = "  ";
    private static final String STATISTICS_FILE = "statistics.json";
    
    private static PrintWriter statsWriter = null;
    
    public static void initStatisticsWriter(String path) {
        if (statsWriter != null) return;
        
        System.out.println();
        
        try {
            statsWriter = new PrintWriter(new BufferedWriter(new FileWriter(path + "/" + STATISTICS_FILE)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void writeInterchangeStatisticStart(String interchangeName) {
        if (statsWriter == null) {
            System.out.println("initStatisticsWriter must be before writeInterchangeStatisticStart()");
            System.exit(1);
        }
        
        StringBuffer statInfo = new StringBuffer();
        statInfo.append("{ \"" + interchangeName + "\" : " + "\n");
        statInfo.append(REPORT_INDENTATION + "{");
        statsWriter.println(statInfo);
        
        System.out.println("{ \"" + interchangeName + "\" : ");
        System.out.println(REPORT_INDENTATION + "{");
    }
    
    public static void writeInterchangeStatisticEnd(long count, long elapsedTime) {
        if (statsWriter == null) {
            System.out.println("initStatisticsWriter must be called before writeInterchangeStatisticEnd()");
            System.exit(1);
        }

        StringBuffer statInfo = new StringBuffer();
        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "\"Count\" : " + count + ",\n");
        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "\"ElapsedTime\" : " + elapsedTime +"\n");
        statInfo.append(REPORT_INDENTATION + "}\n");
        statInfo.append("}");
        statsWriter.println(statInfo);
        statsWriter.flush();
        
        System.out.println(statInfo);
    }

    public static void writeInterchangeEntityStatistic(String entityName, long entityCount, long elapsedTime) {
        if (statsWriter == null) {
            System.out.println("initStatisticsWriter must be called before writeInterchangeEntityStatistic()");
            System.exit(1);
        }

        StringBuffer statInfo = new StringBuffer();
        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "{ \"" + entityName + "\" : \n");
        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + REPORT_INDENTATION + 
                "{ \"Count\" : " + entityCount + ", \"ElapsedTime\" : " + elapsedTime + " } \n");
        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "}");
        statsWriter.println(statInfo);
        
        System.out.println(statInfo);
    }
    
}
