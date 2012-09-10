package org.slc.sli.dal.convert;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Utility for accessing subdocuments that have been collapsed into a super-doc
 *
 * @author nbrown
 *
 */
public class SubDocAccessor {
    private final Map<String, Location> locations = new HashMap<String, SubDocAccessor.Location>();

    private final MongoTemplate template;

    public SubDocAccessor(MongoTemplate template) {
        this.template = template;
        locations.put("studentAssessmentAssociation", new Location("student", "studentId", "assessments"));
        locations.put("studentSectionAssociation", new Location("student", "studentId", "sections"));
    }

    /**
     * THe location of the subDoc
     *
     * @author nbrown
     *
     */
    public class Location {
        private static final String ID_SEPERATOR = ";";
        private final String collection;
        private final String key;
        private final String subCollection;

        public Location(String collection, String key, String subCollection) {
            super();
            this.collection = collection;
            this.key = key;
            this.subCollection = subCollection;
        }

        private String getKey() {
            return key;
        }

        private Query getQuery(Map<String, Object> entity) {
            return Query.query(Criteria.where("_id").is(getParentEntityId(entity)));
        }

        private String getParentEntityId(Map<String, Object> entity) {
            return (String) entity.get(getKey());
        }

        private String getParentEntityId(String entityId) {
            return entityId.split(ID_SEPERATOR)[0];
        }

        private Update getUpdateObject(String id, Map<String, Object> entity) {
            return new Update().set(getField(id), entity);
        }

        public boolean update(String id, Map<String, Object> entity) {
            Query query = getQuery(entity);
            Update updateObject = getUpdateObject(id, entity);
            return template.updateFirst(query, updateObject, collection).getLastError().ok();
        }

        public boolean create(Map<String, Object> entity) {
            return update(makeEntityId(entity), entity);
        }

        private String makeEntityId(Map<String, Object> entity) {
            //TODO this needs to be done a bit smarter, probably using whatever is in place for deterministic ids
            return getParentEntityId(entity) + ID_SEPERATOR + RandomStringUtils.randomNumeric(16);
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> read(String id) {
            return (Map<String, Object>) ((Map<String, Object>) template.findOne(
                    Query.query(Criteria.where("_id").is(getParentEntityId(id))), Map.class, collection).get(
                    subCollection)).get(id);
        }

        private String getField(String id) {
            return subCollection + "." + id;
        }

    }

    public boolean isSubDoc(String docType) {
        return locations.containsKey(docType);
    }

    public Location subDoc(String docType) {
        return locations.get(docType);
    }
}
