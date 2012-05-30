package org.slc.sli.ingestion.transformation;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * An implementation of SmooksEdFi2SLITransformer that has special logic to look up the CourseOffering's Session
 * @author srichards
 */
public class CourseOfferingEdFi2SLITransformer extends SmooksEdFi2SLITransformer {
    private IdNormalizer idNormalizer;
    private EntityConfigFactory entityConfigurations;

    public void resolveReferences(NeutralRecord item, ErrorReport errorReport) {
        Entity entity = new NeutralRecordEntity(item);

        entityConfigurations = getEntityConfigurations();
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        idNormalizer = getIdNormalizer();

        idNormalizer.resolveInternalIds(entity, item.getSourceId(), entityConfig, errorReport);

        /* The JSON uses the session information from the SessionReference to look up a schoolSessionAssociation.
         * If there is no schoolSessionAssociation with that school and the right session information, there will
         * be no ssaId set.  If there's no ssaId set, you won't find a session.  So, you'll never get a *different*
         * session than the one you find to look up the schoolSessionAssociation, but you may have a session there
         * and not have one here.  (Which would cause ingestion of this object to fail.)
         * And that's the correct behavior.
         */
        
        String ssaid = (String) item.getAttributes().get("ssaId");
        Entity matchedSSA = getEntityRepository().findById("schoolSessionAssociation", ssaid);
        Object sessionIdVal = matchedSSA.getBody().get("sessionId");
        ((NeutralRecordEntity) entity).setAttributeField("sessionId", sessionIdVal);
    }
}
