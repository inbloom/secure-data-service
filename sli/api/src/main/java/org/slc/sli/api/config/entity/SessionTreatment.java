package org.slc.sli.api.config.entity;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

/**
 * This treatment addresses DE2739. For Session entities, the current edorg reference is
 * named "schoolId". This is misleading as any edorg type is valid for this reference, not just schools.
 * In addition the field naming is inconsistent with the sli-edfi model and ingestion.
 * 
 * In order to address this issue without making a non-additive API change this treatment is 
 * processing inbound and outbound Session entities to allow Session to have both an EducationOrganization
 * reference and a School reference while maintaining a single field (educationOrganizationReference)
 * in the database. This treatment handles juggling these two fields as outlined below.
 *  
 * ------------------------------------------------------
 * toStored
 * ------------------------------------------------------
 * Possible cases:
 * 	1) Only educationOrganizationReference is provided. Store the provided educationOrganizationReference
 * 	2) Only schoolId is provided. Copy schoolId value into the educationOrganizationReference & 
 * 		remove schoolId field from entity
 * 	3) Both educationOrganizationReference and schoolId are provided. Use the provided
 * 		educationOrganizationReference & remove schoolId field from the entity
 * 	4) Neither educationOrganizationReference or schooId is provided. Allow validation to fail 
 * 		on missing required field. 
 * 
 * ------------------------------------------------------
 * toExposed
 * ------------------------------------------------------
 * Possible cases:
 *  	
 * 
 * @author lloyd.engebretsen@inbloom.org
 *
 */
@Component
public class SessionTreatment implements Treatment {

	@Override
	public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {

		if (defn.getType().equals("session"))
		{
			if(StringUtils.isNotEmpty((String)exposed.get("schoolId")) && 
					StringUtils.isEmpty((String)exposed.get("educationOrganizationReference")))
			{
				//got a schoolId, but no edorg reference so take the school id and store it as edorg
				exposed.put("educationOrganizationReference", exposed.get("schoolId"));
			}
			
			if(exposed.containsKey("schoolId"))
			{
				//remove schoolId field from entity
				exposed.remove("schoolId");
			}
		}
		
		return exposed;
	}

	@Override
	public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity) {
		if (stored.get("entityType").equals("session"))
		{
			//check to see if we have an edOrg reference id from our stored entity
			if(StringUtils.isNotEmpty((String)stored.get("educationOrganizationReference")))
			{
				//looks like we have an edOrg reference so we need to copy it over to schoolId field
				stored.put("schoolId", stored.get("educationOrganizationReference"));
			}
			else if(StringUtils.isNotEmpty((String)stored.get("schoolId")))
			{
				//in this case we have an old reference to the schoolId instead of an edOrg
				stored.put("educationOrganizationReference", stored.get("schoolId"));
				//TODO some way to persist this change to mongo as the document is being updated?
			}
		}
		
		return stored;
	}

}
