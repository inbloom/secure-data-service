package org.slc.sli.api.config;

import java.util.LinkedList;
import java.util.List;

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;

public class LinkTreatment implements Treatment {
    // private static final Logger LOG = LoggerFactory.getLogger(LinkTreatment.class);
    private static final String LINKS = "links";
    
    public LinkTreatment(EntityDefinitionStore defStore) {
        super();
    }

    @Override
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        exposed.remove(LINKS);
        return exposed;
    }
    
    @Override
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, String id) {
        stored.put(LINKS, findLinks(defn, id));
        return stored;
    }
    
    private List<EmbeddedLink> findLinks(EntityDefinition defn, String id) {
        List<EmbeddedLink> links = new LinkedList<EmbeddedLink>();
        links.add(new EmbeddedLink("self", defn.getType(), defn.getResourceName() + "/" + id));
        return links;
    }
    
}
