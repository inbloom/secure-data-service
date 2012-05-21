package org.slc.sli.sample.transform;

import java.io.IOException;
import java.util.HashMap;

public class CCSMathCSVReader extends CSVReader {
    
    public CCSMathCSVReader(String filename) throws IOException {
        super(filename);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void readRecord() {
        try {
            String dataRow = csvFile.readLine();
            if (dataRow == null) {
                record = null;
                return;
            }
            String description = "";
            if (dataRow.contains("\"")) {
                description = dataRow.substring(dataRow.indexOf("\"") + 1, dataRow.lastIndexOf("\"")).replace("\"", "");
                dataRow = dataRow.substring(0, dataRow.indexOf("\"") - 1);
            }
                String[] values = dataRow.split(",");
            
            record = new HashMap<String, String>();
            
            for (int i = 0; i < columnNames.length; i++) {
                if (i < values.length) {
                    record.put(columnNames[i], values[i]);
                } else if (i == values.length && !description.equals("")) {
                    record.put(columnNames[i], description);}
                    else {
                        record.put(columnNames[i], null);
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
