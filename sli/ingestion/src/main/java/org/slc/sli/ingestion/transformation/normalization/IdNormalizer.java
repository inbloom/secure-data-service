package org.slc.sli.ingestion.transformation.normalization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Internal ID resolver.
 *
 * @author okrook
 *
 */
public class IdNormalizer {
    private EntityRepository entityRepository;

    public String resolveInternalId(Ref myCollectionId, ErrorReport errorReport) {
        String cname = myCollectionId.getCollectionName();
        Map<String, String> smap = new HashMap<String, String>();
        for (List<Field> fields : myCollectionId.getChoiceOfFields()) {
            for (Field field: fields) {
                String path = field.getPath();
                FieldValue v = field.getValue();
                if (v.getRef() != null) {
                    resolveInternalId(v.getRef(), errorReport);
                } else {
                    String svalue = v.getSourceValue();
                    if (svalue != null) {
                        smap.put(path, svalue);
                    }
                }
            }
        }

        Iterable<Entity> found = entityRepository.findByPaths(cname, smap, 0, 1);
        if (found == null || !found.iterator().hasNext()) {
            errorReport.error(
                    "Cannot find [" + cname + "] record using fieldPath",
                    IdNormalizer.class);

            return null;
        }

        return found.iterator().next().getEntityId();
    }

    /**
     * @return the entityRepository
     */
    public EntityRepository getEntityRepository() {
        return entityRepository;
    }

    /**
     * @param entityRepository the entityRepository to set
     */
    public void setEntityRepository(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

}

