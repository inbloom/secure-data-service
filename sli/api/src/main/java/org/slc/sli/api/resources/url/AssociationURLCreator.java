package org.slc.sli.api.resources.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.util.ResourceUtil;

public class AssociationURLCreator extends URLCreator {

	@Override
	public List<EmbeddedLink> getUrls(final UriInfo uriInfo,
			Map<String, String> params) {
		
		// use login data to resolve what type of user and ID of user
        Map<String, String> map = ResourceUtil.resolveUserDataFromSecurityContext();
        
        // remove user's type from map
        String userType = map.remove("userType");
        String userId = map.remove("id");
        String roleType = map.remove("roleType");
        
        // look up known associations to user's type
        EntityDefinition defn = store.lookupByResourceName(userType);
        
        // look up all associations for supplied entity
        Collection<AssociationDefinition> associations = store.getLinked(defn);
        
        // loop through all associations to supplied entity type
        for (AssociationDefinition assoc : associations) {
        	System.out.println(assoc.getSourceEntity().getResourceName());
        }
        
		return null;
	}

}
