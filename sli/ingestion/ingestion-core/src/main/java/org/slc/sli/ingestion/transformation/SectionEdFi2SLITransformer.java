package org.slc.sli.ingestion.transformation;

import org.apache.commons.beanutils.PropertyUtils;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SectionEdFi2SLITransformer extends SmooksEdFi2SLITransformer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SectionEdFi2SLITransformer.class);
    
    private IdNormalizer idNormalizer;
    private EntityConfigFactory entityConfigurations;

    public void resolveReferences(NeutralRecord item, ErrorReport errorReport) {
        Entity entity = new NeutralRecordEntity(item);

        entityConfigurations = getEntityConfigurations();
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        idNormalizer = getIdNormalizer();

        idNormalizer.resolveInternalIds(entity, item.getSourceId(), entityConfig, errorReport);

        //resolve a session id
        String ssaid = (String) item.getAttributes().get("ssaId");
        Entity matchedSSA = getEntityRepository().findById("schoolSessionAssociation", ssaid);
        Object sessionIdVal = matchedSSA.getBody().get("sessionId");
        ((NeutralRecordEntity) entity).setAttributeField("sessionId", sessionIdVal);
        
        //resolve a second session id (for use in the courseOffering resolution)
        String ssaid_courseOffering = (String) item.getAttributes().get("ssaId_courseOffering");
        Entity matchedSSA_courseOffering = getEntityRepository().findById("schoolSessionAssociation", ssaid_courseOffering);
        String sessionIdVal_courseOffering = (String) matchedSSA_courseOffering.getBody().get("sessionId");

        try {
            Object localCourseCode = PropertyUtils.getProperty(item.getAttributes(), 
                    "CourseOfferingReference.CourseOfferingIdentity.LocalCourseCode");
        
            /* The courseOffering reference in section cannot be resolved in IdNormalizer because it requires involves a 
             * sessionId which must be resolved through a schoolSessionAssociation.  Until IdNormalizer supports this kind 
             * of reference resolution, a courseOffering specific query must be constructed in code.
             */
        
            //construct the courseOffering query
            Query courseOfferingQuery = new Query();
            courseOfferingQuery.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(
                    item.getSourceId()));
            courseOfferingQuery.addCriteria(Criteria.where("body.localCourseCode").is(localCourseCode));
            courseOfferingQuery.addCriteria(Criteria.where("body.sessionId").is(sessionIdVal_courseOffering));
        
            Iterable<Entity> foundRecords = getEntityRepository().findByQuery("courseOffering", courseOfferingQuery, 0, 0); 
            
            //should get a single record
            
            Object courseOfferingIdVal = foundRecords.iterator().next().getEntityId();
            ((NeutralRecordEntity) entity).setAttributeField("courseOfferingId", courseOfferingIdVal);
            
        } catch (Exception e) {
            LOG.error("Exception encountered resolving section courseOfferingId ", e);
            errorReport.error("Failed to resolve courseOfferingId in section entity.", this);
        }
    }
}
