package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Describes a attribute level mapping
 *
 * @author srupasinghe
 *
 */
public class AttributeMapper implements Mappable {

    private String type;
    private String name;
    private String mapName;

    public AttributeMapper(String type, String name, String mapName) {
        this.type = type;
        this.name = name;
        this.mapName = mapName;
    }

    @Override
    public Iterable<Entity> readAll(List<String> ids, Iterable<Entity> entities) {
        List<Entity> returnEntities = new ArrayList<Entity>();

        for (Entity entity : entities) {
            returnEntities.add(read(entity));
        }

        return returnEntities;
    }

    @Override
    public Entity read(Entity entity) {
        Map<String, Object> body = entity.getBody();

        if (body.containsKey(name)) {
            Object value = body.get(name);
            body.remove(name);
            body.put(mapName, value);
        }

        return entity;
    }

    @Override
    public Entity write(Entity entity) {
        Map<String, Object> body = entity.getBody();

        if (body.containsKey(mapName)) {
            Object value = body.get(mapName);
            body.remove(mapName);
            body.put(name, value);
        }

        return entity;
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
