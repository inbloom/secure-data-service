package org.slc.sli.api.resources.url;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.dal.repository.EntityRepository;

/**
 * Handles creating different types of URL sets.
 * @author srupasinghe
 *
 */
public abstract class URLCreator {
	
	@Autowired
    protected EntityDefinitionStore store;
	
	@Autowired
	protected EntityRepository repo;
	
	/**
	 * Returns a list of embedded Urls that matches the given parameters
	 * @param uriInfo
	 * @param params
	 * @return
	 */
	abstract public List<EmbeddedLink> getUrls(final UriInfo uriInfo, Map<String, String> params);

	public void setStore(EntityDefinitionStore store) {
		this.store = store;
	}

	public void setRepo(EntityRepository repo) {
		this.repo = repo;
	}
}
