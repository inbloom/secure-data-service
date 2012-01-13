package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.resolve.ClientRoleManager;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;

/**
 * Default converter for client roles to sli roles
 * Does absolutely nothing but give back exactly same list
 * 
 * @author dkornishev
 *
 */
@Component
public class DefaultClientRoleManager implements ClientRoleManager {

	@Autowired
	private EntityRepository repo;
	
    @Override
    /**
     */
    public List<String> resolveRoles(String realmId, List<String> clientRoleNames) 
    {
    	List<String> result = new ArrayList<String>();
    	for(String clientRoleName : clientRoleNames)
    	{
        	Query query = new Query(Criteria.where("body.mappings." + realmId).is(clientRoleName));
        	Iterable<Entity> roles = repo.findByFields("role", query, 0, 1);
        	for(Entity role : roles)
        	{
        		Object name = role.getBody().get("name");
        		if(name instanceof String)
        		{
        			result.add((String) name);
        		}
        	}
    	}
    	return result;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void addClientRole(String sliRoleName, String realmId,
			String clientRoleName) {
		Map<String, String> searchFields = new HashMap<String, String>();
		searchFields.put("name", sliRoleName); 
		Iterable<Entity> sliRoles = repo.findByFields("roles", searchFields);
		for(Entity entity : sliRoles)
		{
			Map<String, Object> entityBody = entity.getBody();
			Object mappings = entityBody.get("mappings");
			List<Map<String, List<String>>> listMappings = null;
			if(mappings != null && mappings instanceof List)
			{
				listMappings = (List<Map<String, List<String>>>) mappings;
			}
			else
			{
				listMappings = new ArrayList<Map<String, List<String>>>();
			}
			Map<String, List<String>> clientRoles = new HashMap<String, List<String>>();
			clientRoles.put(realmId, Arrays.asList(clientRoleName));
			listMappings.add(clientRoles);
			entityBody.put("mappings", listMappings);
			repo.update("roles", entity);
		}
		
	}

	@Override
	public void deleteClientRole(String sliRoleName, String realmId,
			String clientRoleName) {
		// TODO Auto-generated method stub
		
	}
	
	public void setRepository(EntityRepository r)
	{
		this.repo = r;
	}

	@Override
	public String getSliRoleName(String realmId, String clientRoleName) 
	{
    	Query query = new Query(Criteria.where("body.mappings." + realmId).is(clientRoleName));
    	Iterable<Entity> result = repo.findByFields("role", query, 0, 1);
    	Entity one = result.iterator().next();
		return (String) one.getBody().get("name");
	}

    
}
