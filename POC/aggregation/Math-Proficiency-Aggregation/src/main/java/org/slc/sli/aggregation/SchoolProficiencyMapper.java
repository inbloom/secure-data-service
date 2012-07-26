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
        extends Mapper<String, BSONObject, Text, Text> {

    public static final String SCORE_TYPE = "ProficiencyCounts";

    Text code = new Text();
    Text edOrg = new Text();
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


    @Override
    public void map(final String schoolId,
                    final BSONObject school,
                    final Context context)
            throws IOException, InterruptedException {

        String idCode = context.getConfiguration().get("AssessmentIdCode");

        Set<DBObject> students = getStudentsForSchool(schoolId, idCode);

        if (students.isEmpty()) {
            return;
        }

        for (DBObject student : students) {
            String score = (String) student.get("aggregations.assessments." + idCode + ".HighestEver.ScaleScore");

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

            edOrg.set(schoolId);
            context.write(edOrg, code);
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<DBObject> getStudentsForSchool(String schoolId, String assessmentId) {
        Set<DBObject> students = new HashSet<DBObject>();

        BasicDBObject ids = new BasicDBObject();
        DBCursor c = ssa.find(new BasicDBObject("body.schoolId", schoolId));
        while (c.hasNext()) {
            DBObject ssaObject = c.next();
            Map<String, Object> body = (Map<String, Object>) ssaObject.get("body");
            String key = (String) body.get("studentId");
            ids.put("studentId", key);
        }

        DBObject fields = new BasicDBObject();
        fields.put("aggregations.assessments." + assessmentId + ".HighestEver.ScaleScore", 1);

        DBCursor studentCursor = studentColl.find(ids, fields);
        while (studentCursor.hasNext()) {
            students.add(studentCursor.next());
        }

        return students;
    }
}
