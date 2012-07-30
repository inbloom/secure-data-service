package org.slc.sli.aggregation;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import org.slc.sli.aggregation.mapreduce.TenantAndID;

/**
 * Map state math assessment scores and aggregate them at the school level based on scale score.
 *
 * The following buckets are defined:
 *
 *  Scale value      1-character code    Description
 *  6 <= n <= 14            W            Warning
 * 15 <= n <= 20            B
 * 21 <= n <= 27            S
 * 28 <= n <= 33            E
 *                          !            Invalid Score or Data Error
 *                          -            Score not available (N/A)
 *
 * @author asaarela
 */
public class SchoolProficiencyMapper
        extends Mapper<String, BSONObject, TenantAndID, Text> {

    public static final String SCORE_TYPE = "ProficiencyCounts";

    Text code = new Text();
    Mongo mongo = null;
    DB db = null;
    DBCollection ssa = null;
    DBCollection studentColl = null;

    public SchoolProficiencyMapper() throws UnknownHostException, MongoException {
        mongo = new Mongo("localhost");
        db = mongo.getDB("sli");
        ssa = db.getCollection("studentSchoolAssociation");
        studentColl = db.getCollection("student");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void map(final String schoolId,
                    final BSONObject school,
                    final Context context)
            throws IOException, InterruptedException {

        String idCode = context.getConfiguration().get("AssessmentIdCode");

        Map<String, Object> metaData = (Map<String, Object>) school.get("metaData");
        String tenantId = (String) metaData.get("tenantId");

        Set<DBObject> students = getStudentsForSchool(school, idCode);

        if (students.isEmpty()) {
            return;
        }

        for (DBObject student : students) {
            Map<String, Object> aggregations = (Map<String, Object>) student.get("aggregations");
            if (aggregations == null) {
                continue;
            }
            Map<String, Object> assessments = (Map<String, Object>) aggregations.get("assessments");
            if (assessments == null) {
                continue;
            }
            Map<String, Object> assessment = (Map<String, Object>) assessments.get(idCode);
            if (assessment == null) {
                continue;
            }
            Map<String, Object> highest = (Map<String, Object>) assessment.get("HighestEver");
            if (highest == null) {
                continue;
            }

            String score = (String) highest.get("ScaleScore");
            code.set("!");
            if (score != null) {
                Double scaleScore = Double.valueOf(score);

            // TODO -- these ranges should come from the assessment directly.
            if (scaleScore >= 6 && scaleScore <= 14) {
                code.set("W");
            } else if (scaleScore >= 15 && scaleScore <= 20) {
                code.set("B");
            } else if (scaleScore >= 21 && scaleScore < 27) {
                code.set("S");
            } else if (scaleScore >= 28 && scaleScore <= 33) {
                code.set("E");
            }
            } else {
                code.set("-");
            }
        }
        context.write(new TenantAndID(schoolId, tenantId), code);
    }

    @SuppressWarnings("unchecked")
    protected Set<DBObject> getStudentsForSchool(BSONObject school, String assessmentId) {
        Set<DBObject> students = new HashSet<DBObject>();

        BasicDBObject query = new BasicDBObject();

        String schoolId = (String) school.get("_id");

        Map<String, Object> metaData = (Map<String, Object>) school.get("metaData");
        String tenantId = (String) metaData.get("tenantId");

        query.put("body.schoolId", schoolId);
        query.put("metaData.tenantId", tenantId);

        DBCursor c = ssa.find(query);
        String[] ids = new String[c.count()];
        int idx = 0;
        while (c.hasNext()) {
            DBObject ssaObject = c.next();
            Map<String, Object> body = (Map<String, Object>) ssaObject.get("body");
            String key = (String) body.get("studentId");
            ids[idx++] = key;
        }

        DBObject fields = new BasicDBObject();
        fields.put("aggregations.assessments." + assessmentId + ".HighestEver.ScaleScore", 1);

        query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", ids));
        query.put("metaData.tenantId", tenantId);

        DBCursor studentCursor = studentColl.find(query, fields);
        while (studentCursor.hasNext()) {
            students.add(studentCursor.next());
        }

        return students;
    }
}
