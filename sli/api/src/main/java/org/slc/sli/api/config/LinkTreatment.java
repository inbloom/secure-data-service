package org.slc.sli.api.config;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;

public class LinkTreatment implements Treatment {
    private static final Logger LOG = LoggerFactory.getLogger(LinkTreatment.class);
    private static final String LINKS = "links";

    private final EntityDefinitionStore defStore;
    
    public LinkTreatment(EntityDefinitionStore defStore) {
        super();
        this.defStore = defStore;
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
    
    private List<EmbeddedLink> findLinks(EntityDefinition defn, String id){
        List<EmbeddedLink> links = new LinkedList<EmbeddedLink>();
        links.add(new EmbeddedLink("self", defn.getType(), defn.getResourceName()+"/"+id));
        return links;
    }
    
}
