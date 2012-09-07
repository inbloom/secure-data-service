package org.slc.sli.aggregation.jobs.school.assessment;

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
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ConfigSections;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.CutPointConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.MetadataConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.RangeConfig;
import org.slc.sli.aggregation.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * Map state math assessment scores and aggregate them at the school level based on scale score.
 *
 * Buckets are defined within the configuration file.  For example,
 *
 * Scale value 1-character code Description
 * 6 <= n <= 14  W Warning
 * 15 <= n <= 20 B
 * 21 <= n <= 27 S
 * 28 <= n <= 33 E
 * ! Invalid Score or Data Error
 * - Score not available (N/A)
 *
 * @author asaarela
 */
public class SchoolProficiencyMapper extends Mapper<IdFieldEmittableKey, BSONObject, TenantAndIdEmittableKey, Text> {

    Mongo mongo = null;
    DB db = null;
    DBCollection ssa = null;
    DBCollection studentColl = null;
    ObjectMapper om = new ObjectMapper();
    RangeConfig validRange = null;
    ArrayList<CutPointConfig> bands = null;
    String[] fields = null;

    public SchoolProficiencyMapper() throws UnknownHostException, MongoException {
        mongo = new Mongo("localhost");
        db = mongo.getDB("sli");
        ssa = db.getCollection("studentSchoolAssociation");
        studentColl = db.getCollection("student");
    }

    @Override
    public void map(final IdFieldEmittableKey schoolId, final BSONObject school, final Context context) throws IOException,
            InterruptedException {

        Text code = new Text();

        LinkedList<DBObject> students = getStudentsForSchool(school);
        if (students.isEmpty()) {
            return;
        }

        for (DBObject student : students) {
            String score = BSONUtilities.getValue(student, fields[2]);

            if (score != null) {
                Double scaleScore = Double.valueOf(score);

                boolean valid = false;
                for (CutPointConfig band : bands) {
                    RangeConfig range = band.getRange();

                    if (range != null && scaleScore >= range.getMin() && scaleScore <= range.getMax()) {
                        code.set(band.getEmit());
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    // first entry is the 'invalid' band.
                    code.set(bands.get(0).getEmit());
                }
            } else {
                // second entry is the 'no score' band.
                code.set(bands.get(1).getEmit());
            }

            String tenantId = BSONUtilities.getValue(school, "metaData.tenantId");

            TenantAndIdEmittableKey outKey = new TenantAndIdEmittableKey();
            outKey.setId(schoolId.getId());
            outKey.setTenantId(new Text(tenantId));

            context.write(outKey, code);
        }
    }

    protected LinkedList<DBObject> getStudentsForSchool(BSONObject school) {
        String schoolId = BSONUtilities.getValue(school, "_id");
        String tenantId = BSONUtilities.getValue(school, "metaData.tenantId");

        BasicDBObject query = new BasicDBObject();
        query.put("body.schoolId", schoolId);
        query.put("metaData.tenantId", tenantId);

        DBCursor c = ssa.find(query);
        String[] ids = new String[c.count()];
        int idx = 0;
        while (c.hasNext()) {
            DBObject ssaObject = c.next();
            ids[idx++] = BSONUtilities.getValue(ssaObject, "body.studentId");
        }

        query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", ids));
        query.put("metaData.tenantId", tenantId);

        LinkedList<DBObject> students = new LinkedList<DBObject>();
        DBCursor studentCursor = studentColl.find(query);
        while (studentCursor.hasNext()) {
            students.add(studentCursor.next());
        }

        return students;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void setup(Mapper.Context context) throws IOException, InterruptedException {
        super.setup(context);

        ConfigSections cfg = JobConfiguration.fromHadoopConfiguration(context.getConfiguration());
        MetadataConfig meta = cfg.getMetadata();
        bands = meta.getCutPoints();

        BSONObject obj = MongoConfigUtil.getFields(context.getConfiguration());
        if (obj != null) {
            fields = obj.keySet().toArray(new String[0]);
        } else {
            throw new IllegalArgumentException("Invalid configuration found. Aggregates must "
                + "specify a the hadoop.map.fields property.");
        }
    }
}
