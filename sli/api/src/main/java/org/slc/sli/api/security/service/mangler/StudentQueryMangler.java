package org.slc.sli.api.security.service.mangler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

public class StudentQueryMangler implements Mangler {
    
    @Override
    public NeutralQuery mangleQuery(NeutralQuery query, NeutralCriteria securityCriteria) {
        // Is this a  list query or a specific one?
        boolean isList = true;
        NeutralCriteria idCriteria = null;
        for (NeutralCriteria criteria : query.getCriteria()) {
            if (criteria.getKey().equals("_id")) {
                idCriteria = criteria;
                isList = false;
            }
        }
        if (isList) {
            query.addCriteria(securityCriteria);
            return query;
        }
        else {
             Set<String> finalIdSet = new HashSet<String>((Collection) securityCriteria.getValue());
             finalIdSet.retainAll((Collection)idCriteria.getValue());
             if (finalIdSet.size() != 0) {
                 //They're asking for something they CAN see.
                 query.removeCriteria(idCriteria);
                 idCriteria.setValue(finalIdSet);
                 query.addCriteria(idCriteria);
                 return query;
             }
             // This is a 403

        }
        return null;
    }
    
    @Override
    public boolean respondsTo(String type) {
        return type.equals("student");
    }
    
}
