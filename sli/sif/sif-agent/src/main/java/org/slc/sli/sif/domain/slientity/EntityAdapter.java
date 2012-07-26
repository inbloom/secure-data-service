/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.sif.domain.slientity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;

/**
 * An GenericEntity in the SLI domain.
 * Each SLI Entity can be converted to a JSON Node ready for SLI operations.
 *
 * @author slee
 *
 */
public class EntityAdapter implements Entity
{
    protected static ObjectMapper mapper = new ObjectMapper();
    private GenericEntity adaptedEntity = null;
    private String entityType = null;
    private String id = null;
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    
    /**
     *  Constructor
     */
    public EntityAdapter(GenericEntity adaptedEntity, String entityType) {
        this.adaptedEntity = adaptedEntity;
        this.entityType = entityType;
        try
        {
            if (this.adaptedEntity!=null) {
                dataMap = mapper.readValue(adaptedEntity.json(), new TypeReference<Map<String, Object>>(){});
                clearNullValueKeys (dataMap);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Constructor
     */
    public EntityAdapter(GenericEntity adaptedEntity, String entityType, String id) {
        this(adaptedEntity, entityType);
        this.id = id;
    }

    /**
     * Get the data associated with this entity. If the entity has no data, returns
     * an empty map. The key into this map is the property name. The values of this
     * map can one of the following JSON types:
     * 
     * <ul>
     * <li>List</li>
     * <li>Map</li>
     * <li>null</li>
     * <li>Boolean</li>
     * <li>Character</li>
     * <li>Long</li>
     * <li>Double</li>
     * <li>String</li>
     * </ul>
     * 
     * @return Map of data.
     */
    @Override
    public Map<String, Object> getData()
    {
        return dataMap;
    }
    

    /**
     * Get the type name for this entity.
     * 
     * @return EntityType for this entity
     * 
     * @see org.slc.sli.api.client.constants.EntityNames for a list of available names.
     */
    @Override
    public String getEntityType()
    {
        return this.entityType;
    }

    /**
     * Get the ID for the entity. Each entity in the system has a unique identifier
     * assigned to it.
     * 
     * @return id String
     */
    @Override
    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setEntityType(String t)
    {
        this.entityType = t;
    }

    /**
     * Get a list of links for this entity. If the entity has no links, returns an empty list.
     * 
     * @return a List of links.
     */
    @Override
    public List<Link> getLinks()
    {
        return null;
    }

    /**
     * Takes an entity and fills in the missing values from it recursively
     * @param m
     */
    public void fillDataFromEntity(Entity e) {
        setId(e.getId());
        setEntityType(e.getEntityType());
        fillMap(dataMap, e.getData());
        dataMap.remove("links"); // Workaround: why is "links" included in an entity's data?!? 
    }
    
    // applies map2 to map1 recursively
    private void fillMap (Map map, Map u) {
        for (Object k : u.keySet()) {
            if (!map.containsKey(k)) {
                map.put(k, u.get(k));
            } else {
                Object o1 = map.get(k);
                Object o2 = u.get(k);
                // recursive update collections
                if (o1 instanceof Map && o2 instanceof Map) {
                    fillMap((Map) o1, (Map) o2);
                }
            }
        }
    }
    
    // removes all keys from this map that has a null value. If some values are maps, 
    // do it recursively
    private void clearNullValueKeys(Map m) {
        Set keySet = m.keySet();
        Set keysToRemove = new HashSet();
        for (Object k : keySet) {
            if (isNullValue(m.get(k))) {
                keysToRemove.add(k);
            }
        }
        for (Object k : keysToRemove) {
        	m.remove(k);
        }
    }
    private void clearNullValueFromList(List l) {
        ListIterator it = l.listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (isNullValue(o)) {
                it.remove();
            }
        }
    }
    private boolean isNullValue (Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Map) {
            clearNullValueKeys((Map) o);
            return ((Map)o).isEmpty();
        } else if (o instanceof List) {
            clearNullValueFromList((List) o);
            return ((List)o).isEmpty();
        }
        return false;
    }
}

