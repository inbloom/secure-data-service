package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Transforms discipline incident relates entities to allow resolution of
 * disciplineIncident reference in discipline incidents
 *
 * This strategy will be replaced once automatic idref ref
 * resolution is fully implemented.
 *
 * @author jtully
 */
@Scope("prototype")
@Component("studentDisciplineIncidentAssociationTransformationStrategy")
public class StudentDisciplineIncidentAssociationTransformer extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentDisciplineIncidentAssociationTransformer.class);

    private static final String STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION = "studentDisciplineIncidentAssociation";

    private Map<Object, NeutralRecord> collection;

    public StudentDisciplineIncidentAssociationTransformer() {
        this.collection = new HashMap<Object, NeutralRecord>();
    }

    /**
     * The chaining of transformation steps. This implementation forces that all data will be
     * processed in "one-go"
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
    }

    private void loadData() {
        LOG.info("Loading data for student discipline incident association transformation.");
        collection = getCollectionFromDb(STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION);
        LOG.info("{} is loaded into local storage.  Total Count = {}", STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, collection.size());
        LOG.info("Finished loading data for student discipline incident association transformation.");
    }

    private void transform() {
        try {
            LOG.info("Transforming data: resolving disciplineIncident reference");
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collection.entrySet()) {
                NeutralRecord record = neutralRecordEntry.getValue();
                String idRef = (String) PropertyUtils.getProperty(record, "attributes.DisciplineIncidentReference.ref");
                NeutralRecord disciplineIncidentRef = findDisciplineIncidentNRFromIdRef(idRef);
                String incidentIdentifier = (String) PropertyUtils.getProperty(disciplineIncidentRef,
                        "attributes.IncidentIdentifier");
                PropertyUtils.setProperty(record, "attributes.refIncidentIdentifier", incidentIdentifier);
                
                record.setRecordType(record.getRecordType() + "_transformed");
                record.setCreationTime(getWorkNote().getRangeMinimum());
                insertRecord(record);
            }
        } catch (Exception e) {
            LogUtil.error(LOG, "Exception encountered resolving DisciplineIncidentAssociation reference:", e);
        }
    }

    private NeutralRecord findDisciplineIncidentNRFromIdRef(String idRef) {
        NeutralRecord result = null;
        Criteria idCriteria = Criteria.where("body.id").is(idRef);
        Query query = new Query(idCriteria);
        List<NeutralRecord> data = (List<NeutralRecord>) getNeutralRecordMongoAccess().getRecordRepository()
                .findByQueryForJob("disciplineIncident", query, getBatchJobId(), 0, 0);
        if (data.size() == 1) {
            result = data.get(0);
        }
        return result;
    }
}
