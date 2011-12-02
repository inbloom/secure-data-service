package org.slc.sli.dal.repository;

import java.util.Map;

import org.slc.sli.domain.Entity;

/**
 * Define the entity repository interface that provides basic CRUD and field
 * query methods for entities including core entities and association entities
 * 
 * @author  Dong Liu dliu@wgen.net
 * 
 */
public interface EntityRepository {

	/**
	 * @param entityType
	 *            the entity type need to be retrieved, can be entity type for
	 *            core entity or association entity
	 * @param id
	 *            the global unique id of the entity
	 * @return the entity retrieved
	 */
	Entity find(String entityType, String id);

	/**
	 * @param entityType
	 *            the entity type need to be retrieved, can be entity type for
	 *            core entity or association entity
	 * @param skip the beginning index of the entity that will be returned
	 * @param max the max number of entities that will be returned
	 * @return the collection of entities
	 */
	Iterable<Entity> finalAll(String entityType, int skip, int max);

	/**
	 * @param entity the entity that will be updated
	 */
	void update(Entity entity);

	/**
	 * @param entity the entity that will be persisted
	 * @return the entity that has been persisted
	 */
	Entity create(Entity entity);

	/**
	 * @param entity the entity that will be deleted
	 */
	void delete(Entity entity);
	
	/**
	 * @param entityType the entity type need to be deleted, can be entity type for
	 *            core entity or association entity
	 * @param id the global unique id of the entity
	 */
	void delete(String entityType, String id);

	/**
	 * @param entityType the entity type need to be retrieved, can be entity type for
	 *            core entity or association entity
	 * @param fields a map with key value pairs that define the search criteria 
	 * @param skip the beginning index of the entity that will be returned
	 * @param max the max number of entities that will be returned
	 * @return the collection of entities
	 */
	Iterable<Entity> findByFields(String entityType,
			Map<String, String> fields, int skip, int max);

}

