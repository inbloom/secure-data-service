package org.slc.sli.stamper.mapreduce.reduce;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.slc.sli.stamper.mapreduce.io.MongoAggFormatter;
import org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.stamper.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.stamper.util.BSONUtilities;

import com.mongodb.hadoop.io.BSONWritable;

public class StampEdorg extends Reducer<IdFieldEmittableKey, BSONWritable, TenantAndIdEmittableKey, BSONWritable> {

	@Override
	protected void reduce(IdFieldEmittableKey id, Iterable<BSONWritable> results, Context context) throws IOException, InterruptedException {
		
		for(BSONWritable student : results) {
			String studentId = BSONUtilities.getValue(student, "body.studentId");
			String tenantId = BSONUtilities.getValue(student, "metaData.tenantId");
			
	        String field = context.getConfiguration().get(MongoAggFormatter.UPDATE_FIELD);
	        BSONObject obj = BSONUtilities.setValue(field, id.getId());
	        BSONWritable output = new BSONWritable(obj);
	        TenantAndIdEmittableKey fid = new TenantAndIdEmittableKey();
	        fid.setTenantId(new Text(tenantId));
	        fid.setId(new Text(studentId));
	        context.write(fid, output);
		}
	}
}
