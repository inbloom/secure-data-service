package org.slc.sli.stamper.mapreduce.reduce;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.slc.sli.stamper.mapreduce.io.MongoAggFormatter;
import org.slc.sli.stamper.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.stamper.util.BSONUtilities;

import com.mongodb.hadoop.io.BSONWritable;

public class StampEdorg extends Reducer<TenantAndIdEmittableKey, TenantAndIdEmittableKey, TenantAndIdEmittableKey, BSONWritable> {

	@Override
	protected void reduce(TenantAndIdEmittableKey schoolKey, Iterable<TenantAndIdEmittableKey> studentKeys, Context context)
			throws IOException, InterruptedException {
		
		for(TenantAndIdEmittableKey studentKey : studentKeys) {
	        String field = context.getConfiguration().get(MongoAggFormatter.UPDATE_FIELD);	        
	        BSONObject obj = BSONUtilities.setValue(field, schoolKey.getId().toString());
	        BSONWritable output = new BSONWritable(obj);
	        context.write(studentKey, output);
		}
	}	
}
