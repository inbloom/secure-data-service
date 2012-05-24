package org.slc.sli.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.util.Constants;

/**
 * Simple application entity
 *
 * @author David Wu
 * @author Robert Bloh
 *
 */
public class GenericEntity extends LinkedHashMap<String, Object> implements Entity {

    private static final long serialVersionUID = -1398693068211322783L;

    public GenericEntity() {
        super();
    }

    public GenericEntity(Map<String, Object> map) {
        super(map);
    }

    public String getId() {
        return getString(Constants.ATTR_ID);
    }

    public String getString(String key) {
        return (String) (get(key));
    }

    @SuppressWarnings("unchecked")
    /**
     * Get object for dot notation based key
     * @param key
     * @return
     */
    public Object getNode(String key) {
        if (key == null) {
            return null;
        }
        String[] fieldArray = key.split("\\.");
        Object node = this;
        for (String field : fieldArray) {
            if (node == null || !(node instanceof Map)) {
                return null;
            }
            node = ((Map<String, Object>) node).get(field);
        }
        return node;
    }

    @SuppressWarnings("rawtypes")
    public List getList(String key) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) (get(key));
        return list == null ? Collections.emptyList() : list;
    }

    public void appendToList(String key, GenericEntity obj) {
        if (!containsKey(key)) {
            put(key, new ArrayList<GenericEntity>());
        }
        @SuppressWarnings("unchecked")
        List<GenericEntity> list = (List<GenericEntity>) get(key);
        list.add(obj);
        put(key, list);
    }

    // Entity Interface for SDK Integration

    public String getEntityType() {
        return null;
    }

    public Map<String, Object> getData() {
        return this;
    }

    public List<Link> getLinks() {
        if (getData().containsKey(Constants.ATTR_LINKS)) {
            return (List<Link>) getData().get(Constants.ATTR_LINKS);
        }
        return null;
    }

}