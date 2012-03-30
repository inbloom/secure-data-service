package org.slc.sli.ingestion.tool;

import java.io.IOException;

public class ToolMain{

    public static void main(String [] args){
        Validation validation = new Validation();
        try {
            validation.validateControlFile("/Users/tke/Documents/Projects/SLI/sli/ingestion/ingestion-validation/src/main/java/org/slc/sli/ingestion/tool/test/", "MainControlFile.ctl");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("fine");
    }

}


