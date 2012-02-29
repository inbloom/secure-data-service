package org.slc.sli.api.resources.v1;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;

/**
 * Provides resources for custom entities
 * @author smelody
 *
 */
@Resource
public class CustomResource {

	private String id;
	private String type;
	private EntityDefinition entityDefinition;
	
		 
	public CustomResource(String id, EntityDefinition entityDef) {
		this.id = id;
		this.entityDefinition = entityDef;
	}

	@GET
	@Path("/")
	public EntityBody get( ) {

		return entityDefinition.getService().getCustom( id );
	}
	
}
