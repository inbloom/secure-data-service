package org.slc.sli.aggregation;


import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

public class SchoolProficiencyMapper
        extends Mapper<Date, BSONObject, IntWritable, DoubleWritable> {

    @Override
    public void map( final Date pKey,
                     final BSONObject pValue,
                     final Context pContext )
            throws IOException, InterruptedException{

        final int year = pKey.getYear() + 1900;
        double bid10Year = ( (Number) pValue.get( "bc10Year" ) ).doubleValue();

        pContext.write( new IntWritable( year ), new DoubleWritable( bid10Year ) );
    }
}
