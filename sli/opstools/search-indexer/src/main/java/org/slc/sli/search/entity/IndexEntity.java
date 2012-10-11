package org.slc.sli.search.entity;

import java.util.Map;

/**
 * Indexable Unit
 *
 */
public class IndexEntity {
    public enum Action {
        UPDATE("update"),
        INDEX("index"),
        DELETE("delete");
        
        String type;
        Action(String type) {this.type = type;}
        public String getType() {
            return type;
        }
    }
    private Action action;
    private String index;
    private String type;
    private String id;
    private String parentId;
    private Map<String, Object> body;
    
    public IndexEntity(Action action, String index, String type, String id, String parentId, Map<String, Object> body) {
        this.action = action;
        this.index = index;
        this.type = type;
        this.id = id;
        this.body = body;
        this.parentId = parentId;
    }
    
    public IndexEntity(String index, String type, String id, String parentId, Map<String, Object> body) {
        this(Action.INDEX, index, type, id, parentId, body);
    }
    
    public IndexEntity(String index, String type, String id, Map<String, Object> body) {
        this(Action.INDEX, index, type, id, null, body);
    }
    
    public IndexEntity(String index, String type, String id) {
        this(Action.INDEX, index, type, id, null, null);
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getBody() {
        return body;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public String getActionValue() {
        return action.getType();
    }
    
    @Override
    public String toString() {
        return action.getType() + ": {index:" + index + ", type:" + type + ", id:" + id + ", body:" + body + ", parent: " + parentId + "}";
    }

    public boolean isUpdate() {
        return action == Action.UPDATE;
    }
    
    public IndexEntity cloneWithBody(Map<String, Object> body) {
        return new IndexEntity(index, type, id, body);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((index == null) ? 0 : index.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IndexEntity other = (IndexEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (index == null) {
            if (other.index != null)
                return false;
        } else if (!index.equals(other.index))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
