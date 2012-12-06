package org.slc.sli.api.resources.generic.service.custom;

import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("applicationResourceService")
public class ApplicationResourceService extends DefaultResourceService {

    public static final String AUTHORIZED_ED_ORGS = "authorized_ed_orgs";
    private static final String CREATED_BY = "created_by";

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Override
    protected void addAdditionalCriteria(final NeutralQuery query) {

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (SecurityUtil.hasRight(Right.DEV_APP_CRUD)) { //Developer sees all apps they own
            query.addCriteria(new NeutralCriteria(CREATED_BY, NeutralCriteria.OPERATOR_EQUAL, principal.getExternalId()));
        } else if (!SecurityUtil.hasRight(Right.SLC_APP_APPROVE)) {  //realm admin, sees apps that they are either authorized or could be authorized

            //know this is ugly, but having trouble getting or queries to work
            List<String> idList = new ArrayList<String>();
            NeutralQuery newQuery = new NeutralQuery(new NeutralCriteria(AUTHORIZED_ED_ORGS, NeutralCriteria.OPERATOR_EQUAL, principal.getEdOrgId()));
            Iterable<String> ids = repo.findAllIds("application", newQuery);
            for (String id : ids) {
                idList.add(id);
            }

            newQuery = new NeutralQuery(0);
            newQuery.addCriteria(new NeutralCriteria("allowed_for_all_edorgs", NeutralCriteria.OPERATOR_EQUAL, true));
            newQuery.addCriteria(new NeutralCriteria("authorized_for_all_edorgs", NeutralCriteria.OPERATOR_EQUAL, false));

            ids = repo.findAllIds("application", newQuery);
            for (String id : ids) {
                idList.add(id);
            }
            query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, idList));
        } //else - operator -- sees all apps
    }


}
