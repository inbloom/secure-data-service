package org.slc.sli.shtick;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = JacksonRestEntitySerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
@JsonDeserialize(using = JacksonRestEntityDeserializer.class)
public final class Entity {

    private final String type;

    private final Map<String, Object> data;

    /**
     * Construct a new generic entity.
     *
     * @param type
     *            Entity type for this entity.
     * @param data
     *            Map representing the entity's data.
     */
    public Entity(final String type, final Map<String, Object> data) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.type = type;
        // FIXME: We're only making a defensive copy to the first level.
        this.data = Collections.unmodifiableMap(new HashMap<String, Object>(data));
    }

    /**
     * Returns the data associated with this entity. If the entity has no data, returns
     * an empty map. The key into this map is the property name. The values of this
     * map can one of the following JSON types:
     *
     * <ul>
     * <li>List<Object></li>
     * <li>Map<string,Object></li>
     * <li>null</li>
     * <li>Boolean</li>
     * <li>Character</li>
     * <li>Long</li>
     * <li>Double</li>
     * <li>String</li>
     * </ul>
     */
    public Map<String, Object> getData() {
        return data;
    }

    public String getId() {
        if (data.containsKey(Constants.ENTITY_ID_KEY)) {
            return (String) data.get(Constants.ENTITY_ID_KEY);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Link> getLinks() {

        if (data.containsKey(Constants.LINKS_KEY)) {
            return (List<Link>) data.get(Constants.LINKS_KEY);
        }
        return Collections.emptyList();
    }

    /**
     * Returns the type name for this entity.
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type : ").append(type).append(", data : ").append(data);
        sb.append("}");
        return sb.toString();
    }
}
