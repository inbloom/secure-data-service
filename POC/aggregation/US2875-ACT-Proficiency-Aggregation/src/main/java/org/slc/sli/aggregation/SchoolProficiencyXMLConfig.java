package org.slc.sli.aggregation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

import com.mongodb.hadoop.util.MongoTool;

public class SchoolProficiencyXMLConfig extends MongoTool {

    static{
        // Load the XML config defined in hadoop-local.xml
        Configuration.addDefaultResource( "src/main/resources/mongo-defaults.xml" );
        Configuration.addDefaultResource( "src/main/resources/mongo-us2875-ACT-Proficiency-Aggregation.xml" );
    }

    public static void main( final String[] pArgs ) throws Exception{
        System.exit( ToolRunner.run( new SchoolProficiencyXMLConfig(), pArgs ) );
    }
}
