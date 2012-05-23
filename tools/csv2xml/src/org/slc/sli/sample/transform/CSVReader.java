package org.slc.sli.sample.transform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads CSV file and convert data into record.
 * Each record consists of a set of column-name/value pairs.
 *
 */
public class CSVReader {
    public static String DATE_FORMAT = "yyyy-mm-dd";

    protected String[] columnNames;
    protected BufferedReader csvFile;
    protected Map<String, String> record;

    public static Calendar getDate(String dateString) {
        DateFormat dateFormatter = new SimpleDateFormat(CSVReader.DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();

        try {
            Date date = (Date) dateFormatter.parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public CSVReader(String filename) throws IOException {
        csvFile = new BufferedReader(new FileReader(filename));
        String dataRow = csvFile.readLine();
        columnNames = dataRow.split(",");
        this.readRecord();
    }

    /**
     * read one line of CSV file and convert it into a record
     */
    protected void readRecord() {
        try {
            String dataRow = csvFile.readLine();
            if (dataRow == null) {
                record = null;
                return;
            }

            String[] values = dataRow.split(",");

            record = new HashMap<String, String>();

            for (int i = 0; i < columnNames.length; i++) {
                if (i < values.length) {
                    record.put(columnNames[i], values[i]);
                } else {
                    record.put(columnNames[i], null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get current record
     * @return one record (a set of column-name/value pairs)
     */
    public Map<String, String> getCurrentRecord() {
        return record;
    }

    /**
     * get next record
     * @return one record (a set of column-name/value pairs), or null if no more record in the file
     */
    public Map<String, String> getNextRecord() {
        this.readRecord();
        return record;
    }

}
