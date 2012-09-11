package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility for accessing subdocuments that have been collapsed into a super-doc
 *
 * @author nbrown
 *
 */
public class LocationMapper implements Mappable {
    private static final String ID_SEPERATOR = "_";
    private String type;
    private String collection;
    private String key;
    private String subCollection;

    private MongoTemplate template;

    public LocationMapper(MongoTemplate template, String type, String collection, String key, String subCollection) {
        this.type = type;
        this.collection = collection;
        this.key = key;
        this.subCollection = subCollection;
        this.template = template;
    }

    private String getKey() {
        return key;
    }

    private Query getQuery(String parentId) {
        return Query.query(Criteria.where("_id").is(parentId));
    }

    private String getParentEntityId(Map<String, Object> entity) {
        return (String) entity.get(getKey());
    }

    public String getParentEntityId(String entityId) {
        return entityId.split(ID_SEPERATOR)[0];
    }

    private Update getUpdateObject(Map<String, Map<String, Object>> newEntities) {
        Update update = new Update();
        for (Map.Entry<String, Map<String, Object>> entity : newEntities.entrySet()) {
            update.set(getField(entity.getKey()), entity.getValue());
        }
        return update;
    }

    public boolean update(String id, Entity entity) {
        Query query = getQuery(getParentEntityId(entity.getBody()));
        Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
        updateMap.put(id, entity.getBody());
        Update updateObject = getUpdateObject(updateMap);
        return template.updateFirst(query, updateObject, collection).getLastError().ok();
    }

    public boolean bulkUpdate(String parentId, Map<String, Map<String, Object>> newEntities) {
        Query query = getQuery(parentId);
        Update update = getUpdateObject(newEntities);
        return template.updateFirst(query, update, collection).getLastError().ok();
    }

    private boolean create(String id, Entity entity) {
        return update(id, entity);
    }

    public boolean bulkCreate(String parentId, List<Map<String, Object>> entities) {
        Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> entity : entities) {
            updateMap.put(makeEntityId(entity), entity);
        }
        return bulkUpdate(parentId, updateMap);
    }

    public boolean insert(List<Entity> entities) {
        ConcurrentMap<String, List<Map<String, Object>>> parentEntityMap = new ConcurrentHashMap<String, List<Map<String, Object>>>();
        for (Entity entity : entities) {
            String parentEntityId = getParentEntityId(entity.getBody());
            parentEntityMap.putIfAbsent(parentEntityId, new ArrayList<Map<String, Object>>());
            parentEntityMap.get(parentEntityId).add(entity.getBody());
        }
        boolean result = true;
        for (Map.Entry<String, List<Map<String, Object>>> entry : parentEntityMap.entrySet()) {
            result &= bulkCreate(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String makeEntityId(Map<String, Object> entity) {
        // TODO this needs to be done a bit smarter, probably using whatever is in place for
        // deterministic ids
        return getParentEntityId(entity) + ID_SEPERATOR + RandomStringUtils.randomNumeric(16);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> read(String id) {
        Map<?, ?> result = template.findOne(Query.query(Criteria.where("_id").is(getParentEntityId(id))),
                Map.class, collection);
        return (Map<String, Object>) ((Map<String, Object>) result.get(subCollection)).get(id);
    }

    private String getField(String id) {
        return subCollection + "." + id;
    }


    @Override
    public Iterable<Entity> readAll(List<String> ids, Iterable<Entity> entities) {
        List<String> parentIds = new ArrayList<String>();
        List<Entity> returnEntities = new ArrayList<Entity>();

        for (String id : ids) {
            parentIds.add(getParentEntityId(id));
        }

        Iterable<Map> results = template.find(Query.query(Criteria.where("_id").in(parentIds)), Map.class, collection);

        for (Map entity : results) {
            for (String id : ids) {
                if (((Map<String, Object>) entity.get(subCollection)).containsKey(id)) {
                    returnEntities.add(new MongoEntity(type, id, (Map<String, Object>) ((Map<String, Object>) entity.get(subCollection)).get(id), null));
                }
            }
        }

        return returnEntities;
    }

    @Override
    public Entity read(Entity entity) {
        return null;
    }

    @Override
    public Entity write(Entity entity) {
        String id = makeEntityId(entity.getBody());
        create(id, entity);

        return new MongoEntity(type, id, entity.getBody(), entity.getMetaData(), entity.getCalculatedValues(), entity.getAggregates());
    }

    @Override
    public Iterable<Entity> acceptRead(String type, NeutralQuery neutralQuery, SchemaVisitor visitor) {
        return visitor.visitRead(type, neutralQuery, this);
    }

    @Override
    public Entity acceptWrite(String type, Entity entity, SchemaVisitor visitor) {
        return visitor.visitWrite(type, entity, this);
    }
}
