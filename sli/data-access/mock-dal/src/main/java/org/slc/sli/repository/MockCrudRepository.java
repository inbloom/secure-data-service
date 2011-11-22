package org.slc.sli.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Provides a mock in-memory repository that is backed by a linked hash map to retain insertion
 * order.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 * @param <T>
 *            The entity type
 * @param <ID>
 *            The type of the entity's ID.
 */
public class MockCrudRepository<T, ID extends Serializable> implements PagingAndSortingRepository<T, ID> {
    
    protected HashMap<Serializable, T> map = new LinkedHashMap<Serializable, T>();
    
    /**
     * The generator provides entity specific information about how to generate new IDs.
     */
    private MockIDProvider<T, ID> provider;
    
    /**
     * Construct a new repository with the given provider;
     * 
     * @param provider
     */
    public MockCrudRepository(MockIDProvider<T, ID> provider) {
        this.provider = provider;
    }
    
    @Override
    public T save(T entity) {
        Serializable id = provider.getIDForEntity(entity);
        map.put(id, entity);
        return entity;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Iterable<T> save(Iterable<? extends T> entities) {
        for (T entity : entities) {
            save(entity);
        }
        return (Iterable<T>) entities;
    }
    
    @Override
    public T findOne(ID id) {
        return map.get(id);
    }
    
    @Override
    public boolean exists(ID id) {
        return map.containsKey(id);
    }
    
    @Override
    public Iterable<T> findAll() {
        return map.values();
    }
    
    @Override
    public long count() {
        return map.size();
    }
    
    @Override
    public void delete(ID id) {
        map.remove(id);
    }
    
    @Override
    public void delete(T entity) {
        Serializable id = provider.getIDForEntity(entity);
        map.remove(id);
    }
    
    @Override
    public void delete(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }
    
    @Override
    public void deleteAll() {
        map.clear();
    }
    
    @Override
    public Iterable<T> findAll(Sort sort) {
        return findAll();
    }
    
    @Override
    public Page<T> findAll(Pageable pageable) {
        List<T> listValues = new ArrayList<T>();
        listValues.addAll(map.values());
        Page<T> page = new PageImpl<T>(listValues);
        
        return page;
    }
    
}
