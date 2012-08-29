package org.slc.sli.stamper.mapreduce.map;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.stamper.mapreduce.io.JobConfiguration.BandConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.RangeConfig;
import org.slc.sli.stamper.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.stamper.util.BSONUtilities;

/**
 */
public class StampEdorg extends Mapper<TenantAndIdEmittableKey, BSONObject, TenantAndIdEmittableKey, TenantAndIdEmittableKey> {

    Mongo mongo = null;
    DB db = null;
    DBCollection ssa = null;
    DBCollection studentColl = null;
    ObjectMapper om = new ObjectMapper();
    RangeConfig validRange = null;
    ArrayList<BandConfig> bands = null;
    String[] fields = null;

    public StampEdorg() throws UnknownHostException, MongoException {
        mongo = new Mongo("localhost");
        db = mongo.getDB("sli");
        ssa = db.getCollection("studentSchoolAssociation");
        studentColl = db.getCollection("student");
    }

    @Override
    public void map(final TenantAndIdEmittableKey schoolKey, final BSONObject school, final Context context) throws IOException,
            InterruptedException {

        LinkedList<DBObject> students = getStudentsForSchool(schoolKey);
        if (students.isEmpty()) {
            return;
        }

        for (DBObject student : students) {
        	String studentId = BSONUtilities.getValue(student, "_id");

            TenantAndIdEmittableKey studentKey = new TenantAndIdEmittableKey();
            studentKey.setId(new Text(studentId));
            studentKey.setTenantId(schoolKey.getTenantId());

            context.write(schoolKey, studentKey);
        }
    }

    protected LinkedList<DBObject> getStudentsForSchool(TenantAndIdEmittableKey schoolKey) {

        BasicDBObject query = new BasicDBObject();
        query.put("body.schoolId", schoolKey.getId().toString());
        query.put("metaData.tenantId", schoolKey.getTenantId().toString());

        DBCursor c = ssa.find(query);
        String[] ids = new String[c.count()];
        int idx = 0;
        while (c.hasNext()) {
            DBObject ssaObject = c.next();
            ids[idx++] = BSONUtilities.getValue(ssaObject, "body.studentId");
        }

        query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", ids));
        query.put("metaData.tenantId", schoolKey.getTenantId().toString());

        LinkedList<DBObject> students = new LinkedList<DBObject>();
        DBCursor studentCursor = studentColl.find(query);
        while (studentCursor.hasNext()) {
            students.add(studentCursor.next());
        }

        return students;
    }
}
