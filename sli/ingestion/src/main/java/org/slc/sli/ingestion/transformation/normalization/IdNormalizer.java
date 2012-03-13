package org.slc.sli.ingestion.transformation.normalization;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.ingestion.validation.ErrorReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdNormalizer {

    public String resolveInternalId(EntityRepository entityRepository, Ref myCollectionId, ErrorReport errorReport) {
        // TODO Auto-generated method stub
        List<Field> fl;
        String cname = myCollectionId.getCollectionName();
        Map<String, String> smap = new HashMap();
        for (List<Field> list : myCollectionId.getChoiceOfFields()) {

            for (Field aField: list) {
                String path= aField.getPath();
                FieldValue v = aField.getValue();
                if (v.getRef() != null) resolveInternalId(entityRepository, v.getRef(), errorReport);
                else {
                    String svalue = v.getSourceValue();
                    if (svalue != null ) {
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



}

