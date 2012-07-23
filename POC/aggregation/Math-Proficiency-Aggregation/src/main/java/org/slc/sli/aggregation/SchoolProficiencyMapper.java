package org.slc.sli.aggregation;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

/**
 * Map ACT scores and aggregate them at the school level based on scale score.
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

    public SchoolProficiencyMapper() throws UnknownHostException, MongoException {
        mongo = new Mongo("localhost");
        db = mongo.getDB("sli");
        ssa = db.getCollection("studentSchoolAssociation");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void map(final String studentId,
                    final BSONObject student,
                    final Context context)
            throws IOException, InterruptedException {

        String idCode = context.getConfiguration().get("AssessmentIdCode");
        String edOrgId = getStudentEdOrgId(studentId.toString());

        if (edOrgId == null) {
            return;
        }

        double scaleScore = 0.0;
        boolean found = false;

        // "aggregations" : { "assessments" : { "Grade 7 2011 State Math" : { "HighestEver" : { "ScaleScore" : "31.0" }

        if (student.get("aggregations") != null) {
            Map<String, Object> aggregations = (Map<String, Object>) student.get("aggregations");
            if (aggregations.containsKey("assessments")) {
                Map<String, Object> assessments = (Map<String, Object>) aggregations.get("assessments");
                if (assessments.containsKey(idCode)) {
                    Map<String, Object> assessment = (Map<String, Object>) assessments.get(idCode);
                    if (assessment.containsKey("HighestEver")) {
                        Map<String, Object> highest = (Map<String, Object>) assessment.get("HighestEver");
                        if (highest.containsKey("ScaleScore")) {
                            found = true;
                            scaleScore = Double.parseDouble((String) highest.get("ScaleScore"));
                        }
                    }
                }
            }
        }

        code.set("!");
        if (found) {
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

        edOrg.set(edOrgId);
        context.write(edOrg, code);
    }

    @SuppressWarnings("unchecked")
    protected String getStudentEdOrgId(String studentId) throws UnknownHostException {
        DBObject ssaRecord = ssa.findOne(new BasicDBObject("body.studentId", studentId));
        if (ssaRecord != null) {
            String schoolId = (String) ((Map<String, Object>) ssaRecord.get("body")).get("schoolId");
            return schoolId;
        }
        return null;
    }
}
