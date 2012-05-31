package org.slc.sli.ingestion.transformation;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
import org.slc.sli.ingestion.validation.ErrorReport;

public class SectionEdFi2SLITransformer extends SmooksEdFi2SLITransformer {
    private IdNormalizer idNormalizer;
    private EntityConfigFactory entityConfigurations;

    public void resolveReferences(NeutralRecord item, ErrorReport errorReport) {
        Entity entity = new NeutralRecordEntity(item);

        entityConfigurations = getEntityConfigurations();
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        idNormalizer = getIdNormalizer();

        idNormalizer.resolveInternalIds(entity, item.getSourceId(), entityConfig, errorReport);
        if (errorReport.hasErrors())
        	return;
        String ssaid = (String) item.getAttributes().get("ssaId");
        Entity matchedSSA = getEntityRepository().findById("schoolSessionAssociation", ssaid);
        Object sessionIdVal = matchedSSA.getBody().get("sessionId");
        ((NeutralRecordEntity) entity).setAttributeField("sessionId", sessionIdVal);
    }
}
