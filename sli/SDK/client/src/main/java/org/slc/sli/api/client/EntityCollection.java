package org.slc.sli.api.client;

import java.util.LinkedList;

import com.google.gson.JsonArray;

import org.slc.sli.api.client.impl.transform.GenericEntityFromJson;

/**
 * Collection of Entity instances.
 *
 * @author asaarela
 */
public class EntityCollection extends LinkedList<Entity> {

    // Serializable
    private static final long serialVersionUID = -4836656021586038348L;

    /**
     * @param array JSON array to convert into an entity collection.
     */
    public void fromJsonArray(final JsonArray array) {
        GenericEntityFromJson transform = new GenericEntityFromJson();

        for (int i = 0; i < array.size(); ++i) {
            this.add(transform.deserialize(array.get(i).getAsJsonObject(), null, null));
        }
    }

}
