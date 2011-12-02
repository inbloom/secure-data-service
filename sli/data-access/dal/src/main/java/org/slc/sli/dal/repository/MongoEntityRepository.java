package org.slc.sli.dal.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

@Repository
public class MongoEntityRepository implements EntityRepository {

	@Autowired
	private MongoTemplate template;

	@Override
	public Entity find(String entityType, String id) {
		return template.findById(new ObjectId(id), MongoEntity.class,
				entityType);
	}

	@Override
	public Iterable<Entity> finalAll(String entityType, int skip, int max) {

		LinkedList<Entity> entities = new LinkedList<Entity>();
		List<MongoEntity> results = template.find(
				new Query().skip(skip).limit(max), MongoEntity.class,
				entityType);
		entities.addAll(results);
		return entities;
	}

	@Override
	public void update(Entity entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public Entity create(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Entity entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String entityType, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<Entity> findByFields(String entityType,
			Map<String, String> fields, int skip, int max) {
		// TODO Auto-generated method stub
		return null;
	}

}
