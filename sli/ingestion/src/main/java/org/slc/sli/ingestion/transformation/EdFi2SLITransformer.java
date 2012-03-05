package org.slc.sli.ingestion.transformation;

import java.util.List;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.validation.DummyErrorReport;

/**
 * EdFi to SLI data transformation
 *
 * @author okrook
 *
 */
public abstract class EdFi2SLITransformer implements Handler<NeutralRecord, List<? extends Entity>> {

    @Override
    public List<? extends Entity> handle(NeutralRecord item) {
        return handle(item, new DummyErrorReport());
    }

}
