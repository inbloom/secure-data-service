package org.slc.sli.sample.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            
            List<String> values = new ArrayList<String>();
            while (dataRow.length() > 0 && !dataRow.equals("")) {
                if (!dataRow.substring(0, 1).equals(",")) {
                    values.add(dataRow.substring(0, dataRow.indexOf(",")));
                    dataRow = dataRow.substring(dataRow.indexOf(","));
                } else if (dataRow.length() > 1 && !dataRow.substring(0, 2).equals(",\"")) {
                    dataRow = dataRow.substring(1);
                    if (dataRow.indexOf(",") > 0) {
                        values.add(dataRow.substring(0, dataRow.indexOf(",")));
                        dataRow = dataRow.substring(dataRow.indexOf(","));
                    } else {
                        values.add(dataRow.substring(0));
                        dataRow = "";
                    }
                    
                } else if (dataRow.length() > 1) {
                    dataRow = dataRow.substring(2);
                    if (dataRow.indexOf("\",") > 0) {
                        values.add(dataRow.substring(0, dataRow.indexOf("\",")));
                        dataRow = dataRow.substring(dataRow.indexOf("\",") + 1);
                    } else {
                        values.add(dataRow.substring(0, dataRow.length() - 1));
                        dataRow = "";
                    }
                }
            }
            
            record = new HashMap<String, String>();
            
            for (int i = 0; i < columnNames.length; i++) {
                if (i < values.size()) {
                    record.put(columnNames[i], values.get(i));
                } else {
                    record.put(columnNames[i], null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
