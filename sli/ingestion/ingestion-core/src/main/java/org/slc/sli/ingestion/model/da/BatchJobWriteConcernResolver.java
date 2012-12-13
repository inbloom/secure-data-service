package org.slc.sli.ingestion.model.da;

import com.mongodb.WriteConcern;

import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.WriteConcernResolver;

/**
 * Strategy of WriteConcernResolver for the BatchJob mongotemplate.
 *
 * @author dduran
 *
 */
public class BatchJobWriteConcernResolver implements WriteConcernResolver {

    @Override
    public WriteConcern resolve(MongoAction action) {

        if (action.getEntityClass().getSimpleName().equals("Error")) {
            return WriteConcern.NORMAL;
        }

        return WriteConcern.SAFE;
    }

}
